FROM openjdk:8

ADD . /root/run
ENV DISCORD_TOKEN=token
# RUN cd /root/run; ./gradlew --console=plain --info shadowJar

RUN mkdir -p /root/run/run

WORKDIR /root/run/run
ENTRYPOINT java -cp /root/run/build/libs/music-bot-1.0-SNAPSHOT-all.jar Main
