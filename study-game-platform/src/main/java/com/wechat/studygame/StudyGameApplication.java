package com.wechat.studygame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.wechat.studygame.util.ScriptExecutor;

/**
 * 学习游戏平台应用启动类
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.wechat.studygame"})
public class StudyGameApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
//        ScriptExecutor.executePowerShellScript();
        SpringApplication.run(StudyGameApplication.class, args);
        System.out.println("\n=========================================");
        System.out.println("  学习游戏平台已启动！");
        System.out.println("  访问管理后台: http://localhost:8080/admin/login.html");
        System.out.println("  默认管理员账号: admin/admin123");
        System.out.println("=========================================\n");
    }
}
