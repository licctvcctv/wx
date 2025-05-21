package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.dto.LoginResponse;
import com.wechat.studygame.model.entity.Admin;
import com.wechat.studygame.repository.AdminRepository;
import com.wechat.studygame.service.AdminService;
import com.wechat.studygame.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(String username, String password) {
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (!adminOptional.isPresent()) {
            throw new RuntimeException("用户名不存在");
        }

        Admin admin = adminOptional.get();
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间
        admin.setLastLoginTime(LocalDateTime.now());
        adminRepository.save(admin);

        // 生成JWT令牌
        String token = jwtService.generateToken(username);

        return new LoginResponse(token, null);
    }

    @Override
    public Admin createAdmin(String username, String password, String name) {
        // 检查用户名是否已存在
        if (adminRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setName(name);
        admin.setCreateTime(LocalDateTime.now());
        admin.setLastLoginTime(LocalDateTime.now());

        return adminRepository.save(admin);
    }

    @Override
    public Admin getAdminByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }
}
