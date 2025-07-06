pipeline{
    agent{
        docker{
            image 'bryan949/poc-agent:0.2.1'
            args '-v /root/.m2:/root/.m2 \
                  -v /root/jenkins/restaurant-resources/:/root/jenkins/restaurant-resources/ \
                  -v /var/run/docker.sock:/var/run/docker.sock \
                  --privileged --env KOPS_STATE_STORE=${KOPS_STATE_STORE} \
                  --env DOCKER_USER=${DOCKER_USER} --env DOCKER_PASS=${DOCKER_PASS}'
            alwaysPull true
        }
    }
    environment{
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
    }
    stages{
        stage('Maven build and test'){
            steps{
                script{
                    def gitOutput = sh(script: '''
                                                set -e
                                                rm -rf RestaurantService || true
                                                git clone ${RESTAURANT_REPO}
                                                cd RestaurantService
                                                GIT_SHA=$(git rev-parse --short HEAD)
                                                MASTER_COMMIT=$(git rev-parse master)
                                                echo "GIT_SHA=${GIT_SHA}"
                                                echo "MASTER_COMMIT=${MASTER_COMMIT}"
                                               ''', returnStdout: true).trim()
                    env.GIT_SHA = (gitOutput =~ /GIT_SHA=([a-f0-9]+)/)[0][1]
                    env.MASTER_COMMIT = (gitOutput =~ /MASTER_COMMIT=([a-f0-9]+)/)[0][1]

                    echo "MASTER_COMMIT: ${env.MASTER_COMMIT}"
                    echo "GIT_SHA: ${env.GIT_SHA}"

                    env.PREV_IMAGE = sh(script: '''
                                                docker pull bryan949/poc-customers:latest >> /dev/null
                                                docker inspect --format='{{index .RepoDigests 0}}' bryan949/poc-customers:latest
                                                ''', returnStdout: true).trim()
                    echo "PREV_IMAGE: ${env.PREV_IMAGE}"
                }
            }
        }
        stage('Build and push docker image'){
            steps{
                unstash 'restaurant-repo'
                sh '''
                    cp /root/jenkins/restaurant-resources/tomcat-users.xml .
                    cp /root/jenkins/restaurant-resources/context.xml .
                    cp /root/jenkins/restaurant-resources/server.xml .

                    docker build -t bryan949/poc-restaurant:${GIT_SHA} .
                    docker tag bryan949/poc-restaurant:${GIT_SHA} bryan949/poc-restaurant:latest
                    docker push bryan949/poc-restaurant:${GIT_SHA}
                    docker push bryan949/poc-restaurant:latest

                    rm tomcat-users.xml
                    rm context.xml
                    rm server.xml
                '''
            }
        }
        stage('Configure cluster connection'){
            steps{
    	        sh '''
	                kops export kubecfg --admin --name poc.k8s.local
	                if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
	                kubectl config set-context --current --namespace rc
	            '''
            }
        }
        stage('Deploy services to cluster - rc namespace'){
            steps{
                sh '''
                    git clone https://github.com/bconnelly/Restaurant-k8s-components.git

                    find Restaurant-k8s-components/restaurant -type f -path ./Restaurant-k8s-components/restaurant -prune -o -name *.yaml -print | while read line; do yq -i '.metadata.namespace = "rc"' $line > /dev/null; done
                    yq -i '.metadata.namespace = "rc"' /root/jenkins/restaurant-resources/poc-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/poc-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl apply -f /root/jenkins/restaurant-resources/poc-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/restaurant
                    kubectl apply -f Restaurant-k8s-components/poc-config.yaml
                    kubectl apply -f Restaurant-k8s-components/mysql-external-service.yaml
                    kubectl get deployment
                    kubectl rollout restart deployment restaurant-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "failed to deploy to rc namespace" && exit 1; fi
                    sleep 3
                '''
                stash includes: 'Restaurant-k8s-components/restaurant/', name: 'k8s-components'
                stash includes: 'Restaurant-k8s-components/tests.py,Restaurant-k8s-components/tests.py', name: 'tests'
            }
        }
        stage('sanity tests'){
            steps{
                unstash 'tests'
                sh '''
                    python Restaurant-k8s-components/tests.py ${RC_LB}
                    exit_status=$?
                    if [ "${exit_status}" -ne 0 ];
                    then
                        echo "Tests failed. Exit status: ${exit_status}"
                        exit ${exit_status}
                    fi
                '''


                withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                    sh '''
                        git checkout rc
                        git checkout master
                        git merge rc
                        git push origin master
                    '''
                }
            }
        }
        stage('deploy to cluster - prod namespace'){
            steps{
                unstash 'k8s-components'

                sh '''
                    find Restaurant-k8s-components/restaurant -type f -path ./Restaurant-k8s-components/restaurant -prune -o -name *.yaml -print | while read line; do yq -i '.metadata.namespace = "prod"' $line > /dev/null; done
                    yq -i '.metadata.namespace = "prod"' /root/jenkins/restaurant-resources/poc-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/poc-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl config set-context --current --namespace prod
                    kubectl apply -f /root/jenkins/restaurant-resources/poc-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/restaurant/
                    kubectl get deployment
                    kubectl rollout restart deployment restaurant-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "PROD FAILURE"; fi
                    sleep 3
                '''
            }
        }
    }
    post{
        failure{
            withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                unstash 'restaurant-repo'
                sh '''
                    echo "Reverting git master branch to previous commit ${MASTER_COMMIT}"
                    git checkout master
                    git reset --hard ${MASTER_COMMIT}
                    git push origin master --force
                '''
            }
        }
        always{
            cleanWs(cleanWhenAborted: true,
                    cleanWhenFailure: true,
                    cleanWhenNotBuilt: true,
                    cleanWhenSuccess: true,
                    cleanWhenUnstable: true,
                    cleanupMatrixParent: true,
                    deleteDirs: true,
                    disableDeferredWipeout: true)

            sh '''
                docker rmi bryan949/poc-restaurant:${GIT_SHA} || true
                docker rmi bryan949/poc-restaurant:latest || true
                docker image prune || true
            '''
        }
    }
}