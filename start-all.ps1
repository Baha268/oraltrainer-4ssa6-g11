$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$logDir = Join-Path $repoRoot "logs"
New-Item -ItemType Directory -Path $logDir -Force | Out-Null

# Recherche automatique de Java 17+
$javaExe = "java"
if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
} elseif (Test-Path "C:\Users\user\.jdks\jbrsdk_jcef-17.0.14\bin\java.exe") {
    $javaExe = "C:\Users\user\.jdks\jbrsdk_jcef-17.0.14\bin\java.exe"
}

Write-Host "=== [1/4] Vérification des livrables (.jar et NestJS dist) ===" -ForegroundColor Cyan
$javaServices = @(
    "eureka-server",
    "config-server",
    "api-gateway",
    "user-service",
    "content-service",
    "recording-service",
    "simulation-service",
    "feedback-service",
    "progress-service",
    "classroom-service"
)

foreach ($svc in $javaServices) {
    $jarPath = Join-Path $repoRoot "Backend\$svc\target\$svc-1.0.0.jar"
    if (-not (Test-Path $jarPath)) {
        Write-Host "Compilation de $svc..." -ForegroundColor Yellow
        Push-Location (Join-Path $repoRoot "Backend\$svc")
        try {
            .\mvnw.cmd -B -DskipTests package
        } finally {
            Pop-Location
        }
    }
}

$nestMain = Join-Path $repoRoot "Backend\notification-service\dist\main.js"
if (-not (Test-Path $nestMain)) {
    Write-Host "Compilation de notification-service (NestJS)..." -ForegroundColor Yellow
    $env:NODE_OPTIONS = "--use-system-ca"
    Push-Location (Join-Path $repoRoot "Backend\notification-service")
    try {
        if (-not (Test-Path "node_modules")) { npm install --no-audit --no-fund }
        npm run build
    } finally {
        Pop-Location
    }
}

# Détection conflit port 8081 (ex. Oracle TNSLSNR sous Windows)
$userPortArg = @()
$c8081 = Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($c8081) {
    Write-Host "Port 8081 déjà occupé sur ce PC -> lancement de user-service sur le port 8091" -ForegroundColor Yellow
    $userPortArg = @("--server.port=8091")
}

$jvmOpts = @("-Xms64m", "-Xmx128m", "-XX:+UseSerialGC", "-XX:TieredStopAtLevel=1")

Write-Host "=== [2/4] Démarrage de Eureka Server (:8761) ===" -ForegroundColor Cyan
Start-Process -FilePath $javaExe `
    -ArgumentList ($jvmOpts + @("-jar", "`"$repoRoot\Backend\eureka-server\target\eureka-server-1.0.0.jar`"", "--eureka.server.response-cache-update-interval-ms=3000")) `
    -WorkingDirectory "$repoRoot\Backend\eureka-server" `
    -RedirectStandardOutput "$logDir\eureka-server.log" `
    -RedirectStandardError "$logDir\eureka-server-err.log" `
    -WindowStyle Hidden | Out-Null

for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 1
    try {
        if ((Invoke-WebRequest -Uri "http://localhost:8761" -UseBasicParsing -TimeoutSec 2).StatusCode -eq 200) {
            Write-Host " -> Eureka Server prêt sur http://localhost:8761/" -ForegroundColor Green
            break
        }
    } catch {}
}

Write-Host "=== [3/4] Démarrage des microservices + Config Server + API Gateway ===" -ForegroundColor Cyan
$clients = @(
    @{ Name = "config-server";      Args = @() },
    @{ Name = "user-service";       Args = $userPortArg },
    @{ Name = "content-service";    Args = @() },
    @{ Name = "recording-service";  Args = @() },
    @{ Name = "simulation-service"; Args = @() },
    @{ Name = "feedback-service";   Args = @() },
    @{ Name = "progress-service";   Args = @() },
    @{ Name = "classroom-service";  Args = @() },
    @{ Name = "api-gateway";        Args = @() }
)

foreach ($c in $clients) {
    $s = $c.Name
    $allArgs = $jvmOpts + @("-jar", "`"$repoRoot\Backend\$s\target\$s-1.0.0.jar`"", "--eureka.client.initial-instance-info-replication-interval-seconds=4") + $c.Args
    Start-Process -FilePath $javaExe `
        -ArgumentList $allArgs `
        -WorkingDirectory "$repoRoot\Backend\$s" `
        -RedirectStandardOutput "$logDir\$s.log" `
        -RedirectStandardError "$logDir\$s-err.log" `
        -WindowStyle Hidden | Out-Null
    Write-Host " -> Lancé : $s"
    Start-Sleep -Milliseconds 800
}

$env:PORT = "8088"
$env:EUREKA_ENABLED = "true"
Start-Process -FilePath "node" `
    -ArgumentList "dist/main.js" `
    -WorkingDirectory "$repoRoot\Backend\notification-service" `
    -RedirectStandardOutput "$logDir\notification-service.log" `
    -RedirectStandardError "$logDir\notification-service-err.log" `
    -WindowStyle Hidden | Out-Null
Write-Host " -> Lancé : notification-service (NestJS :8088)"

Write-Host "=== [4/4] Démarrage du Monitoring (Prometheus :9090 & Grafana :3000) ===" -ForegroundColor Cyan
$dockerOk = $false
try {
    cmd /c "docker info >nul 2>&1"
    if ($LASTEXITCODE -eq 0) { $dockerOk = $true }
} catch {}

if ($dockerOk) {
    Push-Location $repoRoot
    try {
        docker compose -f docker-compose.monitoring.yml up -d
        Write-Host " -> Prometheus (:9090) et Grafana (:3000) lancés via Docker !" -ForegroundColor Green
    } finally {
        Pop-Location
    }
} else {
    Write-Host " -> Astuce : ouvrez Docker Desktop puis lancez 'docker compose -f docker-compose.monitoring.yml up -d' pour Prometheus/Grafana." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Stack OralTrain démarrée ! Attendez ~15 secondes puis ouvrez :" -ForegroundColor Green
Write-Host " - Eureka Dashboard : http://localhost:8761/"
Write-Host " - API Gateway      : http://localhost:8090/api/content/hello"
Write-Host " - Prometheus       : http://localhost:9090/targets"
Write-Host " - Grafana          : http://localhost:3000/d/oraltrain-overview"
