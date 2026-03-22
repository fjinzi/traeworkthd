import requests
import json

BASE_URL = "http://localhost:8080/api"

def test_register_and_login():
    """测试用户注册和登录功能"""
    print("=" * 60)
    print("测试1: 用户注册和登录")
    print("=" * 60)
    
    # 测试注册 - 正向测试
    print("\n1.1 测试注册新用户")
    register_data = {
        "username": "testuser001",
        "password": "123456",
        "email": "test@example.com"
    }
    response = requests.post(f"{BASE_URL}/user/register", json=register_data)
    print(f"注册请求: {response.status_code}")
    print(f"响应: {response.text}")
    
    # 测试登录 - 正向测试
    print("\n1.2 测试用户登录")
    login_data = {
        "username": "admin",
        "password": "admin123"
    }
    response = requests.post(f"{BASE_URL}/user/login", json=login_data)
    print(f"登录请求: {response.status_code}")
    result = response.json()
    print(f"响应: {json.dumps(result, ensure_ascii=False, indent=2)}")
    
    if result.get('success'):
        token = result['data']['token']
        print(f"获取Token成功: {token[:20]}...")
        return token
    else:
        print("登录失败!")
        return None

def test_register_negative():
    """反向测试：异常场景"""
    print("\n" + "=" * 60)
    print("测试2: 反向异常测试")
    print("=" * 60)
    
    # 测试2.1: 用户名已存在
    print("\n2.1 测试用户名已存在")
    register_data = {
        "username": "admin",
        "password": "123456"
    }
    response = requests.post(f"{BASE_URL}/user/register", json=register_data)
    print(f"状态码: {response.status_code}, 响应: {response.text}")
    
    # 测试2.2: 错误密码登录
    print("\n2.2 测试错误密码登录")
    login_data = {
        "username": "admin",
        "password": "wrongpassword"
    }
    response = requests.post(f"{BASE_URL}/user/login", json=login_data)
    print(f"状态码: {response.status_code}, 响应: {response.text}")
    
    # 测试2.3: 不存在的用户
    print("\n2.3 测试不存在的用户")
    login_data = {
        "username": "nonexistuser",
        "password": "123456"
    }
    response = requests.post(f"{BASE_URL}/user/login", json=login_data)
    print(f"状态码: {response.status_code}, 响应: {response.text}")
    
    # 测试2.4: 空用户名
    print("\n2.4 测试空用户名登录")
    login_data = {
        "username": "",
        "password": "123456"
    }
    response = requests.post(f"{BASE_URL}/user/login", json=login_data)
    print(f"状态码: {response.text}")
    
    # 测试2.5: 无效格式错误
    print("\n2.5 测试无效格式")
    register_data = {
        "username": "a",
        "password": "123"
    }
    response = requests.post(f"{BASE_URL}/user/register", json=register_data)
    print(f"状态码: {response.text}")

def test_seckill_access_control(token):
    """测试秒杀商品访问控制"""
    print("\n" + "=" * 60)
    print("测试3: 秒杀商品访问控制")
    print("=" * 60)
    
    # 测试3.1: 未登录访问秒杀商品列表
    print("\n3.1 未登录访问秒杀商品列表")
    response = requests.get(f"{BASE_URL}/seckill/products")
    print(f"状态码: {response.status_code}")
    print(f"响应: {response.text}")
    
    # 测试3.2: 已登录访问秒杀商品列表
    print("\n3.2 已登录访问秒杀商品列表")
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{BASE_URL}/seckill/products", headers=headers)
    print(f"状态码: {response.status_code}")
    try:
        result = response.json()
        print(f"响应: {json.dumps(result, ensure_ascii=False, indent=2)}")
    except:
        print(f"原始响应: {response.text[:200]}")

def test_user_info(token):
    """测试获取用户信息"""
    print("\n" + "=" * 60)
    print("测试4: 获取用户信息")
    print("=" * 60)
    
    # 测试4.1: 带有效token获取用户信息
    print("\n4.1 带有效token获取用户信息")
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{BASE_URL}/user/info", headers=headers)
    print(f"状态码: {response.status_code}")
    print(f"响应: {response.text}")
    
    # 测试4.2: 无效token
    print("\n4.2 无效token测试")
    headers = {"Authorization": "Bearer invalid_token_here"}
    response = requests.get(f"{BASE_URL}/user/info", headers=headers)
    print(f"状态码: {response.status_code}")
    print(f"响应: {response.text}")
    
    # 测试4.3: 无token
    print("\n4.3 无token测试")
    response = requests.get(f"{BASE_URL}/user/info")
    print(f"状态码: {response.status_code}")
    print(f"响应: {response.text}")

def test_register_new_user():
    """测试注册新用户"""
    print("\n" + "=" * 60)
    print("测试5: 注册新用户并登录测试")
    print("=" * 60)
    
    import random
    random_suffix = random.randint(1000, 9999)
    username = f"newuser{random_suffix}"
    
    print(f"\n5.1 注册新用户: {username}")
    register_data = {
        "username": username,
        "password": "123456",
        "email": f"{username}@test.com"
    }
    response = requests.post(f"{BASE_URL}/user/register", json=register_data)
    print(f"注册响应: {response.text}")
    
    print("\n5.2 使用新用户登录")
    login_data = {
        "username": username,
        "password": "123456"
    }
    response = requests.post(f"{BASE_URL}/user/login", json=login_data)
    print(f"登录响应: {response.text}")
    result = response.json()
    
    if result.get('success'):
        token = result['data']['token']
        print(f"普通用户登录成功，token: {token[:20]}...")
        
        print("\n5.3 普通用户(非管理员)访问秒杀商品列表")
        headers = {"Authorization": f"Bearer {token}"}
        response = requests.get(f"{BASE_URL}/seckill/products", headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")

if __name__ == "__main__":
    print("开始执行用户模块功能测试")
    
    # 1. 基础注册登录测试
    token = test_register_and_login()
    
    if token:
        # 2. 异常测试
        test_negative()
        
        # 3. 访问控制测试
        test_seckill_access_control(token)
        
        # 4. 用户信息测试
        test_user_info(token)
        
        # 5. 新用户注册登录测试
        test_register_new_user()
        
        print("\n" + "=" * 60)
        print("测试完成!")
        print("=" * 60)
    else:
        print("获取管理员登录失败，无法继续测试!")
