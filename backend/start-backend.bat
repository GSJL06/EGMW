@echo off
echo ========================================
echo    EducaGestor360 Backend Startup
echo ========================================
echo.

echo Verificando Java...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java no está instalado o no está en el PATH
    pause
    exit /b 1
)

echo.
echo Verificando Maven...
mvn -version
if %errorlevel% neq 0 (
    echo ERROR: Maven no está instalado o no está en el PATH
    pause
    exit /b 1
)

echo.
echo Compilando proyecto...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Falló la compilación
    pause
    exit /b 1
)

echo.
echo Iniciando aplicación Spring Boot...
echo Presiona Ctrl+C para detener la aplicación
echo.
mvn spring-boot:run -DskipTests -q

pause
