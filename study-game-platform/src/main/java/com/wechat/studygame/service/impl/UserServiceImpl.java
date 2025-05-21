package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.dto.LoginResponse;
import com.wechat.studygame.model.entity.User;
import com.wechat.studygame.repository.UserRepository;
import com.wechat.studygame.service.JwtService;
import com.wechat.studygame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        user = saveOrUpdateUser(user);
        
        // 生成JWT令牌
        String token = jwtService.generateToken(username);
        
        return new LoginResponse(token, user);
    }

    @Override
    public User register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // 加密密码
        user.setNickname(nickname);
        user.setTotalScore(0);
        user.setCreateTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        
        return saveOrUpdateUser(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User saveOrUpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUserScore(Long userId, Integer scoreToAdd) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setTotalScore(user.getTotalScore() + scoreToAdd);
            return userRepository.save(user);
        }
        throw new RuntimeException("用户不存在");
    }

    @Override
    public List<User> getTopUsers(int limit) {
        return userRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "totalScore"))
        ).getContent();
    }
    
    @Override
    public long getUserCount() {
        return userRepository.count();
    }
    
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    public Page<User> findUsersByUsernameOrNickname(String keyword, Pageable pageable) {
        return userRepository.findByUsernameOrNicknameContaining(keyword, pageable);
    }
    
    @Override
    public User updateUserStatus(Long userId, String status) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 根据status字符串更新用户状态
        // 假设status可以是"enable"或"disable"
        if ("enable".equalsIgnoreCase(status)) {
            user.setStatus(1); // 启用
        } else if ("disable".equalsIgnoreCase(status)) {
            user.setStatus(0); // 禁用
        } else {
            throw new IllegalArgumentException("无效的状态值: " + status);
        }
        
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
