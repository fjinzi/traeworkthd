package com.seckill.service;

import com.seckill.dto.*;

public interface UserService {

    LoginResultDTO login(UserLoginDTO loginDTO);

    UserDTO register(UserRegisterDTO registerDTO);

    UserDTO getCurrentUser(Long userId);
}
