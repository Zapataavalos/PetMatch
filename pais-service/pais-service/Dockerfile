FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/pais-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]