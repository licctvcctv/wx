package com.wechat.studygame.model.dto;

import com.wechat.studygame.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatLoginResponse {
    
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 用户信息
     */
    private User user;
}
