#FROM openjdk:17-jdk-slim
#EXPOSE 8080
#
#RUN apt-get update && apt-get install -y maven
#
#COPY . /app
#
#WORKDIR /app
#
#RUN mvn clean package
#
#COPY --chmod=+x ./target/app-0.0.1-SNAPSHOT.jar /app/app.jar
#
#CMD ["java", "-jar", "app.jar"]

FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

