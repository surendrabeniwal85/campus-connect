FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn/ .mvn/

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw

COPY src/main/java/ src/main/java/
COPY src/main/resources/db/ src/main/resources/db/
COPY src/main/resources/application.yml src/main/resources/application.yml

RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jre AS runtime

WORKDIR /app

ENV PORT=8080

EXPOSE 8080

COPY --from=build --chown=10001:0 \
    /workspace/target/placement-assistant-api-0.0.1-SNAPSHOT.jar \
    /app/app.jar

USER 10001:0

ENTRYPOINT ["sh", "-c", "export SPRING_DATASOURCE_URL=\"${DB_URL:-jdbc:mysql://localhost:3306/placement_assistant}\" SPRING_DATASOURCE_USERNAME=\"${DB_USERNAME:-campus_app}\" SPRING_DATASOURCE_PASSWORD=\"${DB_PASSWORD:-}\"; exec java -jar /app/app.jar --server.port=${PORT:-8080} --spring.jpa.hibernate.ddl-auto=validate --spring.jpa.open-in-view=false"]
