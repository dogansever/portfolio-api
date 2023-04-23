FROM eclipse-temurin:17-jdk-alpine
WORKDIR /var
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/var/app.jar"]