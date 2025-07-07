FROM eclipse-temurin:21.0.6_7-jdk
SHELL ["/bin/bash", "-c"]

ENV TOMCAT_VERSION=11.0.4


ARG TOMCAT_USER
ARG TOMCAT_PASS

RUN useradd -m -U -d /opt/tomcat -s /bin/false tomcat
RUN wget https://archive.apache.org/dist/tomcat/tomcat-11/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz && \
    tar -xf apache-tomcat-$TOMCAT_VERSION.tar.gz -C /opt/tomcat && \
    rm apache-tomcat-$TOMCAT_VERSION.tar.gz && \
    chown -R tomcat: /opt/tomcat
RUN ln -s /opt/tomcat/apache-tomcat-$TOMCAT_VERSION /opt/tomcat/latest

RUN bash -c " cat <<EOF > /opt/tomcat/latest/conf/tomcat-users.xml
<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<tomcat-users xmlns=\"http://tomcat.apache.org/xml\"
              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
              xsi:schemaLocation=\"http://tomcat.apache.org/xml tomcat-users.xsd\"
              version=\"1.0\">
  <role rolename=\"manager-gui\"/>
  <user username=\"${TOMCAT_USER}\" password=\"${TOMCAT_PASS}\" roles=\"manager-gui\"/>
</tomcat-users>
EOF"

COPY RestaurantService.war /opt/tomcat/latest/webapps
COPY context.xml /opt/tomcat/latest/webapps/manager/META-INF
COPY server.xml /opt/tomcat/latest/conf

HEALTHCHECK --interval=30m --timeout=3s CMD curl --fail http://localhost:80 || exit 1

CMD ["bash", "-c", "/opt/tomcat/latest/bin/catalina.sh run"]