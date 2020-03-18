# Repo: https://github.com/poshjosh/cometdchatservice
# ---------------------------------------------------
FROM openjdk:8-jdk-alpine
#FROM maven:3-alpine
VOLUME /tmp
ARG SERVER_PORT
RUN test -z "${SERVER_PORT}" || EXPOSE "${SERVER_PORT}" && :
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
COPY start.sh .
ARG JAVA_OPTS
ARG MAIN_CLASS
ENTRYPOINT ["start.sh"]
