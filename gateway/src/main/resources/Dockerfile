FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY gateway.jar app.jar

ENV SERVER_PORT 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE ${SERVER_PORT}