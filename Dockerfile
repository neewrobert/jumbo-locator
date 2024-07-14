# First stage: build the application
FROM maven:3.8-eclipse-temurin-21-alpine as build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Second stage: create the final image
FROM eclipse-temurin:21-alpine
COPY --from=build /app/target/jumbo-locator-*.jar /app/jumbo-locator.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/jumbo-locator.jar"]
