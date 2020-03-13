# Repo: https://github.com/poshjosh/cometdchatservice
# @see https://hub.docker.com/_/maven
# ---------------
# Pull base image
# ---------------
FROM maven:3-alpine
# Speed up Maven a bit
# ---------------
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# Install project dependencies and keep sources
# ---------------
# make app folder
# ---------------
RUN mkdir -p /usr/src/app
#WORKDIR /usr/src/app
# ---------------
# Install maven dependency packages (keep in image).
# Create a pre-packaged repository by using our pom.xml and settings file
# /usr/share/maven/ref/settings-docker.xml which is a settings file that changes
# the local repository to /usr/share/maven/ref/repository.
# Anything in /usr/share/maven/ref/ will be copied on container startup to
# $MAVEN_CONFIG (default = /root/.m2)
# ---------------
COPY pom.xml /usr/src/app
#->RUN mvn -B -f /usr/src/app/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
RUN mvn -B -f /usr/src/app/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:analyze
# ---------------
# Copy other source files (keep in image) - Not applicable to pom projects
# ---------------
#->COPY src /usr/src/app/src



