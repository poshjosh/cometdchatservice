# Repo: https://github.com/poshjosh/cometdchatservice
# @see https://hub.docker.com/_/maven
# ---------------
# Pull base image
# ---------------
FROM maven:3-alpine
# Speed up Maven a bit
# ---------------
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# Anything in /usr/share/maven/ref/ will be copied on container startup to
# $MAVEN_CONFIG (default = /root/.m2)
# Create a pre-packaged repository by using our pom.xml and settings file
# /usr/share/maven/ref/settings-docker.xml which is a settings file that changes
# the local repository to /usr/share/maven/ref/repository
# COPY pom.xml /tmp/pom.xml
# RUN mvn -B -f /tmp/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
