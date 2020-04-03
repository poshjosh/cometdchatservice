# Repo: https://github.com/poshjosh/cometdchatservice
# @see: https://spring.io/guides/topicals/spring-boot-docker/
# ---------------------------------------------------
FROM openjdk:8-jdk-alpine
LABEL maintainer="posh.bc@gmail.com"
VOLUME /tmp
COPY start.sh .
# Privileged action that should run before changing to non-root user
# ------------------------------------------------------------------
RUN chmod +x /start.sh
# Create and use non-root user
# ---------------------------
RUN addgroup -S poshjosh && adduser -S poshjosh -G poshjosh
USER poshjosh
ARG DEPENDENCY_DIR=target/dependency
# for Spring Boot
COPY ${DEPENDENCY_DIR}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY_DIR}/META-INF /app/META-INF
# for Spring Boot
COPY ${DEPENDENCY_DIR}/BOOT-INF/classes /app
ARG DEBUG
ENV DEBUG=$DEBUG
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ARG MAIN_CLASS
ENV MAIN_CLASS=$MAIN_CLASS
ENTRYPOINT ["/start.sh"]