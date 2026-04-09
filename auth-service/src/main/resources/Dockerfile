FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY auth-service.jar app.jar

ENV SERVER_PORT 8083

ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE ${SERVER_PORT}