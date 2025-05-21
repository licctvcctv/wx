package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.User;
import com.wechat.studygame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> getUserCount() {
        return ApiResponse.success(userService.getUserCount());
    }

    /**
     * 分页获取用户列表
     */
    @GetMapping
    public ApiResponse<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<User> users;
        
        if (search != null && !search.trim().isEmpty()) {
            users = userService.findUsersByUsernameOrNickname(search, pageable);
        } else {
            users = userService.getAllUsers(pageable);
        }
        
        return ApiResponse.success(users);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<User> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusRequest) {
        
        String status = statusRequest.get("status");
        if (status == null || (!status.equals("ACTIVE") && !status.equals("INACTIVE"))) {
            return ApiResponse.error(400, "无效的状态值，只能为ACTIVE或INACTIVE");
        }
        
        User user = userService.updateUserStatus(id, status);
        return ApiResponse.success(user);
    }
}
