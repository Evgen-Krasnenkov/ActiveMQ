FROM gradle:8.1-jdk17-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17.0.1-jdk-slim

EXPOSE 9090

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/ActiveMQ*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
