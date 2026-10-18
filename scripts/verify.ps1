$ErrorActionPreference = "Stop"

Write-Host "Checking Git status..."
git status --short

if (Test-Path "backend/mvnw.cmd") {
  Write-Host "Running backend tests..."
  Push-Location backend
  .\mvnw.cmd test
  Pop-Location
} else {
  Write-Warning "backend/mvnw.cmd not found. Generate the Spring Boot project and Maven Wrapper first."
}

if (Test-Path "frontend/package.json") {
  Write-Host "Checking frontend..."
  Push-Location frontend
  if (-not (Test-Path "node_modules")) { npm install }
  npm run build
  Pop-Location
} else {
  Write-Warning "frontend/package.json not found."
}
