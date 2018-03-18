FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD /target/ryan-site-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","/app.jar"]