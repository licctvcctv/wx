package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Level;
import com.wechat.studygame.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关卡控制器
 */
@RestController
@RequestMapping("/api/levels")
public class LevelController {

    @Autowired
    private LevelService levelService;

    /**
     * 根据章节ID获取关卡列表
     *
     * @param chapterId 章节ID
     * @return 关卡列表
     */
    @GetMapping("/by-chapter/{chapterId}")
    public ApiResponse<List<Level>> getLevelsByChapter(@PathVariable Long chapterId) {
        return ApiResponse.success(levelService.getLevelsByChapter(chapterId));
    }

    /**
     * 根据ID获取关卡
     *
     * @param id 关卡ID
     * @return 关卡
     */
    @GetMapping("/{id}")
    public ApiResponse<Level> getLevelById(@PathVariable Long id) {
        try {
            return ApiResponse.success(levelService.getLevelById(id));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建或更新关卡
     *
     * @param level 关卡
     * @return 保存后的关卡
     */
    @PostMapping
    public ApiResponse<Level> saveOrUpdateLevel(@RequestBody Level level) {
        try {
            return ApiResponse.success(levelService.saveOrUpdateLevel(level));
        } catch (Exception e) {
            return ApiResponse.error("保存关卡失败：" + e.getMessage());
        }
    }

    /**
     * 删除关卡
     *
     * @param id 关卡ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLevel(@PathVariable Long id) {
        try {
            levelService.deleteLevel(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除关卡失败：" + e.getMessage());
        }
    }
}
