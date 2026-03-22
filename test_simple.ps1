# 简单API测试
$baseUrl = "http://localhost:8080/api"

Write-Host "=== 测试1: 管理员登录 ===" -ForegroundColor Cyan
$loginData = '{"username":"admin","password":"admin123"}'
$response = Invoke-RestMethod -Uri "$baseUrl/user/login" -Method Post -Body $loginData -ContentType "application/json"
Write-Host "登录结果: $($response.success)"
if ($response.success) {
    $adminToken = $response.data.token
    Write-Host "Token: $($adminToken.Substring(0, 30))..." -ForegroundColor Green
}

Write-Host "`n=== 测试2: 获取用户信息 ===" -ForegroundColor Cyan
$headers = @{"Authorization" = "Bearer $adminToken"}
$response = Invoke-RestMethod -Uri "$baseUrl/user/info" -Method Get -Headers $headers
Write-Host "用户: $($response.data.username), 角色: $($response.data.role)"

Write-Host "`n=== 测试3: 查看秒杀商品(管理员) ===" -ForegroundColor Cyan
$response = Invoke-RestMethod -Uri "$baseUrl/seckill/products" -Method Get -Headers $headers
Write-Host "结果: $($response.message)"

Write-Host "`n=== 测试4: 未登录查看商品 ===" -ForegroundColor Cyan
$response = Invoke-RestMethod -Uri "$baseUrl/seckill/products" -Method Get
Write-Host "结果: $($response.message)"

Write-Host "`n=== 测试5: 普通用户登录 ===" -ForegroundColor Cyan
$loginData = '{"username":"user","password":"user123"}'
$response = Invoke-RestMethod -Uri "$baseUrl/user/login" -Method Post -Body $loginData -ContentType "application/json"
Write-Host "登录结果: $($response.success)"
if ($response.success) {
    $userToken = $response.data.token
    Write-Host "普通用户Token获取成功" -ForegroundColor Green
}

Write-Host "`n=== 测试6: 普通用户查看商品 ===" -ForegroundColor Cyan
$headers = @{"Authorization" = "Bearer $userToken"}
$response = Invoke-RestMethod -Uri "$baseUrl/seckill/products" -Method Get -Headers $headers
Write-Host "结果: $($response.message)"

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
