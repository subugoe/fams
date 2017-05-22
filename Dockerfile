FROM openjdk:8-jdk-alpine
WORKDIR /usr/src/fams
COPY . /usr/src/fams/
RUN ["./gradlew", "shadowJar"]
RUN ["cp", "build/libs/fams-0.3.0-SNAPSHOT-fat.jar", "/usr/bin/fams.jar"]
RUN ["rm", "-rf", "/root/.gradle", ".gradle", "build"]
WORKDIR /usr/bin
CMD ["java", "-jar", "fams.jar"]
