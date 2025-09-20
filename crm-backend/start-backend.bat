@echo off
echo === 启动CRM后端服务 ===

echo 1. 编译后端项目...
call mvn clean compile -DskipTests

echo 2. 启动用户服务...
start "User Service" cmd /k "cd crm-user-service && mvn spring-boot:run"

echo 3. 等待用户服务启动...
timeout /t 20

echo 4. 启动客户服务...
start "Customer Service" cmd /k "cd crm-customer-service && mvn spring-boot:run"

echo 5. 等待客户服务启动...
timeout /t 20

echo 6. 启动网关服务...
start "Gateway Service" cmd /k "cd crm-gateway-service && mvn spring-boot:run"

echo === 后端服务启动完成 ===
echo 服务地址：
echo - 网关服务: http://localhost:8080
echo - API文档: http://localhost:8080/swagger-ui/index.html
echo - 健康检查: http://localhost:8080/actuator/health

pause