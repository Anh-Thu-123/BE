# Build nhieu tang: build bang JDK, chay bang JRE de giu image nho - phu hop RAM 512MB tren Render.
FROM eclipse-temurin:21-jdk-alpine AS build
RUN apk add --no-cache curl unzip bash
WORKDIR /app
COPY mvnw pom.xml ./
COPY src ./src
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS run
WORKDIR /app
RUN addgroup -S nagare && adduser -S nagare -G nagare
COPY --from=build /app/target/nagare-api.jar app.jar
USER nagare

ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -XX:MaxRAMPercentage=75 -Xss512k -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
