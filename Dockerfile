# Étape 1 : Build
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copier le code source
COPY src ./src

# Compiler tous les fichiers .java
RUN find src -name "*.java" > sources.txt \
    && javac @sources.txt -d out

# Étape 2 : Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/out ./out

# Remplace Main par ta classe principale
CMD ["java", "-cp", "out", "Main"]
