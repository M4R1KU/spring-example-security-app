FROM openjdk:8-jre-alpine

VOLUME /tmp
ADD build/libs/security-app-*.jar springApp.jar
RUN sh -c 'touch /springApp.jar'
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /springApp.jar"]