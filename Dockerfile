FROM openjdk:8-jre
RUN mkdir app
ARG JAR_FILE
ADD /target/${JAR_FILE} /app/rh-api.jar
WORKDIR /app
ENTRYPOINT java -jar rh-api.jar