FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/product-service-0.0.1-SNAPSHOT.jar product-service.jar
EXPOSE 8050
CMD ["java", "-jar", "product-service.jar"]
