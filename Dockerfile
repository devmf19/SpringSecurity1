FROM openjdk:8-jdk-alpine
COPY "./target/springSecurity1-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]