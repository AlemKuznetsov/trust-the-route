@echo off
REM Скрипт для открытия проекта в Android Studio
REM Проверяет различные возможные расположения Android Studio

set "PROJECT_PATH=%~dp0"
set "STUDIO_FOUND=0"

REM Проверка стандартных расположений Android Studio
if exist "%LOCALAPPDATA%\Programs\Android\Android Studio\bin\studio64.exe" (
    set "STUDIO_PATH=%LOCALAPPDATA%\Programs\Android\Android Studio\bin\studio64.exe"
    set "STUDIO_FOUND=1"
) else if exist "%ProgramFiles%\Android\Android Studio\bin\studio64.exe" (
    set "STUDIO_PATH=%ProgramFiles%\Android\Android Studio\bin\studio64.exe"
    set "STUDIO_FOUND=1"
) else if exist "%ProgramFiles(x86)%\Android\Android Studio\bin\studio64.exe" (
    set "STUDIO_PATH=%ProgramFiles(x86)%\Android\Android Studio\bin\studio64.exe"
    set "STUDIO_FOUND=1"
)

if %STUDIO_FOUND%==1 (
    echo Opening project in Android Studio...
    echo Project path: %PROJECT_PATH%
    echo Studio path: %STUDIO_PATH%
    start "" "%STUDIO_PATH%" "%PROJECT_PATH%"
) else (
    echo Android Studio not found in standard locations.
    echo Please open Android Studio manually and select this folder:
    echo %PROJECT_PATH%
    echo.
    echo Or install Android Studio from: https://developer.android.com/studio
    pause
)
