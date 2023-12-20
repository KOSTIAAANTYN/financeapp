FROM openjdk:17-jdk-slim
EXPOSE 8080

RUN apt-get update && apt-get install -y maven

COPY . /app

WORKDIR /app

RUN mvn clean package

CMD ["cp", "./target/app-0.0.1-SNAPSHOT.jar", "app.jar"]
ENTRYPOINT["java","-jar","app.jar"]