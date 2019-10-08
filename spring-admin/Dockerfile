FROM openjdk:11-jre-slim
RUN mkdir /app
COPY build/libs/app.jar /app/app.jar
WORKDIR /app
COPY entrypoint.sh .
RUN chmod 777 entrypoint.sh

ENTRYPOINT [ "/bin/bash", "/app/entrypoint.sh" ]