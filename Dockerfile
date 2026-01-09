# Étape 1 : Build
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copier le fichier source Java
COPY src/Main.java .

# Télécharger le driver PostgreSQL JDBC
RUN curl -L https://jdbc.postgresql.org/download/postgresql-42.6.0.jar -o postgresql.jar

# Compiler le programme Java
RUN mkdir out && javac -cp postgresql.jar Main.java -d out

# Étape 2 : Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copier les fichiers compilés depuis l'étape de build
COPY --from=build /app/out ./out
COPY --from=build /app/postgresql.jar .

# Lancer le programme avec le driver JDBC
CMD ["java", "-cp", "out:postgresql.jar", "Main"]