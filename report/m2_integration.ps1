$ErrorActionPreference = 'Stop'

$projectRoot = 'd:\desk02\todo_26\codex\zhou_zhen\campus-qa-system'
$backendDir = Join-Path $projectRoot 'jeecg-boot'
$backendRunDir = Join-Path $backendDir 'jeecg-module-system/jeecg-system-start'
$reportDir = Join-Path $projectRoot 'report'
$logOut = Join-Path $reportDir 'backend-start-m2.log'
$logErr = Join-Path $reportDir 'backend-start-m2.err.log'
$apiResult = Join-Path $reportDir 'm2-api-results.json'

if (Test-Path $logOut) { Remove-Item $logOut -Force }
if (Test-Path $logErr) { Remove-Item $logErr -Force }
if (Test-Path $apiResult) { Remove-Item $apiResult -Force }

# Prevent false-positive startup detection from a stale backend process.
$existingListener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($existingListener) {
  Stop-Process -Id $existingListener.OwningProcess -Force
  Start-Sleep -Seconds 2
}

$mvn = (Get-Command mvn.cmd -ErrorAction Stop).Source

$job = Start-Job -ScriptBlock {
  param($backendRunDir, $mvn, $logOut, $logErr)
  Set-Location $backendRunDir
  & $mvn spring-boot:run '-Dspring-boot.run.profiles=dev' 1>> $logOut 2>> $logErr
} -ArgumentList $backendRunDir, $mvn, $logOut, $logErr

Write-Output "BACKEND_JOB_ID=$($job.Id)"

$started = $false
$startupSeconds = 0
for ($i = 0; $i -lt 180; $i++) {
  Start-Sleep -Seconds 2
  $startupSeconds += 2
  $listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
  if ($listener) {
    $started = $true
    break
  }
  if (Test-Path $logOut) {
    $tail = (Get-Content $logOut -Tail 60 -ErrorAction SilentlyContinue) -join "`n"
    if ($tail -match 'Started .* in .* seconds') {
      $started = $true
      break
    }
    if ($tail -match 'APPLICATION FAILED TO START|BUILD FAILURE') {
      break
    }
  }
}

if (-not $started) {
  Write-Output "BACKEND_START_FAILED_AFTER_SECONDS=$startupSeconds"
  if (Test-Path $logOut) {
    Write-Output '--- backend-start-m2.log tail ---'
    Get-Content $logOut -Tail 80
  }
  if (Test-Path $logErr) {
    Write-Output '--- backend-start-m2.err.log tail ---'
    Get-Content $logErr -Tail 80
  }
  Stop-Job $job -ErrorAction SilentlyContinue | Out-Null
  Remove-Job $job -Force -ErrorAction SilentlyContinue
  exit 1
}

Write-Output "BACKEND_STARTED_AFTER_SECONDS=$startupSeconds"

$base = 'http://127.0.0.1:8080/jeecg-boot'
$loginBody = @{
  username = 'admin'
  password = '123456'
} | ConvertTo-Json -Compress

$loginResp = Invoke-RestMethod -Method Post -Uri "$base/sys/mLogin" -ContentType 'application/json' -Body $loginBody
$token = $loginResp.result.token
if (-not $token) {
  throw "login failed: $($loginResp | ConvertTo-Json -Depth 6)"
}

$askBody = @{
  question = '教务处在哪里，办理休学需要什么流程？'
  userId = 'stu001'
} | ConvertTo-Json -Compress

$askResp = Invoke-RestMethod -Method Post -Uri "$base/campusqa/mini/ask" -ContentType 'application/json' -Body $askBody

$headers = @{
  'X-Access-Token' = $token
  'tenant-id' = '0'
  'version' = 'v3'
}

$overviewResp = Invoke-RestMethod -Method Get -Uri "$base/campusqa/stats/overview" -Headers $headers
$topResp = Invoke-RestMethod -Method Get -Uri "$base/campusqa/stats/topQuestions?limit=5" -Headers $headers
$intentResp = Invoke-RestMethod -Method Get -Uri "$base/campusqa/stats/intentStats?days=30" -Headers $headers

$summary = [ordered]@{
  backendStartedAfterSeconds = $startupSeconds
  login = [ordered]@{
    success = $loginResp.success
    code = $loginResp.code
    message = $loginResp.message
    tokenPrefix = if ($token.Length -ge 16) { $token.Substring(0, 16) } else { $token }
  }
  miniAsk = [ordered]@{
    success = $askResp.success
    code = $askResp.code
    intentType = $askResp.result.intentType
    intentLabel = $askResp.result.intentLabel
    intentScore = $askResp.result.intentScore
    matchedKeywords = $askResp.result.matchedKeywords
    sourceType = $askResp.result.sourceType
  }
  overview = [ordered]@{
    success = $overviewResp.success
    code = $overviewResp.code
    result = $overviewResp.result
  }
  topQuestions = [ordered]@{
    success = $topResp.success
    code = $topResp.code
    count = @($topResp.result).Count
    first = @($topResp.result | Select-Object -First 1)
  }
  intentStats = [ordered]@{
    success = $intentResp.success
    code = $intentResp.code
    count = @($intentResp.result).Count
    top3 = @($intentResp.result | Select-Object -First 3)
  }
}

$summary | ConvertTo-Json -Depth 12 | Out-File -FilePath $apiResult -Encoding utf8

Write-Output '--- M2_API_SUMMARY ---'
$summary | ConvertTo-Json -Depth 12
Write-Output "RESULT_FILE=$apiResult"

Stop-Job $job -ErrorAction SilentlyContinue | Out-Null
Remove-Job $job -Force -ErrorAction SilentlyContinue
