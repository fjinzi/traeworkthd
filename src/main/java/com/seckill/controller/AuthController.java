package com.seckill.controller;

import com.seckill.dto.*;
import com.seckill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Result<LoginResultDTO>> login(@RequestBody UserLoginDTO loginDTO) {
        log.info("用户登录请求: {}", loginDTO.getUsername());
        try {
            LoginResultDTO result = userService.login(loginDTO);
            return ResponseEntity.ok(Result.success("登录成功", result));
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Result<UserDTO>> register(@RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册请求: {}", registerDTO.getUsername());
        try {
            UserDTO result = userService.register(registerDTO);
            return ResponseEntity.ok(Result.success("注册成功", result));
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Result<UserDTO>> getUserInfo(@RequestAttribute("userId") Long userId) {
        try {
            UserDTO user = userService.getCurrentUser(userId);
            if (user == null) {
                return ResponseEntity.ok(Result.fail("用户不存在"));
            }
            return ResponseEntity.ok(Result.success(user));
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }
}
