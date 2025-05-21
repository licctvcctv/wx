package com.wechat.studygame.util;

import com.wechat.studygame.model.entity.User;
import com.wechat.studygame.repository.UserRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 用户工具类
 */
@Component
public class UserUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UserUtil.applicationContext = applicationContext;
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        
        throw new RuntimeException("用户未登录");
    }

    /**
     * 获取当前登录用户ID
     * 通过当前用户名查询对应的用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        try {
            String username = getCurrentUsername();
            
            // 从应用上下文获取UserRepository
            UserRepository userRepository = applicationContext.getBean(UserRepository.class);
                
            // 查询用户并返回用户ID
            java.util.Optional<User> userOptional = userRepository.findByUsername(username);
            if (!userOptional.isPresent()) {
                throw new RuntimeException("用户不存在: " + username);
            }
            User user = userOptional.get();
            
            return user.getId();
        } catch (Exception e) {
            // 异常时记录错误并返回默认值
            System.err.println("获取用户ID出错: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
