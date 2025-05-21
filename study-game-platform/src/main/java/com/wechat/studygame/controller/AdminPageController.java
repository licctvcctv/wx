package com.wechat.studygame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 管理员页面控制器
 */
@Controller
public class AdminPageController {

    /**
     * 测试API接口，确认服务器正常工作
     */
    @GetMapping("/api/test")
    @ResponseBody
    public String test() {
        return "API工作正常! " + System.currentTimeMillis();
    }
    
    /**
     * 直接提供管理员登录页面
     */
    @GetMapping("/admin-login")
    public String adminLogin() {
        return "forward:/admin/login.html";
    }
    
    /**
     * 直接提供管理员主页
     */
    @GetMapping("/admin-home")
    public String adminHome() {
        return "forward:/admin/index.html";
    }
}
