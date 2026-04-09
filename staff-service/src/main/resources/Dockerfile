FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY staff-service.jar app.jar

ENV SERVER_PORT 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE ${SERVER_PORT}