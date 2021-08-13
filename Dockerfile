# Builder
FROM maven:3.8.1-openjdk-11-slim as builder

WORKDIR /home/project
COPY ./ /home/project

RUN mvn clean verify

#Application
FROM openjdk:11.0.10-jre-buster

RUN mkdir -p /opt/application

COPY --from=builder /home/project/target/sorteio-1.0.0.jar /opt/application/sorteio.jar
ENTRYPOINT ["java","-jar","/opt/application/sorteio.jar"]
