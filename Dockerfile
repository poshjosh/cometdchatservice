# Repo: https://github.com/poshjosh/cometdchatservice
# ---------------------------------------------------
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG APP_PORT
RUN test -z "${APP_PORT}" || EXPOSE "${APP_PORT}" && :
ARG DEPENDENCY_DIR=target/dependency
# for Spring Boot
COPY ${DEPENDENCY_DIR}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY_DIR}/META-INF /app/META-INF
# for Spring Boot
COPY ${DEPENDENCY_DIR}/BOOT-INF/classes /app
COPY start.sh .
RUN chmod +x /start.sh
ARG JAVA_OPTS
ARG MAIN_CLASS
ENTRYPOINT ["/start.sh"]