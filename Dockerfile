FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copier les sources
COPY src ./src

# Compiler le projet (sans Gson, tout est en pur Java)
RUN find src -name "*.java" > sources.txt \
    && javac @sources.txt -d out

FROM eclipse-temurin:17-jdk
WORKDIR /app

# Installer le driver JDBC PostgreSQL via apt
RUN apt-get update \
    && apt-get install -y libpostgresql-jdbc-java \
    && rm -rf /var/lib/apt/lists/*

# Copier les classes compilées
COPY --from=build /app/out ./out

# Exposer le port 8080 pour l'API REST
EXPOSE 8080

# Lancer le serveur REST
CMD ["java", "-cp", "out:/usr/share/java/postgresql.jar", "RestController"]