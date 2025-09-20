#!/bin/bash

# JWT认证测试脚本
echo "=== CRM系统JWT认证测试 ==="

BASE_URL="http://localhost:8080"

echo "1. 测试登录接口..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }')

echo "登录响应: $LOGIN_RESPONSE"

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ 登录失败，无法获取token"
  exit 1
fi

echo "✅ 登录成功，获取到token: ${TOKEN:0:50}..."

echo ""
echo "2. 测试需要认证的接口..."

# 测试获取用户列表
echo "测试获取用户列表..."
USERS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/users" \
  -H "Authorization: Bearer $TOKEN")

echo "用户列表响应: $USERS_RESPONSE"

# 测试获取客户列表
echo ""
echo "测试获取客户列表..."
CUSTOMERS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/customers" \
  -H "Authorization: Bearer $TOKEN")

echo "客户列表响应: $CUSTOMERS_RESPONSE"

echo ""
echo "3. 测试无token访问..."
NO_TOKEN_RESPONSE=$(curl -s -X GET "$BASE_URL/api/users")
echo "无token访问响应: $NO_TOKEN_RESPONSE"

echo ""
echo "4. 测试错误token访问..."
WRONG_TOKEN_RESPONSE=$(curl -s -X GET "$BASE_URL/api/users" \
  -H "Authorization: Bearer invalid_token")
echo "错误token访问响应: $WRONG_TOKEN_RESPONSE"

echo ""
echo "=== 测试完成 ==="