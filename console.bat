@echo off
echo ==========================================
echo Menu Console Interactif - JavaBDD
echo ==========================================
echo.
echo Le serveur REST continue sur http://localhost:8080
echo Pour quitter le menu : tapez 8 puis Entree
echo.
echo ==========================================
echo.

REM Verifier si le conteneur tourne
docker ps -q -f name=java_app_console >nul 2>&1
if %errorlevel% neq 0 (
    echo Demarrage du conteneur console...
    docker-compose --profile console up -d app-console
    timeout /t 2 /nobreak >nul
)

REM Lancer le menu dans le conteneur
docker exec -it java_app_console java -cp "out:/usr/share/java/postgresql.jar" backend.Start

echo.
echo ==========================================
echo Menu ferme. Conteneur toujours actif.
echo Relancez ce script pour y retourner.
echo ==========================================
pause