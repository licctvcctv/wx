package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.User;
import com.wechat.studygame.model.entity.UserProgress;
import com.wechat.studygame.service.UserProgressService;
import com.wechat.studygame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排行榜控制器
 */
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private UserProgressService userProgressService;
    
    @Autowired
    private UserService userService;

    /**
     * 获取关卡排行榜
     *
     * @param levelId 关卡ID
     * @param limit 限制数量
     * @return 排行榜
     */
    @GetMapping("/level/{levelId}")
    public ApiResponse<List<Map<String, Object>>> getLevelLeaderboard(
            @PathVariable Long levelId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserProgress> leaderboard = userProgressService.getLevelLeaderboard(levelId, limit);
            
            // 填充用户信息
            List<Map<String, Object>> result = leaderboard.stream().map(progress -> {
                Map<String, Object> map = new HashMap<>();
                map.put("progress", progress);
                
                try {
                    // 直接通过用户ID获取用户信息
                    User user = userService.getUserById(progress.getUserId());
                    map.put("user", user);
                } catch (Exception e) {
                    map.put("user", null);
                }
                
                return map;
            }).collect(Collectors.toList());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取排行榜失败：" + e.getMessage());
        }
    }

    /**
     * 获取章节排行榜
     *
     * @param chapterId 章节ID
     * @param limit 限制数量
     * @return 排行榜
     */
    @GetMapping("/chapter/{chapterId}")
    public ApiResponse<List<Map<String, Object>>> getChapterLeaderboard(
            @PathVariable Long chapterId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> leaderboard = userProgressService.getChapterLeaderboard(chapterId, limit);
            
            // 填充用户信息
            leaderboard.forEach(map -> {
                Long userId = (Long) map.get("userId");
                try {
                    // 直接通过用户ID获取用户信息
                    User user = userService.getUserById(userId);
                    map.put("user", user);
                } catch (Exception e) {
                    map.put("user", null);
                }
            });
            
            return ApiResponse.success(leaderboard);
        } catch (Exception e) {
            return ApiResponse.error("获取排行榜失败：" + e.getMessage());
        }
    }

    /**
     * 获取全局用户积分排行榜
     *
     * @param limit 限制数量
     * @return 排行榜
     */
    @GetMapping("/global")
    public ApiResponse<List<User>> getGlobalLeaderboard(@RequestParam(defaultValue = "20") int limit) {
        try {
            List<User> topUsers = userService.getTopUsers(limit);
            return ApiResponse.success(topUsers);
        } catch (Exception e) {
            return ApiResponse.error("获取排行榜失败：" + e.getMessage());
        }
    }
}
