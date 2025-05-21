package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.dto.DashboardStats;
import com.wechat.studygame.service.ChapterService;
import com.wechat.studygame.service.LevelService;
import com.wechat.studygame.service.QuestionService;
import com.wechat.studygame.service.SubjectService;
import com.wechat.studygame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘数据控制器
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private LevelService levelService;
    
    @Autowired
    private QuestionService questionService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<DashboardStats> getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        // 设置各项统计数据
        stats.setSubjectCount(subjectService.getSubjectCount());
        stats.setChapterCount(chapterService.getChapterCount());
        stats.setLevelCount(levelService.getLevelCount());
        stats.setQuestionCount(questionService.getQuestionCount());
        stats.setUserCount(userService.getUserCount());
        
        return ApiResponse.success(stats);
    }
    
    /**
     * 获取学科数量
     */
    @GetMapping("/subjects/count")
    public ApiResponse<Long> getSubjectCount() {
        return ApiResponse.success(subjectService.getSubjectCount());
    }
    
    /**
     * 获取章节数量
     */
    @GetMapping("/chapters/count")
    public ApiResponse<Long> getChapterCount() {
        return ApiResponse.success(chapterService.getChapterCount());
    }
    
    /**
     * 获取关卡数量
     */
    @GetMapping("/levels/count")
    public ApiResponse<Long> getLevelCount() {
        return ApiResponse.success(levelService.getLevelCount());
    }
    
    /**
     * 获取用户数量
     */
    @GetMapping("/users/count")
    public ApiResponse<Long> getUserCount() {
        return ApiResponse.success(userService.getUserCount());
    }
}
