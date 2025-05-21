package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.service.UserAnswerService;
import com.wechat.studygame.service.UserProgressService;
import com.wechat.studygame.service.UserService;
import com.wechat.studygame.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户学习统计控制器
 */
@RestController
@RequestMapping("/api/user/stats")
public class UserStatsController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserProgressService userProgressService;

    /**
     * 获取用户学习统计数据
     *
     * @return 学习统计数据
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getUserStats() {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取用户总积分
            int totalScore = userService.getUserById(userId).getTotalScore();
            
            // 获取学习统计数据
            Map<String, Object> stats = userAnswerService.getUserLearningStats(userId);
            
            // 获取章节完成率
            Map<String, Object> chapterCompletionRate = userProgressService.getChapterCompletionRate(userId);
            
            // 总体学习数据
            Map<String, Object> result = new HashMap<>();
            result.put("totalScore", totalScore);
            result.put("learningStats", stats);
            result.put("chapterCompletionRate", chapterCompletionRate);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取学习统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户学习趋势数据（最近7天）
     *
     * @return 学习趋势数据
     */
    @GetMapping("/trend")
    public ApiResponse<Map<String, Object>> getUserLearningTrend() {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取用户学习趋势数据
            Map<String, Object> trend = userAnswerService.getUserLearningTrend(userId);
            
            return ApiResponse.success(trend);
        } catch (Exception e) {
            return ApiResponse.error("获取学习趋势失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户各科目学习情况
     *
     * @return 各科目学习情况
     */
    @GetMapping("/subjects")
    public ApiResponse<Map<String, Object>> getSubjectStats() {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取各科目学习情况
            Map<String, Object> subjectStats = userAnswerService.getSubjectLearningStats(userId);
            
            return ApiResponse.success(subjectStats);
        } catch (Exception e) {
            return ApiResponse.error("获取科目学习情况失败：" + e.getMessage());
        }
    }
}
