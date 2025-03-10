FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/config-server-0.0.1-SNAPSHOT.jar config-server.jar

COPY src/main/resources/configurations /app/configurations

COPY src/main/resources/application.yml /app/application.yml

EXPOSE 8888
CMD ["java", "-jar", "config-server.jar"]
