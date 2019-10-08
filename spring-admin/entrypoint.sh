#!/usr/bin/env bash

java -Xms256m -Xmx2048m -Djava.net.preferIPv4Stack=true \
-Dserver.port=8080 \
-Dmanagement.server.port=8081 \
-Dmanagement.endpoints.web.exposure.include=* \
-XX:+UseStringDeduplication \
-jar app.jar