@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion

:: Java Edition Manager
:: Functions: Switch, List, Current Version

title Java Edition Manager

:: 颜色定义（如果支持）
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "RESET=[0m"

:: JDK 安装路径配置（根据实际情况修改）
set "JAVA_8_HOME=C:\Program Files\Java\jdk-8"
set "JAVA_11_HOME=C:\Program Files\Java\jdk-11"
set "JAVA_17_HOME=C:\Program Files\Java\jdk-17"
set "JAVA_21_HOME=C:\Program Files\Java\jdk-21"

echo %BLUE%========================================%RESET%
echo %BLUE%        Java Edition Manager v1.0%RESET%
echo %BLUE%========================================%RESET%

:: 解析命令行参数
if "%1"=="" goto :show_menu
if "%1"=="list" goto :list_versions
if "%1"=="current" goto :show_current
if "%1"=="switch" goto :switch_version %2
if "%1"=="help" goto :show_help

:: 直接版本号切换
if "%1"=="8" goto :switch_version 8
if "%1"=="11" goto :switch_version 11
if "%1"=="17" goto :switch_version 17
if "%1"=="21" goto :switch_version 21

echo %RED%Error: Unknown command "%1"%RESET%
goto :show_help

:show_menu
echo Please select an option:
echo.
echo %YELLOW%1.%RESET% Switch to Java 8
echo %YELLOW%2.%RESET% Switch to Java 11
echo %YELLOW%3.%RESET% Switch to Java 17
echo %YELLOW%4.%RESET% Switch to Java 21
echo %YELLOW%5.%RESET% Show current version
echo %YELLOW%6.%RESET% List all versions
echo %YELLOW%0.%RESET% Exit
echo.
set /p choice="Enter option (0-6): "

if "%choice%"=="1" goto :switch_version 8
if "%choice%"=="2" goto :switch_version 11
if "%choice%"=="3" goto :switch_version 17
if "%choice%"=="4" goto :switch_version 21
if "%choice%"=="5" goto :show_current
if "%choice%"=="6" goto :list_versions
if "%choice%"=="0" exit /b 0

echo %RED%Invalid option, please try again%RESET%
echo.
goto :show_menu

:list_versions
echo.
echo %BLUE%Installed Java versions:%RESET%
echo ----------------------------------------

if exist "!JAVA_8_HOME!" (
    echo %GREEN%✓%RESET% Java 8  - !JAVA_8_HOME!
) else (
    echo %RED%✗%RESET% Java 8  - 未安装
)

if exist "!JAVA_11_HOME!" (
    echo %GREEN%✓%RESET% Java 11 - !JAVA_11_HOME!
) else (
    echo %RED%✗%RESET% Java 11 - 未安装
)

if exist "!JAVA_17_HOME!" (
    echo %GREEN%✓%RESET% Java 17 - !JAVA_17_HOME!
) else (
    echo %RED%✗%RESET% Java 17 - 未安装
)

if exist "!JAVA_21_HOME!" (
    echo %GREEN%✓%RESET% Java 21 - !JAVA_21_HOME!
) else (
    echo %RED%✗%RESET% Java 21 - 未安装
)

echo.
goto :end_pause

:show_current
echo.
echo %BLUE%当前 Java 配置:%RESET%
echo ----------------------------------------
echo JAVA_HOME: %JAVA_HOME%
echo.
java -version 2>nul
if errorlevel 1 (
    echo %RED%Java 未正确配置或未安装%RESET%
)
echo.
goto :end_pause

:switch_version
set "version=%2"
if "%version%"=="" set "version=%1"

if "%version%"=="8" (
    set "TARGET_JAVA_HOME=!JAVA_8_HOME!"
    set "VERSION_NAME=Java 8"
) else if "%version%"=="11" (
    set "TARGET_JAVA_HOME=!JAVA_11_HOME!"
    set "VERSION_NAME=Java 11"
) else if "%version%"=="17" (
    set "TARGET_JAVA_HOME=!JAVA_17_HOME!"
    set "VERSION_NAME=Java 17"
) else if "%version%"=="21" (
    set "TARGET_JAVA_HOME=!JAVA_21_HOME!"
    set "VERSION_NAME=Java 21"
) else (
    echo %RED%错误: 不支持的版本 "%version%"%RESET%
    echo 支持的版本: 8, 11, 17, 21
    goto :end_pause
)

:: 检查 JDK 是否存在
if not exist "!TARGET_JAVA_HOME!" (
    echo %RED%错误: !VERSION_NAME! 未安装在 "!TARGET_JAVA_HOME!"%RESET%
    echo 请先安装对应版本的 JDK
    goto :end_pause
)

echo.
echo %YELLOW%正在切换到 !VERSION_NAME!...%RESET%

:: 设置 JAVA_HOME
setx JAVA_HOME "!TARGET_JAVA_HOME!" >nul 2>&1

:: 更新 PATH
call :update_path "!TARGET_JAVA_HOME!"

echo %GREEN%✓ 已成功切换到 !VERSION_NAME!%RESET%
echo %GREEN%✓ JAVA_HOME: !TARGET_JAVA_HOME!%RESET%
echo.

:: 验证
echo %BLUE%验证切换结果:%RESET%
echo ----------------------------------------
"!TARGET_JAVA_HOME!\bin\java" -version 2>nul
if errorlevel 1 (
    echo %RED%验证失败，请检查 JDK 安装%RESET%
) else (
    echo %GREEN%✓ 切换成功！%RESET%
)

echo.
echo %YELLOW%注意: 请重新打开命令行窗口以使环境变量完全生效%RESET%
goto :end_pause

:update_path
set "new_java_home=%~1"

:: 获取当前用户 PATH
for /f "tokens=2*" %%a in ('reg query "HKCU\Environment" /v PATH 2^>nul') do set "USER_PATH=%%b"
if not defined USER_PATH set "USER_PATH="

:: 移除所有 Java bin 路径
set "CLEAN_PATH=!USER_PATH!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-8\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-11\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-17\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-21\bin;=!"

:: 添加新的 Java bin 路径
set "NEW_PATH=%new_java_home%\bin;!CLEAN_PATH!"
setx PATH "!NEW_PATH!" >nul 2>&1

goto :eof

:show_help
echo.
echo %BLUE%使用方法:%RESET%
echo   java-manager.bat [命令] [版本]
echo.
echo %YELLOW%命令:%RESET%
echo   无参数        - 显示交互菜单
echo   8/11/17/21   - 直接切换到指定版本
echo   switch [版本] - 切换到指定版本
echo   current      - 显示当前版本
echo   list         - 列出所有已安装版本
echo   help         - 显示此帮助信息
echo.
echo %YELLOW%示例:%RESET%
echo   java-manager.bat 21
echo   java-manager.bat switch 11
echo   java-manager.bat current
echo   java-manager.bat list
echo.

:end_pause
if "%1"=="" (
    echo.
    pause
    goto :show_menu
) else (
    pause
    exit /b 0
)