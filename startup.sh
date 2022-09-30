#!/bin/bash -ex
java -jar -Dspring.profiles.active="${SPRING_PROFILE:-default}" be-pub-building-0.0.1-SNAPSHOT.jar
