@echo off
setlocal enabledelayedexpansion

:: Java版本管理器
:: 功能: 切换、列表、当前版本查看

title Java版本管理器

:: 颜色定义
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "RESET=[0m"

:: JDK安装路径配置（根据实际情况修改）
set "JAVA_8_HOME=C:\Program Files\Java\jdk-8"
set "JAVA_11_HOME=C:\Program Files\Java\jdk-11"
set "JAVA_17_HOME=C:\Program Files\Java\jdk-17"
set "JAVA_21_HOME=C:\Program Files\Java\jdk-21"

echo ========================================
echo         Java版本管理器 v1.0
echo ========================================

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

echo 错误: 未知命令 "%1"
goto :show_help

:show_menu
echo 请选择操作:
echo.
echo 1. 切换到 Java 8
echo 2. 切换到 Java 11
echo 3. 切换到 Java 17
echo 4. 切换到 Java 21
echo 5. 查看当前版本
echo 6. 列出所有版本
echo 0. 退出
echo.
set /p choice="请输入选项 (0-6): "

if "%choice%"=="1" goto :switch_version 8
if "%choice%"=="2" goto :switch_version 11
if "%choice%"=="3" goto :switch_version 17
if "%choice%"=="4" goto :switch_version 21
if "%choice%"=="5" goto :show_current
if "%choice%"=="6" goto :list_versions
if "%choice%"=="0" exit /b 0

echo 无效选项，请重新选择
echo.
goto :show_menu

:list_versions
echo.
echo 已安装的Java版本:
echo ----------------------------------------

if exist "!JAVA_8_HOME!" (
    echo [OK] Java 8  - !JAVA_8_HOME!
) else (
    echo [NO] Java 8  - 未安装
)

if exist "!JAVA_11_HOME!" (
    echo [OK] Java 11 - !JAVA_11_HOME!
) else (
    echo [NO] Java 11 - 未安装
)

if exist "!JAVA_17_HOME!" (
    echo [OK] Java 17 - !JAVA_17_HOME!
) else (
    echo [NO] Java 17 - 未安装
)

if exist "!JAVA_21_HOME!" (
    echo [OK] Java 21 - !JAVA_21_HOME!
) else (
    echo [NO] Java 21 - 未安装
)

echo.
goto :end_pause

:show_current
echo.
echo 当前Java配置:
echo ----------------------------------------
echo JAVA_HOME: %JAVA_HOME%
echo.
java -version 2>nul
if errorlevel 1 (
    echo Java未正确配置或未安装
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
    echo 错误: 不支持的版本 "%version%"
    echo 支持的版本: 8, 11, 17, 21
    goto :end_pause
)

:: 检查JDK是否存在
if not exist "!TARGET_JAVA_HOME!" (
    echo 错误: !VERSION_NAME! 未安装在 "!TARGET_JAVA_HOME!"
    echo 请先安装对应版本的JDK
    goto :end_pause
)

echo.
echo 正在切换到 !VERSION_NAME!...

:: 设置JAVA_HOME
setx JAVA_HOME "!TARGET_JAVA_HOME!" >nul 2>&1

:: 更新PATH
call :update_path "!TARGET_JAVA_HOME!"

echo [OK] 已成功切换到 !VERSION_NAME!
echo [OK] JAVA_HOME: !TARGET_JAVA_HOME!
echo.

:: 验证
echo 验证切换结果:
echo ----------------------------------------
"!TARGET_JAVA_HOME!\bin\java" -version 2>nul
if errorlevel 1 (
    echo 验证失败，请检查JDK安装
) else (
    echo [OK] 切换成功！
)

echo.
echo 注意: 请重新打开命令行窗口以使环境变量完全生效
goto :end_pause

:update_path
set "new_java_home=%~1"

:: 获取当前用户PATH
for /f "tokens=2*" %%a in ('reg query "HKCU\Environment" /v PATH 2^>nul') do set "USER_PATH=%%b"
if not defined USER_PATH set "USER_PATH="

:: 移除所有Java bin路径
set "CLEAN_PATH=!USER_PATH!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-8\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-11\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-17\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-21\bin;=!"

:: 添加新的Java bin路径
set "NEW_PATH=%new_java_home%\bin;!CLEAN_PATH!"
setx PATH "!NEW_PATH!" >nul 2>&1

goto :eof

:show_help
echo.
echo 使用方法:
echo   java-manager-cn.bat [命令] [版本]
echo.
echo 命令:
echo   无参数        - 显示交互菜单
echo   8/11/17/21   - 直接切换到指定版本
echo   switch [版本] - 切换到指定版本
echo   current      - 显示当前版本
echo   list         - 列出所有已安装版本
echo   help         - 显示此帮助信息
echo.
echo 示例:
echo   java-manager-cn.bat 21
echo   java-manager-cn.bat switch 11
echo   java-manager-cn.bat current
echo   java-manager-cn.bat list
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