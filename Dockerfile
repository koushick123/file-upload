FROM alpine:latest
FROM tomcat:10.1-jre17
RUN rm -rf /usr/local/tomcat/webapps/*
RUN mkdir /usr/local/tomcat/webapps/logs
COPY src/main/resources/log4j2-cloud.xml /usr/local/tomcat/webapps/log4j2.xml
COPY src/main/resources/application-cloud.properties /usr/local/tomcat/webapps/application.properties
#COPY src/main/resources/application-dev.properties /usr/local/tomcat/webapps/application.properties
COPY target/*.war /usr/local/tomcat/webapps/uploadapp.war
ENV JAVA_OPTS="-Dlog4j.configurationFile=/usr/local/tomcat/webapps/log4j2.xml -Dspring.config.location=/usr/local/tomcat/webapps/application.properties"
ENV AWS_JAVA_V1_DISABLE_DEPRECATION_ANNOUNCEMENT="true"
EXPOSE 8080
CMD [ "catalina.sh", "run" ]