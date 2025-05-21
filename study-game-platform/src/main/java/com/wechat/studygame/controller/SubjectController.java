package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Subject;
import com.wechat.studygame.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学科控制器
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 获取所有启用的学科
     *
     * @return 学科列表
     */
    @GetMapping
    public ApiResponse<List<Subject>> getAllActiveSubjects() {
        return ApiResponse.success(subjectService.getAllActiveSubjects());
    }

    /**
     * 根据ID获取学科
     *
     * @param id 学科ID
     * @return 学科
     */
    @GetMapping("/{id}")
    public ApiResponse<Subject> getSubjectById(@PathVariable Long id) {
        try {
            return ApiResponse.success(subjectService.getSubjectById(id));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建或更新学科
     *
     * @param subject 学科
     * @return 保存后的学科
     */
    @PostMapping
    public ApiResponse<Subject> saveOrUpdateSubject(@RequestBody Subject subject) {
        try {
            return ApiResponse.success(subjectService.saveOrUpdateSubject(subject));
        } catch (Exception e) {
            return ApiResponse.error("保存学科失败：" + e.getMessage());
        }
    }

    /**
     * 删除学科
     *
     * @param id 学科ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除学科失败：" + e.getMessage());
        }
    }
}
