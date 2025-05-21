package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Question;
import com.wechat.studygame.service.UserAnswerService;
import com.wechat.studygame.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 错题记录控制器
 */
@RestController
@RequestMapping("/api/wrong-questions")
public class WrongQuestionController {

    @Autowired
    private UserAnswerService userAnswerService;

    /**
     * 获取用户错题列表（支持多种筛选条件）
     *
     * @param levelId   关卡ID（可选）
     * @param chapterId 章节ID（可选）
     * @param subjectId 学科ID（可选）
     * @return 错题列表
     */
    @GetMapping
    public ApiResponse<List<Question>> getWrongQuestions(
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) Long subjectId) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();

            // 根据不同条件获取错题列表
            List<Question> wrongQuestions;
            if (levelId != null) {
                // 获取指定关卡的错题
                wrongQuestions = userAnswerService.getWrongQuestionsByLevel(userId, levelId);
            } else if (chapterId != null) {
                // 获取指定章节的错题
                wrongQuestions = userAnswerService.getWrongQuestionsByChapter(userId, chapterId);
            } else if (subjectId != null) {
                // 获取指定学科的错题
                wrongQuestions = userAnswerService.getWrongQuestionsBySubject(userId, subjectId);
            } else {
                // 获取所有错题
                wrongQuestions = userAnswerService.getAllWrongQuestions(userId);
            }

            return ApiResponse.success(wrongQuestions);
        } catch (Exception e) {
            return ApiResponse.error("获取错题列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户在指定章节的错题列表
     *
     * @param chapterId 章节ID
     * @return 错题列表
     */
    @GetMapping("/by-chapter/{chapterId}")
    public ApiResponse<List<Question>> getWrongQuestionsByChapter(@PathVariable Long chapterId) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取错题列表
            List<Question> wrongQuestions = userAnswerService.getWrongQuestions(userId, chapterId);
            
            return ApiResponse.success(wrongQuestions);
        } catch (Exception e) {
            return ApiResponse.error("获取错题列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取错题详细分析
     *
     * @param questionId 题目ID
     * @return 错题分析
     */
    @GetMapping("/{questionId}/analysis")
    public ApiResponse<Map<String, Object>> getWrongQuestionAnalysis(@PathVariable Long questionId) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取错题分析
            Map<String, Object> analysis = userAnswerService.getWrongQuestionAnalysis(userId, questionId);
            
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error("获取错题分析失败：" + e.getMessage());
        }
    }
}
