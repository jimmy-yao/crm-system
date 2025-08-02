@echo off
echo Starting CRM System with MySQL...

echo.
echo Checking Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo.
echo Starting MySQL and Nacos with Docker Compose...
docker-compose up -d

echo.
echo Waiting for MySQL and Nacos to be ready...
timeout /t 30 /nobreak > nul

echo.
echo Starting CRM Customer Service...
start "CRM Customer Service" cmd /k "cd crm-customer-service && mvn spring-boot:run"

echo Waiting for Customer Service to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting CRM User Service...
start "CRM User Service" cmd /k "cd crm-user-service && mvn spring-boot:run"

echo Waiting for User Service to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting CRM Gateway Service...
start "CRM Gateway Service" cmd /k "cd crm-gateway-service && mvn spring-boot:run"

echo.
echo All services are starting...
echo MySQL: localhost:3306 (root/123456)
echo Nacos: http://localhost:8848/nacos (nacos/nacos)
echo Customer Service: http://localhost:8081
echo User Service: http://localhost:8082
echo Gateway Service: http://localhost:8080
echo API Docs: http://localhost:8080/swagger-ui/index.html
echo.
echo Press any key to stop all services...
pause > nul

echo.
echo Stopping Docker services...
docker-compose down