package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.AdminLoginRequest;
import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.dto.LoginResponse;
import com.wechat.studygame.model.entity.Admin;
import com.wechat.studygame.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody AdminLoginRequest request) {
        try {
            LoginResponse response = adminService.login(request.getUsername(), request.getPassword());
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 创建初始管理员账户（仅供初始化使用）
     *
     * @return 创建的管理员
     */
    @PostMapping("/init")
    public ApiResponse<Admin> createInitAdmin() {
        try {
            // 检查是否已存在管理员账户
            Admin existingAdmin = adminService.getAdminByUsername("admin");
            if (existingAdmin != null) {
                return ApiResponse.success(existingAdmin);
            }
            // 创建初始管理员账户，用户名/密码：admin/admin123
            Admin admin = adminService.createAdmin("admin", "admin123", "系统管理员");
            return ApiResponse.success(admin);
        } catch (Exception e) {
            return ApiResponse.error("创建初始管理员失败：" + e.getMessage());
        }
    }

    /**
     * 检查管理员账号是否已初始化
     *
     * @return 初始化状态
     */
    @GetMapping("/init-status")
    public ApiResponse<Boolean> checkInitStatus() {
        Admin existingAdmin = adminService.getAdminByUsername("admin");
        return ApiResponse.success(existingAdmin != null);
    }
}
