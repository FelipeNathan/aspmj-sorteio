FROM openjdk:11.0.10-jre-buster
COPY target/sorteio-1.0.0.jar sorteio-1.0.0.jar
ENTRYPOINT ["java","-jar","/sorteio-1.0.0.jar"]