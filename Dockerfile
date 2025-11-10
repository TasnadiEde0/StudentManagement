FROM maven:latest AS build
WORKDIR dir
COPY ./src ./src
COPY ./pom.xml .
RUN mvn compile
RUN mvn package -Dmaven.test.skip

FROM eclipse-temurin:21-jre-jammy
WORKDIR dir
COPY --from=build /dir/target/StudentManagement-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8099
ENTRYPOINT ["java", "-jar", "./app.jar"]
