#1
FROM gradle:7.3.3-jdk17 AS BUILD_IMAGE

RUN mkdir /apps
COPY --chown=gradle:gradle . /apps
WORKDIR /apps

RUN gradle clean build

#2
FROM openjdk:17-jdk

COPY --from=BUILD_IMAGE /apps/build/libs/be-pub-building-0.0.1-SNAPSHOT.jar .
COPY startup.sh .

CMD bash startup.sh