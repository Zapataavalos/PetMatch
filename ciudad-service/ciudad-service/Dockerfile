FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/ciudad-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8092

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]