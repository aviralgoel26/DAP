# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src

RUN mvn -f backend/pom.xml clean package -DskipTests


# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app/backend

COPY --from=build /build/backend/target/*.jar app.jar

# Seed templates that are part of the application
COPY templates /app/seed-templates

# Runtime storage
RUN mkdir -p /app/persistent/templates /app/persistent/generated

EXPOSE 10000

CMD ["sh", "-c", "if [ -z \"$(ls -A /app/persistent/templates 2>/dev/null)\" ]; then cp -R /app/seed-templates/. /app/persistent/templates/; fi && ln -sfn /app/persistent/templates /app/templates && ln -sfn /app/persistent/generated /app/generated && java -Dserver.port=${PORT:-10000} -jar app.jar"]