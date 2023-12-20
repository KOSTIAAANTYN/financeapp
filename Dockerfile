FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y maven

COPY . /app

WORKDIR /app

RUN mvn clean package

CMD ["cp", "./target/app-0.0.1-SNAPSHOT.jar", "/app/target/app-0.0.1-SNAPSHOT.jar"]
