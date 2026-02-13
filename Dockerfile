FROM maven:3.9.12-eclipse-temurin-17-alpine AS build
COPY /src /src
COPY pom.xml /
RUN mvn -f /pom.xml clean package

FROM eclipse-temurin:17-alpine
COPY --from=build /target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]

#FROM openjdk:17-jdk-alpine
#COPY target/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]

#FROM maven: 3.1.1-openjdk-17 as builder
#WORKDIR /app
#COPY . /app/.
#RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app
#COPY --from=builder /app/target/*.jar /app/*.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app/*.jar"]