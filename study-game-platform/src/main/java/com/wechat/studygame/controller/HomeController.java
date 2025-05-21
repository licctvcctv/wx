package com.wechat.studygame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 主页控制器
 */
@Controller
public class HomeController {

    /**
     * 主页
     */
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "<html><head><meta charset=\"UTF-8\"><title>学习游戏平台</title></head>" +
               "<body>" +
               "<h1>学习游戏平台 - 主页</h1>" +
               "<ul>" +
               "<li><a href=\"/test.html\">测试页面</a></li>" +
               "<li><a href=\"/admin/login.html\">管理员登录</a></li>" +
               "<li><a href=\"/admin-login\">通过控制器访问管理员登录</a></li>" +
               "<li><a href=\"/admin-home\">通过控制器访问管理员主页</a></li>" +
               "</ul>" +
               "</body></html>";
    }
    
    /**
     * 直接提供admin首页内容(不经过静态资源)
     */
    @GetMapping("/direct-admin")
    @ResponseBody
    public String directAdmin() {
        return "<html><head><meta charset=\"UTF-8\"><title>直接管理员页面</title></head>" +
               "<body>" +
               "<h1>直接管理员页面</h1>" +
               "<p>这是通过控制器直接返回的内容，不依赖于静态资源。</p>" +
               "<p>如果您能看到此页面但无法访问/admin/login.html，则问题出在静态资源加载上。</p>" +
               "<p>当前时间: " + java.time.LocalDateTime.now() + "</p>" +
               "</body></html>";
    }
}
