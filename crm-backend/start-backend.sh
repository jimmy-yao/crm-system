#!/bin/bash

echo "=== 启动CRM后端服务 ==="

echo "1. 编译后端项目..."
mvn clean compile -DskipTests

echo "2. 启动用户服务..."
cd crm-user-service
mvn spring-boot:run &
USER_SERVICE_PID=$!
cd ..

echo "3. 等待用户服务启动..."
sleep 20

echo "4. 启动客户服务..."
cd crm-customer-service
mvn spring-boot:run &
CUSTOMER_SERVICE_PID=$!
cd ..

echo "5. 等待客户服务启动..."
sleep 20

echo "6. 启动网关服务..."
cd crm-gateway-service
mvn spring-boot:run &
GATEWAY_SERVICE_PID=$!
cd ..

echo "=== 后端服务启动完成 ==="
echo "服务地址："
echo "- 网关服务: http://localhost:8080"
echo "- API文档: http://localhost:8080/swagger-ui/index.html"
echo "- 健康检查: http://localhost:8080/actuator/health"
echo ""
echo "进程ID："
echo "- 用户服务: $USER_SERVICE_PID"
echo "- 客户服务: $CUSTOMER_SERVICE_PID"
echo "- 网关服务: $GATEWAY_SERVICE_PID"
echo ""
echo "按 Ctrl+C 停止所有后端服务"

# 等待用户中断
trap 'echo "正在停止所有后端服务..."; kill $USER_SERVICE_PID $CUSTOMER_SERVICE_PID $GATEWAY_SERVICE_PID 2>/dev/null; exit' INT

wait