@echo off
echo ========================================
echo    启动CRM系统所有模块
echo ========================================

echo.
echo 1. 检查环境...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Java未安装或配置错误
    pause
    exit /b 1
)

echo.
echo 2. 停止现有Docker服务...
docker-compose down

echo.
echo 3. 启动基础服务 (MySQL + Nacos)...
docker-compose up -d mysql nacos

echo.
echo 4. 等待数据库启动...
echo 等待30秒让MySQL完全启动...
timeout /t 30 /nobreak

echo.
echo 5. 初始化数据库...
docker exec -i crm-mysql mysql -uroot -p123456 < database/init.sql
if %ERRORLEVEL% NEQ 0 (
    echo ❌ 数据库初始化失败，请检查MySQL是否正常启动
    pause
    exit /b 1
)

echo.
echo 6. 编译后端项目...
cd crm-backend
call mvn clean compile -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ 后端编译失败
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo 7. 启动后端服务...
echo 启动用户服务...
start "CRM User Service" cmd /k "cd crm-backend/crm-user-service && mvn spring-boot:run"

echo 等待用户服务启动...
timeout /t 25 /nobreak

echo 启动客户服务...
start "CRM Customer Service" cmd /k "cd crm-backend/crm-customer-service && mvn spring-boot:run"

echo 等待客户服务启动...
timeout /t 25 /nobreak

echo 启动网关服务...
start "CRM Gateway Service" cmd /k "cd crm-backend/crm-gateway-service && mvn spring-boot:run"

echo 等待网关服务启动...
timeout /t 30 /nobreak

echo.
echo 8. 检查前端依赖...
cd crm-frontend
if not exist node_modules (
    echo 安装前端依赖...
    call npm install
    if %ERRORLEVEL% NEQ 0 (
        echo ❌ 前端依赖安装失败
        cd ..
        pause
        exit /b 1
    )
)

echo.
echo 9. 启动前端服务...
start "CRM Frontend" cmd /k "npm run dev"
cd ..

echo.
echo ========================================
echo    所有服务启动完成！
echo ========================================
echo.
echo 🌐 访问地址：
echo   前端界面: http://localhost:3000
echo   API文档:  http://localhost:8080/swagger-ui/index.html
echo   健康检查: http://localhost:8080/actuator/health
echo   Nacos:   http://localhost:8848/nacos (nacos/nacos)
echo.
echo 🔑 默认账户：
echo   管理员: admin / 123456
echo   经理:   manager / 123456
echo   用户:   user / 123456
echo.
echo 📊 服务状态检查：
echo   正在检查服务状态...

timeout /t 10 /nobreak

echo.
echo 检查网关服务...
curl -s http://localhost:8080/actuator/health > nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ 网关服务正常
) else (
    echo ⚠️  网关服务可能还在启动中
)

echo.
echo 检查前端服务...
curl -s http://localhost:3000 > nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ 前端服务正常
) else (
    echo ⚠️  前端服务可能还在启动中
)

echo.
echo 🎉 启动完成！请等待所有服务完全启动后访问系统。
pause