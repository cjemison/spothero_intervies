#!/usr/bin/env bash

./gradlew clean build

docker build . -t spring-admin:2.1.5