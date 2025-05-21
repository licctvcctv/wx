package com.wechat.studygame.service;

import com.wechat.studygame.model.dto.LoginResponse;
import com.wechat.studygame.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends UserDetailsService {

    /**
     * 通过用户名和密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录响应结果
     */
    LoginResponse login(String username, String password);

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @return 注册后的用户
     */
    User register(String username, String password, String nickname);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getUserByUsername(String username);
    
    /**
     * 根据用户ID获取用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    User getUserById(Long userId);

    /**
     * 保存或更新用户信息
     *
     * @param user 用户信息
     * @return 保存后的用户
     */
    User saveOrUpdateUser(User user);

    /**
     * 更新用户积分
     *
     * @param userId 用户ID
     * @param scoreToAdd 要增加的积分
     * @return 更新后的用户
     */
    User updateUserScore(Long userId, Integer scoreToAdd);

    /**
     * 获取排行榜前N名用户
     *
     * @param limit 数量限制
     * @return 用户列表
     */
    List<User> getTopUsers(int limit);
    
    /**
     * 获取用户总数
     * 
     * @return 用户总数
     */
    long getUserCount();
    
    /**
     * 分页获取所有用户
     * 
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<User> getAllUsers(Pageable pageable);
    
    /**
     * 根据用户名或昵称搜索用户
     * 
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<User> findUsersByUsernameOrNickname(String keyword, Pageable pageable);
    
    /**
     * 更新用户状态
     * 
     * @param userId 用户ID
     * @param status 状态值
     * @return 更新后的用户
     */
    User updateUserStatus(Long userId, String status);
}
