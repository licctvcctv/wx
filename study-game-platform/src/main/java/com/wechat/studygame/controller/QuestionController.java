package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Question;
import com.wechat.studygame.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 根据关卡ID获取题目列表
     *
     * @param levelId 关卡ID
     * @return 题目列表
     */
    @GetMapping("/by-level/{levelId}")
    public ApiResponse<List<Question>> getQuestionsByLevel(@PathVariable Long levelId) {
        return ApiResponse.success(questionService.getQuestionsByLevel(levelId));
    }

    /**
     * 根据关卡ID和难度级别获取题目列表
     *
     * @param levelId 关卡ID
     * @param difficultyLevel 难度级别
     * @return 题目列表
     */
    @GetMapping("/by-level/{levelId}/difficulty/{difficultyLevel}")
    public ApiResponse<List<Question>> getQuestionsByLevelAndDifficulty(
            @PathVariable Long levelId,
            @PathVariable Integer difficultyLevel) {
        return ApiResponse.success(questionService.getQuestionsByLevelAndDifficulty(levelId, difficultyLevel));
    }

    /**
     * 根据ID获取题目
     *
     * @param id 题目ID
     * @return 题目
     */
    @GetMapping("/{id}")
    public ApiResponse<Question> getQuestionById(@PathVariable Long id) {
        try {
            return ApiResponse.success(questionService.getQuestionById(id));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建或更新题目
     *
     * @param question 题目
     * @return 保存后的题目
     */
    @PostMapping
    public ApiResponse<Question> saveOrUpdateQuestion(@RequestBody Question question) {
        try {
            return ApiResponse.success(questionService.saveOrUpdateQuestion(question));
        } catch (Exception e) {
            return ApiResponse.error("保存题目失败：" + e.getMessage());
        }
    }

    /**
     * 删除题目
     *
     * @param id 题目ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除题目失败：" + e.getMessage());
        }
    }

    /**
     * 获取相似题目
     *
     * @param id 题目ID
     * @param limit 限制数量
     * @return 相似题目列表
     */
    @GetMapping("/{id}/similar")
    public ApiResponse<List<Question>> getSimilarQuestions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") int limit) {
        try {
            return ApiResponse.success(questionService.getSimilarQuestions(id, limit));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
