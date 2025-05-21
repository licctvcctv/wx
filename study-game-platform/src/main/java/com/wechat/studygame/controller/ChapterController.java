package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Chapter;
import com.wechat.studygame.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节控制器
 */
@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    /**
     * 根据学科ID获取章节列表
     *
     * @param subjectId 学科ID
     * @return 章节列表
     */
    @GetMapping("/by-subject/{subjectId}")
    public ApiResponse<List<Chapter>> getChaptersBySubject(@PathVariable Long subjectId) {
        return ApiResponse.success(chapterService.getChaptersBySubject(subjectId));
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
     * 创建或更新章节
     *
     * @param chapter 章节
     * @return 保存后的章节
     */
    @PostMapping
    public ApiResponse<Chapter> saveOrUpdateChapter(@RequestBody Chapter chapter) {
        try {
            return ApiResponse.success(chapterService.saveOrUpdateChapter(chapter));
        } catch (Exception e) {
            return ApiResponse.error("保存章节失败：" + e.getMessage());
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
