$ErrorActionPreference = "Continue"
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Arrêt des services OralTrain..." -ForegroundColor Yellow
# Ne jamais toucher aux ports 8080 / 8081 (utilisés par Oracle TNSLSNR sous Windows)
$ports = @(8761, 8888, 8090, 8091, 8082, 8083, 8084, 8085, 8086, 8088, 8089)
$conns = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object { $ports -contains $_.LocalPort }
foreach ($c in $conns) {
    if ($c.OwningProcess -gt 4) {
        Stop-Process -Id $c.OwningProcess -Force -ErrorAction SilentlyContinue
        Write-Host " -> Port $($c.LocalPort) libéré (PID $($c.OwningProcess))"
    }
}

try {
    cmd /c "docker info >nul 2>&1"
    if ($LASTEXITCODE -eq 0) {
        Push-Location $repoRoot
        docker compose -f docker-compose.monitoring.yml stop
        Pop-Location
    }
} catch {}

Write-Host "Tous les services OralTrain ont été arrêtés." -ForegroundColor Green
