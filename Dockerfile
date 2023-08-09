FROM openjdk:8

RUN mkdir -p /root/run
COPY build/libs/music-bot-1.0-SNAPSHOT-all.jar /root/run/application.jar
ENV DISCORD_TOKEN=token

ENTRYPOINT java -cp music-bot-1.0-SNAPSHOT-all.jar Main
