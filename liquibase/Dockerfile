FROM eclipse-temurin:21

RUN mkdir /opt/app
COPY target/liquibase-0.0.1-SNAPSHOT.jar /opt/app/app.jar

CMD ["java", "-jar", "/opt/app/app.jar"]
