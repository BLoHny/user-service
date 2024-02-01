FROM maven:3.8.3-openjdk-17 AS GRADLE_BUILD

MAINTAINER rubycpp1225@outlook.kr

COPY build.gradle /build/

COPY src /build/src/

WORKDIR /build/

FROM openjdk:17-jdk

VOLUME /tmp

COPY build/libs/user-service-0.0.1-SNAPSHOT.jar UserService.jar

ENTRYPOINT ["java", "-jar", "UserService.jar"]

