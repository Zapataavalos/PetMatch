@echo off
setlocal EnableExtensions EnableDelayedExpansion

cd /d "%~dp0"

set "BASE_COMPOSE=docker-compose.yml"
set "LOWRAM_COMPOSE=.docker-compose.lowram.generated.yml"

echo.
echo ============================================================
echo PetMatch - detener y liberar memoria
echo ============================================================
echo.

where docker >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Docker no esta disponible en PATH.
    exit /b 1
)

if exist "%LOWRAM_COMPOSE%" (
    set "COMPOSE_FILES=-f %BASE_COMPOSE% -f %LOWRAM_COMPOSE%"
) else (
    set "COMPOSE_FILES=-f %BASE_COMPOSE%"
)

echo Cerrando frontend Vite si esta usando el puerto 5173...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$procIds = Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique; foreach ($procId in $procIds) { try { Stop-Process -Id $procId -Force -ErrorAction Stop; Write-Host ('Frontend detenido. PID=' + $procId) } catch {} }" >nul 2>nul

echo.
choice /C SD /T 10 /D S /M "S=stop rapido conserva contenedores, D=down limpio conserva datos. Elige [S/D]"
if errorlevel 2 goto compose_down
goto compose_stop

:compose_stop
echo.
echo Deteniendo contenedores sin borrar datos ni contenedores...
docker compose %COMPOSE_FILES% stop
if errorlevel 1 goto fail
goto optional_clean

:compose_down
echo.
echo Bajando contenedores y red, conservando volumenes de datos...
docker compose %COMPOSE_FILES% down --remove-orphans
if errorlevel 1 goto fail
goto optional_clean

:optional_clean
echo.
choice /C SN /T 10 /D N /M "Limpiar cache Docker no usada? No borra volumenes ni bases de datos. [S/N]"
if errorlevel 2 goto done

echo.
echo Limpiando contenedores, imagenes colgantes y cache de build sin usar...
docker container prune -f
docker image prune -f
docker builder prune -f

:done
echo.
echo ============================================================
echo PetMatch detenido. Los datos de MySQL/RabbitMQ se conservaron.
echo ============================================================
exit /b 0

:fail
echo.
echo [ERROR] No se pudo detener correctamente. Revisa Docker Desktop e intenta otra vez.
exit /b 1
