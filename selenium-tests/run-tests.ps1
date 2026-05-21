# Runs Selenium tests using IntelliJ's bundled Maven.
# Usage:   powershell -File run-tests.ps1
#          OR right-click -> Run with PowerShell

$ErrorActionPreference = "Stop"

# 1. Find a usable mvn.cmd
$mvnCandidates = @(
    "C:\Users\2484977\AppData\Local\Programs\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd",
    "C:\Users\2484977\.m2\wrapper\dists\apache-maven-3.9.14-bin\1cb7fhup6b5n3bed6kckbrnspv\apache-maven-3.9.14\bin\mvn.cmd"
)

$mvn = $null
foreach ($candidate in $mvnCandidates) {
    if (Test-Path $candidate) { $mvn = $candidate; break }
}

# Fallback: try PATH
if (-not $mvn) {
    $sysMvn = Get-Command mvn -ErrorAction SilentlyContinue
    if ($sysMvn) { $mvn = $sysMvn.Source }
}

if (-not $mvn) {
    Write-Host "ERROR: Maven not found. Install Maven or update the path in this script." -ForegroundColor Red
    exit 1
}

Write-Host "Using Maven: $mvn" -ForegroundColor Cyan
Write-Host ""

# 2. Make sure backend (8081) and frontend (4200) are reachable
function Test-Port($port) {
    try {
        $tcp = New-Object System.Net.Sockets.TcpClient
        $tcp.Connect("localhost", $port)
        $tcp.Close()
        return $true
    } catch { return $false }
}

if (-not (Test-Port 8081)) {
    Write-Host "WARN: Backend not running on port 8081. Start it with 'mvn spring-boot:run' in another terminal." -ForegroundColor Yellow
}
if (-not (Test-Port 4200)) {
    Write-Host "WARN: Frontend not running on port 4200. Start it with 'npm start' in another terminal." -ForegroundColor Yellow
}

# 3. Run tests
Set-Location $PSScriptRoot
& $mvn test

Write-Host ""
Write-Host "Done. Open target\surefire-reports\emailable-report.html for the report." -ForegroundColor Green
