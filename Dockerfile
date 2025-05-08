FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/flames-game-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080
ENV PORT=8080
CMD ["java", "-cp", "app.jar", "com.example.flames.FlamesGameWithDB"] 