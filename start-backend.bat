@echo off
set projectRoot=%~dp0

start "api-platform-backend" cmd /k "cd /d "%projectRoot%api-platform-backend" && mvn clean compile spring-boot:run"
timeout /t 2 /nobreak >nul
start "api-platform-gateway" cmd /k "cd /d "%projectRoot%api-platform-cloud\api-platform-gateway" && mvn clean compile spring-boot:run"
timeout /t 2 /nobreak >nul
start "api-platform-mock-api" cmd /k "cd /d "%projectRoot%api-platform-cloud\api-platform-mock-api" && mvn clean compile spring-boot:run"
