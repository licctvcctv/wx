package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Question;
import com.wechat.studygame.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * 题目管理控制器
 */
@RestController
@RequestMapping("/api/admin/questions")
public class QuestionAdminController {

    @Autowired
    private QuestionService questionService;

    /**
     * 获取题目总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> getQuestionCount() {
        return ApiResponse.success(questionService.getQuestionCount());
    }

    /**
     * 分页获取题目列表
     */
    @GetMapping
    public ApiResponse<Page<Question>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Question> questions;
        
        if (levelId != null && type != null && search != null) {
            questions = questionService.findQuestionsByLevelAndTypeAndContent(levelId, type, search, pageable);
        } else if (levelId != null && type != null) {
            questions = questionService.findQuestionsByLevelAndType(levelId, type, pageable);
        } else if (levelId != null && search != null) {
            questions = questionService.findQuestionsByLevelAndContent(levelId, search, pageable);
        } else if (type != null && search != null) {
            questions = questionService.findQuestionsByTypeAndContent(type, search, pageable);
        } else if (levelId != null) {
            questions = questionService.findQuestionsByLevel(levelId, pageable);
        } else if (type != null) {
            questions = questionService.findQuestionsByType(type, pageable);
        } else if (search != null) {
            questions = questionService.findQuestionsByContent(search, pageable);
        } else {
            questions = questionService.getAllQuestions(pageable);
        }
        
        return ApiResponse.success(questions);
    }

    /**
     * 获取题目详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Question> getQuestionById(@PathVariable Long id) {
        return ApiResponse.success(questionService.getQuestionById(id));
    }

    /**
     * 创建题目
     */
    @PostMapping
    public ApiResponse<Question> createQuestion(@RequestBody Question question) {
        return ApiResponse.success(questionService.createQuestion(question));
    }

    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    public ApiResponse<Question> updateQuestion(
            @PathVariable Long id,
            @RequestBody Question question) {
        
        question.setId(id); // 确保ID一致
        return ApiResponse.success(questionService.updateQuestion(question));
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ApiResponse.success(null);
    }
}
