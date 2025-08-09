@echo off
title RougeV3 Client
echo Starting RougeV3 Client...

REM Проверяем существование JAR файла
if not exist "build\libs\Dimasik.jar" (
    echo ERROR: Dimasik.jar not found in build\libs\
    echo Please build the project first using: gradlew shadowJar
    pause
    exit /b 1
)

REM Проверяем существование директории run
if not exist "run" (
    echo Creating run directory...
    mkdir run
)

REM Проверяем существование natives
if not exist "natives" (
    echo WARNING: natives directory not found
    echo Please extract natives first using: gradlew extractNatives
    echo.
)

REM JVM аргументы для оптимизации
set JVM_ARGS=-Xms512M -Xmx4G
set JVM_ARGS=%JVM_ARGS% -XX:+UnlockExperimentalVMOptions
set JVM_ARGS=%JVM_ARGS% -XX:+UseG1GC
set JVM_ARGS=%JVM_ARGS% -XX:G1NewSizePercent=20
set JVM_ARGS=%JVM_ARGS% -XX:G1ReservePercent=20
set JVM_ARGS=%JVM_ARGS% -XX:MaxGCPauseMillis=50
set JVM_ARGS=%JVM_ARGS% -XX:G1HeapRegionSize=32M

REM Системные аргументы
set SYSTEM_ARGS=-Djava.library.path=natives
set SYSTEM_ARGS=%SYSTEM_ARGS% -Dfile.encoding=UTF-8
set SYSTEM_ARGS=%SYSTEM_ARGS% -Djava.awt.headless=false

REM Переходим в рабочую директорию
cd /d "%~dp0\run"

echo.
echo JVM Args: %JVM_ARGS%
echo System Args: %SYSTEM_ARGS%
echo Working Directory: %CD%
echo JAR Path: %~dp0build\libs\Dimasik.jar
echo.

REM Запускаем клиент
java %JVM_ARGS% %SYSTEM_ARGS% -jar "%~dp0build\libs\Dimasik.jar"

REM Проверяем код выхода
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Client crashed with exit code %ERRORLEVEL%
    echo Check the logs for more information.
    pause
) else (
    echo.
    echo Client closed successfully.
)

cd /d "%~dp0"
pause