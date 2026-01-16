@echo off
echo ==========================================
echo NETTOYAGE ET RECONSTRUCTION COMPLETE
echo ==========================================

echo.
echo 1. Arret de tous les conteneurs...
docker-compose down

echo.
echo 2. Suppression des images...
for /f "tokens=3" %%i in ('docker images ^| findstr "java_app"') do docker rmi -f %%i

echo.
echo 3. Nettoyage du cache Docker...
docker builder prune -a -f

echo.
echo 4. Reconstruction SANS cache...
docker-compose build --no-cache --progress=plain

echo.
echo 5. Redemarrage des services...
docker-compose up -d db app-rest
timeout /t 3 /nobreak >nul
docker-compose --profile console up -d app-console

echo.
echo ==========================================
echo TERMINE ! Vous pouvez lancer console.bat
echo ==========================================
pause