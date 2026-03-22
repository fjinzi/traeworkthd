package com.seckill.controller;

import com.seckill.dto.Result;
import com.seckill.dto.UserDTO;
import com.seckill.dto.UserLoginDTO;
import com.seckill.dto.UserRegisterDTO;
import com.seckill.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Result<UserDTO>> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册请求: username={}", registerDTO.getUsername());
        try {
            UserDTO userDTO = userService.register(registerDTO);
            return ResponseEntity.ok(Result.success("注册成功", userDTO));
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Result<UserDTO>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        log.info("用户登录请求: username={}", loginDTO.getUsername());
        try {
            UserDTO userDTO = userService.login(loginDTO);
            return ResponseEntity.ok(Result.success("登录成功", userDTO));
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Result<UserDTO>> getUserInfo(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestAttribute(value = "username", required = false) String username,
            @RequestAttribute(value = "role", required = false) Integer role) {
        if (userId == null) {
            return ResponseEntity.ok(Result.fail("未登录"));
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername(username);
        userDTO.setRole(role);
        return ResponseEntity.ok(Result.success(userDTO));
    }
}
