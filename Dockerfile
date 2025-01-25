FROM maven:3.8.7-openjdk-22 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package

FROM openjdk:22-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

# Comando para rodar o app
CMD ["java", "-jar", "app.jar"]