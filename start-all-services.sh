#!/bin/bash

echo "========================================"
echo "    启动CRM系统所有模块"
echo "========================================"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 错误处理函数
handle_error() {
    echo -e "${RED}❌ $1${NC}"
    exit 1
}

# 成功信息函数
success_msg() {
    echo -e "${GREEN}✅ $1${NC}"
}

# 警告信息函数
warning_msg() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# 信息函数
info_msg() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

echo
echo "1. 检查环境..."
if ! command -v java &> /dev/null; then
    handle_error "Java未安装"
fi

if ! command -v mvn &> /dev/null; then
    handle_error "Maven未安装"
fi

if ! command -v docker &> /dev/null; then
    handle_error "Docker未安装"
fi

if ! command -v node &> /dev/null; then
    handle_error "Node.js未安装"
fi

java -version
success_msg "环境检查通过"

echo
echo "2. 停止现有Docker服务..."
docker-compose down

echo
echo "3. 启动基础服务 (MySQL + Nacos)..."
docker-compose up -d mysql nacos
if [ $? -ne 0 ]; then
    handle_error "Docker服务启动失败"
fi

echo
echo "4. 等待数据库启动..."
info_msg "等待30秒让MySQL完全启动..."
sleep 30

echo
echo "5. 初始化数据库..."
docker exec -i crm-mysql mysql -uroot -p123456 < database/init.sql
if [ $? -ne 0 ]; then
    handle_error "数据库初始化失败，请检查MySQL是否正常启动"
fi
success_msg "数据库初始化完成"

echo
echo "6. 编译后端项目..."
cd crm-backend
mvn clean compile -DskipTests -q
if [ $? -ne 0 ]; then
    handle_error "后端编译失败"
fi
cd ..
success_msg "后端编译完成"

echo
echo "7. 启动后端服务..."

# 启动用户服务
echo "启动用户服务..."
cd crm-backend/crm-user-service
mvn spring-boot:run > ../../logs/user-service.log 2>&1 &
USER_SERVICE_PID=$!
cd ../..
info_msg "用户服务启动中... PID: $USER_SERVICE_PID"

# 等待用户服务启动
sleep 25

# 启动客户服务
echo "启动客户服务..."
cd crm-backend/crm-customer-service
mvn spring-boot:run > ../../logs/customer-service.log 2>&1 &
CUSTOMER_SERVICE_PID=$!
cd ../..
info_msg "客户服务启动中... PID: $CUSTOMER_SERVICE_PID"

# 等待客户服务启动
sleep 25

# 启动网关服务
echo "启动网关服务..."
cd crm-backend/crm-gateway-service
mvn spring-boot:run > ../../logs/gateway-service.log 2>&1 &
GATEWAY_SERVICE_PID=$!
cd ../..
info_msg "网关服务启动中... PID: $GATEWAY_SERVICE_PID"

# 等待网关服务启动
sleep 30

echo
echo "8. 检查前端依赖..."
cd crm-frontend
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
    if [ $? -ne 0 ]; then
        handle_error "前端依赖安装失败"
    fi
    success_msg "前端依赖安装完成"
fi

echo
echo "9. 启动前端服务..."
npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..
info_msg "前端服务启动中... PID: $FRONTEND_PID"

echo
echo "========================================"
echo "    所有服务启动完成！"
echo "========================================"
echo
echo "🌐 访问地址："
echo "  前端界面: http://localhost:3000"
echo "  API文档:  http://localhost:8080/swagger-ui/index.html"
echo "  健康检查: http://localhost:8080/actuator/health"
echo "  Nacos:   http://localhost:8848/nacos (nacos/nacos)"
echo
echo "🔑 默认账户："
echo "  管理员: admin / 123456"
echo "  经理:   manager / 123456"
echo "  用户:   user / 123456"
echo
echo "📊 进程ID："
echo "  用户服务: $USER_SERVICE_PID"
echo "  客户服务: $CUSTOMER_SERVICE_PID"
echo "  网关服务: $GATEWAY_SERVICE_PID"
echo "  前端服务: $FRONTEND_PID"
echo

# 等待一段时间后检查服务状态
sleep 10

echo "📊 服务状态检查："

# 检查网关服务
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    success_msg "网关服务正常"
else
    warning_msg "网关服务可能还在启动中"
fi

# 检查前端服务
if curl -s http://localhost:3000 > /dev/null 2>&1; then
    success_msg "前端服务正常"
else
    warning_msg "前端服务可能还在启动中"
fi

echo
echo "🎉 启动完成！请等待所有服务完全启动后访问系统。"
echo
echo "💡 提示："
echo "  - 查看日志: tail -f logs/*.log"
echo "  - 停止服务: kill $USER_SERVICE_PID $CUSTOMER_SERVICE_PID $GATEWAY_SERVICE_PID $FRONTEND_PID"
echo "  - 停止Docker: docker-compose down"

# 等待用户中断
trap 'echo "正在停止所有服务..."; kill $USER_SERVICE_PID $CUSTOMER_SERVICE_PID $GATEWAY_SERVICE_PID $FRONTEND_PID 2>/dev/null; docker-compose down; exit' INT

echo
echo "按 Ctrl+C 停止所有服务"
wait