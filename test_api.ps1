# API完整功能测试脚本
$baseUrl = "http://localhost:8080/api"
$global:token = $null
$global:adminToken = $null
$testPassed = 0
$testFailed = 0

function Run-Test {
    param([string]$TestName, [ScriptBlock]$TestScript)
    
    Write-Host "`n=== $TestName ===" -ForegroundColor Cyan
    try {
        & $TestScript
        Write-Host "✓ 测试通过" -ForegroundColor Green
        $script:testPassed++
    }
    catch {
        Write-Host "✗ 测试失败: $_" -ForegroundColor Red
        $script:testFailed++
    }
}

# 测试1: 管理员用户登录
Run-Test "测试1: 管理员用户登录" {
    $loginData = @{
        username = "admin"
        password = "admin123"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/user/login" -Method Post -Body $loginData -ContentType "application/json"
    
    if (-not $response.success) {
        throw "登录失败: $($response.message)"
    }
    if (-not $response.data.token) {
        throw "未返回Token"
    }
    
    $global:adminToken = $response.data.token
    Write-Host "登录成功，Token: $($global:adminToken.Substring(0, 30))..."
}

# 测试2: 普通用户登录
Run-Test "测试2: 普通用户登录" {
    $loginData = @{
        username = "user"
        password = "user123"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/user/login" -Method Post -Body $loginData -ContentType "application/json"
    
    if (-not $response.success) {
        throw "登录失败: $($response.message)"
    }
    
    $global:token = $response.data.token
    Write-Host "普通用户登录成功"
}

# 测试3: 错误密码登录 - 反向测试
Run-Test "测试3: 反向测试 - 错误密码登录" {
    $loginData = @{
        username = "admin"
        password = "wrongpassword"
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/user/login" -Method Post -Body $loginData -ContentType "application/json"
        if ($response.success) {
            throw "应该登录失败但成功了"
        }
    }
    catch {
        $responseBody = $_.Exception.Response | ForEach-Object {
            $reader = New-Object System.IO.StreamReader($_.GetResponseStream())
            $reader.ReadToEnd()
        }
        Write-Host "预期的失败响应: $responseBody"
    }
}

# 测试4: 不存在的用户登录 - 反向测试
Run-Test "测试4: 反向测试 - 不存在的用户登录" {
    $loginData = @{
        username = "nonexistuser"
        password = "123456"
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/user/login" -Method Post -Body $loginData -ContentType "application/json"
        if ($response.success) {
            throw "应该登录失败但成功了"
        }
    }
    catch {
        Write-Host "预期的失败（用户不存在）"
    }
}

# 测试5: 新用户注册
$randomSuffix = Get-Random -Minimum 1000 -Maximum 9999
$newUsername = "newuser$randomSuffix"

Run-Test "测试5: 新用户注册" {
    $registerData = @{
        username = $newUsername
        password = "123456"
        email = "$newUsername@test.com"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/user/register" -Method Post -Body $registerData -ContentType "application/json"
    
    if (-not $response.success) {
        throw "注册失败: $($response.message)"
    }
    
    Write-Host "注册成功，用户: $newUsername"
}

# 测试6: 新注册用户登录
Run-Test "测试6: 新注册用户登录" {
    $loginData = @{
        username = $newUsername
        password = "123456"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/user/login" -Method Post -Body $loginData -ContentType "application/json"
    
    if (-not $response.success) {
        throw "登录失败: $($response.message)"
    }
    
    Write-Host "新用户登录成功"
}

# 测试7: 用户名已存在 - 反向测试
Run-Test "测试7: 反向测试 - 用户名已存在" {
    $registerData = @{
        username = "admin"
        password = "123456"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/user/register" -Method Post -Body $registerData -ContentType "application/json"
    
    if ($response.success) {
        throw "应该注册失败（用户名已存在）但成功了"
    }
    
    Write-Host "预期的失败（用户名已存在）: $($response.message)"
}

# 测试8: 获取用户信息（带Token）
Run-Test "测试8: 获取用户信息（带Token）" {
    $headers = @{
        "Authorization" = "Bearer $global:adminToken"
    }

    $response = Invoke-RestMethod -Uri "$baseUrl/user/info" -Method Get -Headers $headers
    
    if (-not $response.success) {
        throw "获取用户信息失败: $($response.message)"
    }
    
    Write-Host "用户信息: $($response.data.username), 角色: $($response.data.role)"
}

# 测试9: 未登录获取用户信息 - 反向测试
Run-Test "测试9: 反向测试 - 未登录获取用户信息" {
    $response = Invoke-RestMethod -Uri "$baseUrl/user/info" -Method Get
    
    if ($response.success) {
        throw "应该获取失败（未登录）但成功了"
    }
    
    Write-Host "预期的失败（未登录）: $($response.message)"
}

# 测试10: 管理员获取秒杀商品列表（已登录）
Run-Test "测试10: 管理员获取秒杀商品列表（已登录）" {
    $headers = @{
        "Authorization" = "Bearer $global:adminToken"
    }

    $response = Invoke-RestMethod -Uri "$baseUrl/seckill/products" -Method Get -Headers $headers
    
    Write-Host "响应: $($response | ConvertTo-Json -Depth 2)"
    Write-Host "管理员已登录，可以查看商品列表"
}

# 测试11: 普通用户获取秒杀商品列表（权限控制）
Run-Test "测试11: 普通用户获取秒杀商品列表（权限控制）" {
    $headers = @{
        "Authorization" = "Bearer $global:token"
    }

    $response = Invoke-RestMethod -Uri "$baseUrl/seckill/products" -Method Get -Headers $headers
    
    Write-Host "响应消息: $($response.message)"
    Write-Host "普通用户权限验证完成"
}

# 测试12: 未登录获取秒杀商品列表 - 反向测试
Run-Test "测试12: 反向测试 - 未登录获取秒杀商品列表" {
    $response = Invoke-RestMethod -Uri "$baseUrl/seckill/products" -Method Get
    
    if ($response.success -and $response.data.Count -gt 0) {
        throw "未登录用户不应该看到商品列表"
    }
    
    Write-Host "未登录无法查看商品列表: $($response.message)"
}

# 测试13: 无效Token测试 - 反向测试
Run-Test "测试13: 反向测试 - 无效Token" {
    $headers = @{
        "Authorization" = "Bearer invalid_token_here"
    }

    $response = Invoke-RestMethod -Uri "$baseUrl/seckill/products" -Method Get -Headers $headers
    
    Write-Host "无效Token处理完成: $($response.message)"
}

# 输出测试结果
Write-Host "`n"
Write-Host "=" * 60
Write-Host "测试完成！" -ForegroundColor Yellow
Write-Host "通过: $testPassed" -ForegroundColor Green
Write-Host "失败: $testFailed" -ForegroundColor Red
Write-Host "=" * 60

if ($testFailed -eq 0) {
    Write-Host "`n✓ 所有测试通过！" -ForegroundColor Green
} else {
    Write-Host "`n✗ 有测试失败，请检查！" -ForegroundColor Red
    exit 1
}
