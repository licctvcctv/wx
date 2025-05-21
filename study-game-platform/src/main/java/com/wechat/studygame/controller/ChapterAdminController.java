package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Chapter;
import com.wechat.studygame.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节管理控制器 - 管理后台使用
 */
@RestController
@RequestMapping("/api/admin/chapters")
public class ChapterAdminController {

    @Autowired
    private ChapterService chapterService;

    /**
     * 获取所有章节
     *
     * @return 章节列表
     */
    @GetMapping
    public ApiResponse<List<Chapter>> getAllChapters() {
        return ApiResponse.success(chapterService.getAllChapters());
    }

    /**
     * 获取章节总数
     *
     * @return 章节总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> getChapterCount() {
        return ApiResponse.success(chapterService.getChapterCount());
    }

    /**
     * 根据学科ID获取所有章节
     *
     * @param subjectId 学科ID
     * @return 章节列表
     */
    @GetMapping("/subject/{subjectId}")
    public ApiResponse<List<Chapter>> getChaptersBySubjectId(@PathVariable Long subjectId) {
        return ApiResponse.success(chapterService.getChaptersBySubjectId(subjectId));
    }

    /**
     * 根据ID获取章节
     *
     * @param id 章节ID
     * @return 章节
     */
    @GetMapping("/{id}")
    public ApiResponse<Chapter> getChapterById(@PathVariable Long id) {
        try {
            return ApiResponse.success(chapterService.getChapterById(id));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建章节
     *
     * @param chapter 章节
     * @return 保存后的章节
     */
    @PostMapping
    public ApiResponse<Chapter> createChapter(@RequestBody Chapter chapter) {
        try {
            // 确保是新建操作
            chapter.setId(null);
            return ApiResponse.success(chapterService.saveOrUpdateChapter(chapter));
        } catch (Exception e) {
            return ApiResponse.error("创建章节失败：" + e.getMessage());
        }
    }

    /**
     * 更新章节
     *
     * @param id 章节ID
     * @param chapter 章节
     * @return 更新后的章节
     */
    @PutMapping("/{id}")
    public ApiResponse<Chapter> updateChapter(@PathVariable Long id, @RequestBody Chapter chapter) {
        try {
            chapter.setId(id); // 确保ID一致
            
            // 获取原有章节
            Chapter existingChapter = chapterService.getChapterById(id);
            if (existingChapter != null && existingChapter.getCreateTime() != null) {
                // 确保创建时间字段不会丢失
                chapter.setCreateTime(existingChapter.getCreateTime());
            } else {
                // 设置一个默认的创建时间，避免null
                chapter.setCreateTime(java.time.LocalDateTime.now());
            }
            
            // 设置更新时间
            chapter.setUpdateTime(java.time.LocalDateTime.now());
            
            return ApiResponse.success(chapterService.saveOrUpdateChapter(chapter));
        } catch (Exception e) {
            return ApiResponse.error("更新章节失败：" + e.getMessage());
        }
    }

    /**
     * 删除章节
     *
     * @param id 章节ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除章节失败：" + e.getMessage());
        }
    }
}
