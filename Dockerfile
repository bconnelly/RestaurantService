FROM eclipse-temurin:21.0.6_7-jdk
SHELL ["/bin/bash", "-c"]

ENV TOMCAT_VERSION=11.0.4

ARG TOMCAT_USER
ARG TOMCAT_PASS

# Create tomcat user
RUN useradd -m -U -d /opt/tomcat -s /bin/false tomcat

# Download and extract Tomcat
RUN wget https://archive.apache.org/dist/tomcat/tomcat-11/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar -xf apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt/tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    ln -s /opt/tomcat/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat/latest && \
    chown -R tomcat: /opt/tomcat

# Copy WAR file and config files with ownership and permissions
COPY --chown=tomcat:tomcat RestaurantService.war /opt/tomcat/latest/webapps/
COPY --chown=tomcat:tomcat context.xml /opt/tomcat/latest/webapps/manager/META-INF/
COPY --chown=tomcat:tomcat server.xml /opt/tomcat/latest/conf/

# Ensure proper read permissions
RUN chmod 644 /opt/tomcat/latest/conf/server.xml && \
    chmod 644 /opt/tomcat/latest/webapps/manager/META-INF/context.xml

# Copy and prepare entrypoint script
COPY --chown=tomcat:tomcat entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

USER tomcat

HEALTHCHECK --interval=30m --timeout=3s CMD curl --fail http://localhost:80 || exit 1

ENTRYPOINT ["/entrypoint.sh"]
