# Étape 1 : Build
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copier le code source
COPY src ./src

# Compiler tous les fichiers .java dans src/
RUN find src -name "*.java" > sources.txt \
    && javac @sources.txt -d out

# Étape 2 : Execution
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/out ./out

# ⚠️ Remplacer Main par le nom de ta classe qui contient Le main()
CMD ["java", "-cp", "out", "Main"]
