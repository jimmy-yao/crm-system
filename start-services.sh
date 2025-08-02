#!/bin/bash

echo "Starting CRM System with MySQL..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "Docker is not running. Please start Docker first."
    exit 1
fi

echo ""
echo "Starting MySQL and Nacos with Docker Compose..."
docker-compose up -d

echo "Waiting for MySQL and Nacos to be ready..."
sleep 30

echo ""
echo "Starting CRM Customer Service..."
cd crm-customer-service
mvn spring-boot:run &
CUSTOMER_SERVICE_PID=$!
cd ..

echo "Waiting for Customer Service to start..."
sleep 10

echo ""
echo "Starting CRM User Service..."
cd crm-user-service
mvn spring-boot:run &
USER_SERVICE_PID=$!
cd ..

echo "Waiting for User Service to start..."
sleep 10

echo ""
echo "Starting CRM Gateway Service..."
cd crm-gateway-service
mvn spring-boot:run &
GATEWAY_SERVICE_PID=$!
cd ..

echo ""
echo "All services are starting..."
echo "MySQL: localhost:3306 (root/123456)"
echo "Nacos: http://localhost:8848/nacos (nacos/nacos)"
echo "Customer Service: http://localhost:8081"
echo "User Service: http://localhost:8082"
echo "Gateway Service: http://localhost:8080"
echo "API Docs: http://localhost:8080/swagger-ui/index.html"
echo ""
echo "PIDs: CustomerService=$CUSTOMER_SERVICE_PID, UserService=$USER_SERVICE_PID, GatewayService=$GATEWAY_SERVICE_PID"
echo "Press Ctrl+C to stop all services"

# 等待用户中断
trap "echo 'Stopping services...'; kill $CUSTOMER_SERVICE_PID $USER_SERVICE_PID $GATEWAY_SERVICE_PID; docker-compose down; exit" INT
wait