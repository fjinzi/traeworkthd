package com.seckill.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
    private String token;
}
