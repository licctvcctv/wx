package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Subject;
import com.wechat.studygame.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学科管理控制器 - 管理后台使用
 */
@RestController
@RequestMapping("/api/admin/subjects")
public class SubjectAdminController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 获取所有学科
     *
     * @return 学科列表
     */
    @GetMapping
    public ApiResponse<List<Subject>> getAllSubjects() {
        return ApiResponse.success(subjectService.getAllSubjects());
    }

    /**
     * 获取学科总数
     *
     * @return 学科总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> getSubjectCount() {
        return ApiResponse.success(subjectService.getSubjectCount());
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
    public ApiResponse<Subject> createSubject(@RequestBody Subject subject) {
        try {
            // 确保是新建操作
            subject.setId(null);
            return ApiResponse.success(subjectService.saveOrUpdateSubject(subject));
        } catch (Exception e) {
            return ApiResponse.error("创建学科失败：" + e.getMessage());
        }
    }

    /**
     * 更新学科
     *
     * @param id 学科ID
     * @param subject 学科
     * @return 更新后的学科
     */
    @PutMapping("/{id}")
    public ApiResponse<Subject> updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        try {
            subject.setId(id); // 确保ID一致
            return ApiResponse.success(subjectService.saveOrUpdateSubject(subject));
        } catch (Exception e) {
            return ApiResponse.error("更新学科失败：" + e.getMessage());
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
