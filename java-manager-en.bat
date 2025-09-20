@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion

:: Java Version Manager
:: Functions: Switch, List, Current Version

title Java Version Manager

:: Color definitions (if supported)
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "RESET=[0m"

:: JDK installation paths (modify according to your setup)
set "JAVA_8_HOME=C:\Program Files\Java\jdk-8"
set "JAVA_11_HOME=C:\Program Files\Java\jdk-11"
set "JAVA_17_HOME=C:\Program Files\Java\jdk-17"
set "JAVA_21_HOME=C:\Program Files\Java\jdk-21"

echo %BLUE%========================================%RESET%
echo %BLUE%        Java Version Manager v1.0%RESET%
echo %BLUE%========================================%RESET%

:: Parse command line arguments
if "%1"=="" goto :show_menu
if "%1"=="list" goto :list_versions
if "%1"=="current" goto :show_current
if "%1"=="switch" goto :switch_version %2
if "%1"=="help" goto :show_help

:: Direct version switching
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
    echo %GREEN%[OK]%RESET% Java 8  - !JAVA_8_HOME!
) else (
    echo %RED%[NO]%RESET% Java 8  - Not installed
)

if exist "!JAVA_11_HOME!" (
    echo %GREEN%[OK]%RESET% Java 11 - !JAVA_11_HOME!
) else (
    echo %RED%[NO]%RESET% Java 11 - Not installed
)

if exist "!JAVA_17_HOME!" (
    echo %GREEN%[OK]%RESET% Java 17 - !JAVA_17_HOME!
) else (
    echo %RED%[NO]%RESET% Java 17 - Not installed
)

if exist "!JAVA_21_HOME!" (
    echo %GREEN%[OK]%RESET% Java 21 - !JAVA_21_HOME!
) else (
    echo %RED%[NO]%RESET% Java 21 - Not installed
)

echo.
goto :end_pause

:show_current
echo.
echo %BLUE%Current Java configuration:%RESET%
echo ----------------------------------------
echo JAVA_HOME: %JAVA_HOME%
echo.
java -version 2>nul
if errorlevel 1 (
    echo %RED%Java is not properly configured or installed%RESET%
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
    echo %RED%Error: Unsupported version "%version%"%RESET%
    echo Supported versions: 8, 11, 17, 21
    goto :end_pause
)

:: Check if JDK exists
if not exist "!TARGET_JAVA_HOME!" (
    echo %RED%Error: !VERSION_NAME! is not installed at "!TARGET_JAVA_HOME!"%RESET%
    echo Please install the corresponding JDK version first
    goto :end_pause
)

echo.
echo %YELLOW%Switching to !VERSION_NAME!...%RESET%

:: Set JAVA_HOME
setx JAVA_HOME "!TARGET_JAVA_HOME!" >nul 2>&1

:: Update PATH
call :update_path "!TARGET_JAVA_HOME!"

echo %GREEN%[OK] Successfully switched to !VERSION_NAME!%RESET%
echo %GREEN%[OK] JAVA_HOME: !TARGET_JAVA_HOME!%RESET%
echo.

:: Verify
echo %BLUE%Verifying switch result:%RESET%
echo ----------------------------------------
"!TARGET_JAVA_HOME!\bin\java" -version 2>nul
if errorlevel 1 (
    echo %RED%Verification failed, please check JDK installation%RESET%
) else (
    echo %GREEN%[OK] Switch successful!%RESET%
)

echo.
echo %YELLOW%Note: Please reopen command prompt for environment variables to take full effect%RESET%
goto :end_pause

:update_path
set "new_java_home=%~1"

:: Get current user PATH
for /f "tokens=2*" %%a in ('reg query "HKCU\Environment" /v PATH 2^>nul') do set "USER_PATH=%%b"
if not defined USER_PATH set "USER_PATH="

:: Remove all Java bin paths
set "CLEAN_PATH=!USER_PATH!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-8\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-11\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-17\bin;=!"
set "CLEAN_PATH=!CLEAN_PATH:C:\Program Files\Java\jdk-21\bin;=!"

:: Add new Java bin path
set "NEW_PATH=%new_java_home%\bin;!CLEAN_PATH!"
setx PATH "!NEW_PATH!" >nul 2>&1

goto :eof

:show_help
echo.
echo %BLUE%Usage:%RESET%
echo   java-manager-en.bat [command] [version]
echo.
echo %YELLOW%Commands:%RESET%
echo   (no args)     - Show interactive menu
echo   8/11/17/21   - Switch to specified version directly
echo   switch [ver] - Switch to specified version
echo   current      - Show current version
echo   list         - List all installed versions
echo   help         - Show this help information
echo.
echo %YELLOW%Examples:%RESET%
echo   java-manager-en.bat 21
echo   java-manager-en.bat switch 11
echo   java-manager-en.bat current
echo   java-manager-en.bat list
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