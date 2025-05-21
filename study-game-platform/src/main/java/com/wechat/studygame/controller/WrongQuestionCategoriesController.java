package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.dto.CategoryWithCountDTO;
import com.wechat.studygame.service.UserAnswerService;
import com.wechat.studygame.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 错题分类控制器
 * 提供按不同分类查询错题的API接口
 */
@RestController
@RequestMapping("/api")
public class WrongQuestionCategoriesController {

    @Autowired
    private UserAnswerService userAnswerService;

    /**
     * 获取包含错题的关卡列表
     * @return 关卡列表
     */
    @GetMapping("/levels/with-wrong-questions")
    public ApiResponse<List<CategoryWithCountDTO>> getLevelsWithWrongQuestions() {
        try {
            Long userId = UserUtil.getCurrentUserId();
            System.out.println("Getting levels with wrong questions for user: " + userId);
            List<CategoryWithCountDTO> levels = userAnswerService.getLevelsWithWrongQuestions(userId);
            System.out.println("Levels with wrong questions: " + levels.size());
            return ApiResponse.success(levels);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取关卡列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取包含错题的章节列表
     * @return 章节列表
     */
    @GetMapping("/chapters/with-wrong-questions")
    public ApiResponse<List<CategoryWithCountDTO>> getChaptersWithWrongQuestions() {
        try {
            Long userId = UserUtil.getCurrentUserId();
            System.out.println("Getting chapters with wrong questions for user: " + userId);
            List<CategoryWithCountDTO> chapters = userAnswerService.getChaptersWithWrongQuestions(userId);
            System.out.println("Chapters with wrong questions: " + chapters.size());
            return ApiResponse.success(chapters);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取章节列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取包含错题的学科列表
     * @return 学科列表
     */
    @GetMapping("/subjects/with-wrong-questions")
    public ApiResponse<List<CategoryWithCountDTO>> getSubjectsWithWrongQuestions() {
        try {
            Long userId = UserUtil.getCurrentUserId();
            System.out.println("Getting subjects with wrong questions for user: " + userId);
            List<CategoryWithCountDTO> subjects = userAnswerService.getSubjectsWithWrongQuestions(userId);
            System.out.println("Subjects with wrong questions: " + subjects.size());
            return ApiResponse.success(subjects);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取学科列表失败: " + e.getMessage());
        }
    }
}
