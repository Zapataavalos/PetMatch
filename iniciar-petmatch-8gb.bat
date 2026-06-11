@echo off
setlocal EnableExtensions EnableDelayedExpansion

cd /d "%~dp0"

set "BASE_COMPOSE=docker-compose.yml"
set "LOWRAM_COMPOSE=.docker-compose.lowram.generated.yml"
set "FRONTEND_DIR=%CD%\Frontend-Petmach\petmatch-frontend"
set "DO_BUILD=ask"
set "START_FRONTEND=ask"
set "DRY_RUN=0"

for %%A in (%*) do (
    if /I "%%~A"=="build" set "DO_BUILD=1"
    if /I "%%~A"=="--build" set "DO_BUILD=1"
    if /I "%%~A"=="nobuild" set "DO_BUILD=0"
    if /I "%%~A"=="--no-build" set "DO_BUILD=0"
    if /I "%%~A"=="frontend" set "START_FRONTEND=1"
    if /I "%%~A"=="--frontend" set "START_FRONTEND=1"
    if /I "%%~A"=="nofrontend" set "START_FRONTEND=0"
    if /I "%%~A"=="--nofrontend" set "START_FRONTEND=0"
    if /I "%%~A"=="--dry-run" set "DRY_RUN=1"
)

echo.
echo ============================================================
echo PetMatch - inicio optimizado para equipos de 8 GB RAM
echo ============================================================
echo.
echo Recomendado en Docker Desktop:
echo - Memory: 6 GB si es posible
echo - Swap: 2 GB o mas
echo - Cerrar IDEs, Chrome pesado y otros servicios antes de iniciar
echo.

if not exist "%BASE_COMPOSE%" (
    echo [ERROR] No existe %BASE_COMPOSE%. Ejecuta este BAT desde la raiz del proyecto.
    exit /b 1
)

where docker >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Docker no esta disponible en PATH.
    exit /b 1
)

docker compose version >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Docker Compose no esta disponible. Abre Docker Desktop y vuelve a intentar.
    exit /b 1
)

set "COMPOSE_PARALLEL_LIMIT=1"
set "DOCKER_BUILDKIT=1"
set "BUILDKIT_PROGRESS=plain"

call :writeLowRamCompose
if errorlevel 1 goto fail

docker compose -f "%BASE_COMPOSE%" -f "%LOWRAM_COMPOSE%" config --quiet
if errorlevel 1 (
    echo [ERROR] El compose optimizado no es valido.
    goto fail
)

if "%DRY_RUN%"=="1" (
    echo [OK] Dry-run correcto. Se genero %LOWRAM_COMPOSE% y el compose valida bien.
    exit /b 0
)

if /I "%DO_BUILD%"=="ask" (
    echo.
    echo Si es primera vez en este PC, responde S.
    choice /C SN /T 10 /D N /M "Reconstruir imagenes antes de iniciar? [S/N]"
    if errorlevel 2 (set "DO_BUILD=0") else (set "DO_BUILD=1")
)

if /I "%START_FRONTEND%"=="ask" (
    echo.
    choice /C SN /T 10 /D S /M "Levantar tambien el frontend Vite? [S/N]"
    if errorlevel 2 (set "START_FRONTEND=0") else (set "START_FRONTEND=1")
)

if "%DO_BUILD%"=="1" (
    echo.
    echo [1/5] Construyendo imagenes una por una para evitar picos de RAM...
    for %%S in (
        eureka-server
        rol-service
        pais-service
        color-service
        ms-animal-type
        ms-size
        ms-report-type
        ms-report-status
        region-service
        ciudad-service
        usuario-service
        ubicacion-service
        configuracion-usuario-service
        ms-race
        ms-pet
        ms-report
        ms-pet-color
        api-gateway
    ) do (
        echo.
        echo ---- build %%S ----
        docker compose -f "%BASE_COMPOSE%" -f "%LOWRAM_COMPOSE%" build %%S
        if errorlevel 1 goto fail
    )
) else (
    echo.
    echo [1/5] Omitiendo build. Si faltan imagenes, ejecuta: iniciar-petmatch-8gb.bat build
)

echo.
echo [2/5] Levantando infraestructura base...
call :compose up -d --no-build rabbitmq eureka-server
if errorlevel 1 goto fail
call :waitHealthy rabbitmq 80
if errorlevel 1 goto fail
call :waitUrl "Eureka" "http://localhost:8761/actuator/health" 80
if errorlevel 1 goto fail

echo.
echo [3/5] Levantando bases de datos con MySQL reducido...
call :compose up -d --no-build ^
    mysql-animal-type mysql-size mysql-race mysql-pet mysql-pet-color ^
    mysql-report-type mysql-report-status mysql-report mysql-rol mysql-pais ^
    mysql-region mysql-ciudad mysql-color mysql-usuario ^
    mysql-configuracion-usuario mysql-ubicacion
if errorlevel 1 goto fail

for %%S in (
    mysql-animal-type
    mysql-size
    mysql-race
    mysql-pet
    mysql-pet-color
    mysql-report-type
    mysql-report-status
    mysql-report
    mysql-rol
    mysql-pais
    mysql-region
    mysql-ciudad
    mysql-color
    mysql-usuario
    mysql-configuracion-usuario
    mysql-ubicacion
) do (
    call :waitHealthy %%S 80
    if errorlevel 1 goto fail
)

echo.
echo [4/5] Levantando microservicios por tandas...
call :compose up -d --no-build rol-service pais-service color-service ms-animal-type ms-size ms-report-type ms-report-status
if errorlevel 1 goto fail
call :sleep 10

call :compose up -d --no-build region-service ciudad-service usuario-service ubicacion-service configuracion-usuario-service ms-race
if errorlevel 1 goto fail
call :sleep 12

call :compose up -d --no-build ms-pet ms-report ms-pet-color
if errorlevel 1 goto fail
call :sleep 12

call :compose up -d --no-build api-gateway
if errorlevel 1 goto fail
call :waitUrl "API Gateway" "http://localhost:8080/actuator/health" 80
if errorlevel 1 goto fail

echo.
echo [5/5] Estado final de contenedores...
docker compose -f "%BASE_COMPOSE%" -f "%LOWRAM_COMPOSE%" ps

if "%START_FRONTEND%"=="1" (
    if exist "%FRONTEND_DIR%\package.json" (
        echo.
        echo Levantando frontend en una ventana separada...
        if not exist "%FRONTEND_DIR%\node_modules" (
            echo node_modules no existe. Instalando dependencias del frontend...
            pushd "%FRONTEND_DIR%"
            call npm install
            if errorlevel 1 (
                popd
                echo [WARN] No se pudo instalar el frontend. El backend quedo levantado.
                goto done
            )
            popd
        )
        start "PetMatch Frontend" cmd /k pushd "%FRONTEND_DIR%" ^&^& npm run dev -- --host 0.0.0.0
    ) else (
        echo [WARN] No encontre package.json del frontend en %FRONTEND_DIR%.
    )
)

:done
setlocal DisableDelayedExpansion
echo.
echo ============================================================
echo PetMatch iniciado.
echo Backend:  http://localhost:8080/actuator/health
echo Eureka:   http://localhost:8761
echo RabbitMQ: http://localhost:15672  usuario: petmatch clave: petmatch2026
echo Frontend: http://localhost:5173
echo Demo admin: admin@petmatch.cl / Petmatch2026!
echo Demo user:  demo@petmatch.cl / Petmatch2026!
echo ============================================================
endlocal
exit /b 0

:compose
docker compose -f "%BASE_COMPOSE%" -f "%LOWRAM_COMPOSE%" %*
exit /b %ERRORLEVEL%

:waitHealthy
set "SERVICE=%~1"
set "TRIES=%~2"
set "STATUS="
echo Esperando health de %SERVICE%...
for /L %%I in (1,1,%TRIES%) do (
    for /F "usebackq tokens=*" %%H in (`docker inspect -f "{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}" "%SERVICE%" 2^>nul`) do set "STATUS=%%H"
    if /I "!STATUS!"=="healthy" exit /b 0
    call :sleep 3
)
echo [ERROR] %SERVICE% no quedo healthy. Estado: !STATUS!
exit /b 1

:waitUrl
set "NAME=%~1"
set "URL=%~2"
set "TRIES=%~3"
echo Esperando %NAME% en %URL%...
for /L %%I in (1,1,%TRIES%) do (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "try { $r = Invoke-WebRequest -UseBasicParsing -TimeoutSec 4 '%URL%'; if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 500) { exit 0 } } catch { exit 1 }" >nul 2>nul
    if not errorlevel 1 exit /b 0
    call :sleep 3
)
echo [ERROR] %NAME% no respondio a tiempo.
exit /b 1

:sleep
powershell -NoProfile -ExecutionPolicy Bypass -Command "Start-Sleep -Seconds %~1" >nul 2>nul
exit /b 0

:writeLowRamCompose
> "%LOWRAM_COMPOSE%" (
    echo # Auto-generated by iniciar-petmatch-8gb.bat. Safe to delete.
    echo x-low-logging: ^&low-logging
    echo   logging:
    echo     options:
    echo       max-size: "5m"
    echo       max-file: "2"
    echo.
    echo x-rabbit-low: ^&rabbit-low
    echo   ^<^<: *low-logging
    echo   mem_limit: 256m
    echo.
    echo x-mysql-low: ^&mysql-low
    echo   ^<^<: *low-logging
    echo   mem_limit: 320m
    echo   command:
    echo     - --innodb-buffer-pool-size=32M
    echo     - --innodb-log-buffer-size=8M
    echo     - --max-connections=25
    echo     - --performance-schema=OFF
    echo     - --table-definition-cache=400
    echo     - --table-open-cache=256
    echo     - --skip-name-resolve
    echo.
    echo x-java-128: ^&java-128
    echo   ^<^<: *low-logging
    echo   mem_limit: 288m
    echo   environment:
    echo     JAVA_OPTS: "-Xms48m -Xmx128m -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=32m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
    echo     SPRING_JPA_SHOW_SQL: "false"
    echo     SPRINGDOC_API_DOCS_ENABLED: "false"
    echo     SPRINGDOC_SWAGGER_UI_ENABLED: "false"
    echo.
    echo x-java-144: ^&java-144
    echo   ^<^<: *low-logging
    echo   mem_limit: 320m
    echo   environment:
    echo     JAVA_OPTS: "-Xms48m -Xmx128m -XX:MaxMetaspaceSize=144m -XX:ReservedCodeCacheSize=32m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
    echo     SPRING_JPA_SHOW_SQL: "false"
    echo     SPRINGDOC_API_DOCS_ENABLED: "false"
    echo     SPRINGDOC_SWAGGER_UI_ENABLED: "false"
    echo.
    echo x-java-160: ^&java-160
    echo   ^<^<: *low-logging
    echo   mem_limit: 384m
    echo   environment:
    echo     JAVA_OPTS: "-Xms48m -Xmx144m -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=32m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
    echo     SPRING_JPA_SHOW_SQL: "false"
    echo     SPRINGDOC_API_DOCS_ENABLED: "false"
    echo     SPRINGDOC_SWAGGER_UI_ENABLED: "false"
    echo.
    echo x-java-192: ^&java-192
    echo   ^<^<: *low-logging
    echo   mem_limit: 416m
    echo   environment:
    echo     JAVA_OPTS: "-Xms48m -Xmx176m -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=32m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
    echo     SPRING_JPA_SHOW_SQL: "false"
    echo     SPRINGDOC_API_DOCS_ENABLED: "false"
    echo     SPRINGDOC_SWAGGER_UI_ENABLED: "false"
    echo.
    echo x-java-auth: ^&java-auth
    echo   ^<^<: *low-logging
    echo   mem_limit: 448m
    echo   environment:
    echo     JAVA_OPTS: "-Xms64m -Xmx192m -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=32m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
    echo     SPRING_JPA_SHOW_SQL: "false"
    echo     SPRINGDOC_API_DOCS_ENABLED: "false"
    echo     SPRINGDOC_SWAGGER_UI_ENABLED: "false"
    echo.
    echo services:
    echo   rabbitmq:
    echo     ^<^<: *rabbit-low
    echo   mysql-animal-type:
    echo     ^<^<: *mysql-low
    echo   mysql-size:
    echo     ^<^<: *mysql-low
    echo   mysql-race:
    echo     ^<^<: *mysql-low
    echo   mysql-pet:
    echo     ^<^<: *mysql-low
    echo   mysql-pet-color:
    echo     ^<^<: *mysql-low
    echo   mysql-report-type:
    echo     ^<^<: *mysql-low
    echo   mysql-report-status:
    echo     ^<^<: *mysql-low
    echo   mysql-report:
    echo     ^<^<: *mysql-low
    echo   mysql-rol:
    echo     ^<^<: *mysql-low
    echo   mysql-pais:
    echo     ^<^<: *mysql-low
    echo   mysql-region:
    echo     ^<^<: *mysql-low
    echo   mysql-ciudad:
    echo     ^<^<: *mysql-low
    echo   mysql-color:
    echo     ^<^<: *mysql-low
    echo   mysql-usuario:
    echo     ^<^<: *mysql-low
    echo   mysql-configuracion-usuario:
    echo     ^<^<: *mysql-low
    echo   mysql-ubicacion:
    echo     ^<^<: *mysql-low
    echo   eureka-server:
    echo     ^<^<: *java-128
    echo   rol-service:
    echo     ^<^<: *java-144
    echo   pais-service:
    echo     ^<^<: *java-144
    echo   color-service:
    echo     ^<^<: *java-144
    echo   ms-animal-type:
    echo     ^<^<: *java-144
    echo   ms-size:
    echo     ^<^<: *java-144
    echo   ms-report-type:
    echo     ^<^<: *java-144
    echo   ms-report-status:
    echo     ^<^<: *java-144
    echo   region-service:
    echo     ^<^<: *java-160
    echo   ciudad-service:
    echo     ^<^<: *java-160
    echo   ubicacion-service:
    echo     ^<^<: *java-160
    echo   configuracion-usuario-service:
    echo     ^<^<: *java-160
    echo   ms-race:
    echo     ^<^<: *java-160
    echo   ms-pet-color:
    echo     ^<^<: *java-160
    echo   usuario-service:
    echo     ^<^<: *java-auth
    echo   ms-pet:
    echo     ^<^<: *java-192
    echo   ms-report:
    echo     ^<^<: *java-192
    echo   api-gateway:
    echo     ^<^<: *java-192
)
exit /b 0

:fail
echo.
echo ============================================================
echo [ERROR] No se pudo iniciar PetMatch.
echo Revisa el mensaje anterior. Para liberar memoria ejecuta:
echo detener-petmatch.bat
echo ============================================================
exit /b 1
