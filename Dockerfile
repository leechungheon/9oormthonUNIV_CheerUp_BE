# 자바 버전 17
FROM openjdk:17
LABEL authors="leech"

COPY build/libs/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]