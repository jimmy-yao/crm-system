#!/bin/bash

echo "=== Kiro IDE Java 21 环境检查 ==="

# 检查Java版本
echo "1. 检查Java版本..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "Java版本: $JAVA_VERSION"
    
    # 检查是否为Java 21
    if [[ $JAVA_VERSION == 21* ]]; then
        echo "✅ Java 21 已安装"
    else
        echo "❌ 需要Java 21，当前版本: $JAVA_VERSION"
        echo "请安装Java 21并设置JAVA_HOME"
    fi
else
    echo "❌ Java未安装"
fi

# 检查JAVA_HOME
echo ""
echo "2. 检查JAVA_HOME..."
if [ -n "$JAVA_HOME" ]; then
    echo "JAVA_HOME: $JAVA_HOME"
    if [ -f "$JAVA_HOME/bin/java" ]; then
        JAVA_HOME_VERSION=$("$JAVA_HOME/bin/java" -version 2>&1 | head -n 1 | cut -d'"' -f2)
        echo "JAVA_HOME版本: $JAVA_HOME_VERSION"
        
        if [[ $JAVA_HOME_VERSION == 21* ]]; then
            echo "✅ JAVA_HOME指向Java 21"
        else
            echo "❌ JAVA_HOME未指向Java 21"
        fi
    else
        echo "❌ JAVA_HOME路径无效"
    fi
else
    echo "❌ JAVA_HOME未设置"
fi

# 检查Maven
echo ""
echo "3. 检查Maven..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo "Maven版本: $MVN_VERSION"
    echo "✅ Maven已安装"
else
    echo "❌ Maven未安装"
fi

# 检查项目配置
echo ""
echo "4. 检查Kiro项目配置..."
if [ -f ".kiro/settings/project.json" ]; then
    echo "✅ 项目配置文件存在"
else
    echo "❌ 项目配置文件缺失"
fi

if [ -f ".kiro/settings/java.json" ]; then
    echo "✅ Java配置文件存在"
else
    echo "❌ Java配置文件缺失"
fi

echo ""
echo "=== 环境检查完成 ==="