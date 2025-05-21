package com.wechat.studygame.service;

import com.wechat.studygame.model.dto.LoginResponse;
import com.wechat.studygame.model.entity.Admin;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录响应
     */
    LoginResponse login(String username, String password);
    
    /**
     * 创建管理员
     *
     * @param username 用户名
     * @param password 密码
     * @param name 姓名
     * @return 创建后的管理员
     */
    Admin createAdmin(String username, String password, String name);
    
    /**
     * 根据用户名获取管理员
     *
     * @param username 用户名
     * @return 管理员
     */
    Admin getAdminByUsername(String username);
}
