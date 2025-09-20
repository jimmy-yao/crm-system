@echo off
setlocal enabledelayedexpansion

:: JDK 版本切换脚本
:: 使用方法: switch-java.bat [版本号]
:: 例如: switch-java.bat 8 或 switch-java.bat 21

echo ========================================
echo        JDK 版本切换工具
echo ========================================

:: 如果没有提供参数，显示帮助信息
if "%1"=="" (
    echo 使用方法: switch-java.bat [版本号]
    echo.
    echo 支持的版本:
    echo   8  - Java 8
    echo   11 - Java 11
    echo   17 - Java 17
    echo   21 - Java 21
    echo.
    echo 当前 Java 版本:
    java -version 2>nul
    if errorlevel 1 (
        echo Java 未安装或未配置
    )
    echo.
    pause
    exit /b 0
)

:: 设置 JDK 路径变量
set "JAVA_8_HOME=C:\Program Files\Java\jdk-8"
set "JAVA_11_HOME=C:\Program Files\Java\jdk-11"
set "JAVA_17_HOME=C:\Program Files\Java\jdk-17"
set "JAVA_21_HOME=C:\Program Files\Java\jdk-21"

:: 根据输入参数选择对应的 JDK 版本
if "%1"=="8" (
    set "TARGET_JAVA_HOME=!JAVA_8_HOME!"
    set "VERSION_NAME=Java 8"
) else if "%1"=="11" (
    set "TARGET_JAVA_HOME=!JAVA_11_HOME!"
    set "VERSION_NAME=Java 11"
) else if "%1"=="17" (
    set "TARGET_JAVA_HOME=!JAVA_17_HOME!"
    set "VERSION_NAME=Java 17"
) else if "%1"=="21" (
    set "TARGET_JAVA_HOME=!JAVA_21_HOME!"
    set "VERSION_NAME=Java 21"
) else (
    echo 错误: 不支持的版本 "%1"
    echo 支持的版本: 8, 11, 17, 21
    pause
    exit /b 1
)

:: 检查目标 JDK 是否存在
if not exist "!TARGET_JAVA_HOME!" (
    echo 错误: !VERSION_NAME! 未安装在 "!TARGET_JAVA_HOME!"
    echo 请检查 JDK 安装路径是否正确
    pause
    exit /b 1
)

:: 设置环境变量
echo 正在切换到 !VERSION_NAME!...
setx JAVA_HOME "!TARGET_JAVA_HOME!" >nul

:: 更新 PATH（移除旧的 Java 路径，添加新的）
for /f "tokens=2*" %%a in ('reg query "HKCU\Environment" /v PATH 2^>nul') do set "USER_PATH=%%b"
if not defined USER_PATH set "USER_PATH="

:: 移除所有可能的 Java bin 路径
set "NEW_PATH=!USER_PATH!"
set "NEW_PATH=!NEW_PATH:C:\Program Files\Java\jdk-8\bin;=!"
set "NEW_PATH=!NEW_PATH:C:\Program Files\Java\jdk-11\bin;=!"
set "NEW_PATH=!NEW_PATH:C:\Program Files\Java\jdk-17\bin;=!"
set "NEW_PATH=!NEW_PATH:C:\Program Files\Java\jdk-21\bin;=!"

:: 添加新的 Java bin 路径到开头
set "NEW_PATH=!TARGET_JAVA_HOME!\bin;!NEW_PATH!"
setx PATH "!NEW_PATH!" >nul

echo.
echo ✓ 已成功切换到 !VERSION_NAME!
echo ✓ JAVA_HOME: !TARGET_JAVA_HOME!
echo.

:: 验证切换结果
echo 验证当前 Java 版本:
echo ----------------------------------------
"!TARGET_JAVA_HOME!\bin\java" -version

echo.
echo 注意: 请重新打开命令行窗口以使环境变量生效
echo ========================================
pause