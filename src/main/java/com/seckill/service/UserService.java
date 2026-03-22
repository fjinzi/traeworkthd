package com.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.dto.UserDTO;
import com.seckill.dto.UserLoginDTO;
import com.seckill.dto.UserRegisterDTO;
import com.seckill.entity.User;

public interface UserService extends IService<User> {
    UserDTO register(UserRegisterDTO registerDTO);
    UserDTO login(UserLoginDTO loginDTO);
    User getByUsername(String username);
}
