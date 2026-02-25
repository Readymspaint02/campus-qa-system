$ErrorActionPreference = 'Stop'

$projectRoot = 'd:\desk02\todo_26\codex\zhou_zhen\campus-qa-system'
$backendDir = Join-Path $projectRoot 'jeecg-boot'
$backendRunDir = Join-Path $backendDir 'jeecg-module-system/jeecg-system-start'
$reportDir = Join-Path $projectRoot 'report'
$logOut = Join-Path $reportDir 'backend-start-m3.log'
$logErr = Join-Path $reportDir 'backend-start-m3.err.log'
$apiResult = Join-Path $reportDir 'm3-api-results.json'

if (Test-Path $logOut) { Remove-Item $logOut -Force }
if (Test-Path $logErr) { Remove-Item $logErr -Force }
if (Test-Path $apiResult) { Remove-Item $apiResult -Force }

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
    Write-Output '--- backend-start-m3.log tail ---'
    Get-Content $logOut -Tail 80
  }
  if (Test-Path $logErr) {
    Write-Output '--- backend-start-m3.err.log tail ---'
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

$headers = @{
  'X-Access-Token' = $token
  'tenant-id' = '0'
  'version' = 'v3'
}

$userId = 'stu001'
$askBody = @{
  question = '请问宿舍停电报修怎么走流程？'
  userId = $userId
} | ConvertTo-Json -Compress
$askResp = Invoke-RestMethod -Method Post -Uri "$base/campusqa/mini/ask" -ContentType 'application/json' -Body $askBody

$subscribeBody = @{
  userId = $userId
  categoryId = 'CQA_CAT_EDU'
} | ConvertTo-Json -Compress
$subscribeToggleResp = Invoke-RestMethod -Method Post -Uri "$base/campusqa/mini/subscribe/toggle" -ContentType 'application/json' -Body $subscribeBody
if ($subscribeToggleResp.result -eq 'removed') {
  $subscribeToggleResp = Invoke-RestMethod -Method Post -Uri "$base/campusqa/mini/subscribe/toggle" -ContentType 'application/json' -Body $subscribeBody
}
$subscribeListResp = Invoke-RestMethod -Method Get -Uri "$base/campusqa/mini/subscribe/list?userId=$userId&pageNo=1&pageSize=20"

$badFeedbackBody = @{
  userId = $userId
  knowledgeId = 'CQA_KNOW_001'
  rating = 'like'
  content = ''
} | ConvertTo-Json -Compress
$badFeedbackResp = Invoke-RestMethod -Method Post -Uri "$base/campusqa/mini/feedback" -ContentType 'application/json' -Body $badFeedbackBody

$badHistoryBody = @{
  userId = $userId
} | ConvertTo-Json -Compress
$badHistoryResp = Invoke-RestMethod -Method Post -Uri "$base/campusqa/mini/history" -ContentType 'application/json' -Body $badHistoryBody

$overviewResp = Invoke-RestMethod -Method Get -Uri "$base/campusqa/stats/overview" -Headers $headers
$topResp = Invoke-RestMethod -Method Get -Uri "$base/campusqa/stats/topQuestions?limit=5" -Headers $headers
$intentResp = Invoke-RestMethod -Method Get -Uri "$base/campusqa/stats/intentStats?days=30" -Headers $headers

$summary = [ordered]@{
  backendStartedAfterSeconds = $startupSeconds
  login = [ordered]@{
    success = $loginResp.success
    code = $loginResp.code
    tokenPrefix = if ($token.Length -ge 16) { $token.Substring(0, 16) } else { $token }
  }
  miniAsk = [ordered]@{
    success = $askResp.success
    code = $askResp.code
    intentType = $askResp.result.intentType
    intentLabel = $askResp.result.intentLabel
    intentScore = $askResp.result.intentScore
  }
  subscribe = [ordered]@{
    toggleSuccess = $subscribeToggleResp.success
    toggleResult = $subscribeToggleResp.result
    listSuccess = $subscribeListResp.success
    listCount = @($subscribeListResp.result.records).Count
  }
  validation = [ordered]@{
    feedbackShouldFail = (-not $badFeedbackResp.success)
    feedbackCode = $badFeedbackResp.code
    feedbackMessage = $badFeedbackResp.message
    historyShouldFail = (-not $badHistoryResp.success)
    historyCode = $badHistoryResp.code
    historyMessage = $badHistoryResp.message
  }
  stats = [ordered]@{
    overviewSuccess = $overviewResp.success
    overviewCode = $overviewResp.code
    historyTotal = $overviewResp.result.historyTotal
    askTotal = $overviewResp.result.askTotal
    topQuestionsSuccess = $topResp.success
    topQuestionsCount = @($topResp.result).Count
    intentStatsSuccess = $intentResp.success
    intentStatsCount = @($intentResp.result).Count
  }
}

$summary | ConvertTo-Json -Depth 10 | Out-File -FilePath $apiResult -Encoding utf8
Write-Output '--- M3_API_SUMMARY ---'
$summary | ConvertTo-Json -Depth 10
Write-Output "RESULT_FILE=$apiResult"

Stop-Job $job -ErrorAction SilentlyContinue | Out-Null
Remove-Job $job -Force -ErrorAction SilentlyContinue
