package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Level;
import com.wechat.studygame.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关卡管理控制器 - 管理后台使用
 */
@RestController
@RequestMapping("/api/admin/levels")
public class LevelAdminController {

    @Autowired
    private LevelService levelService;

    /**
     * 获取所有关卡
     *
     * @return 关卡列表
     */
    @GetMapping
    public ApiResponse<List<Level>> getAllLevels() {
        return ApiResponse.success(levelService.getAllLevels());
    }

    /**
     * 获取关卡总数
     *
     * @return 关卡总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> getLevelCount() {
        return ApiResponse.success(levelService.getLevelCount());
    }

    /**
     * 根据学科ID获取所有关卡
     *
     * @param subjectId 学科ID
     * @return 关卡列表
     */
    @GetMapping("/subject/{subjectId}")
    public ApiResponse<List<Level>> getLevelsBySubjectId(@PathVariable Long subjectId) {
        return ApiResponse.success(levelService.getLevelsBySubjectId(subjectId));
    }

    /**
     * 根据章节ID获取所有关卡
     *
     * @param chapterId 章节ID
     * @return 关卡列表
     */
    @GetMapping("/chapter/{chapterId}")
    public ApiResponse<List<Level>> getLevelsByChapterId(@PathVariable Long chapterId) {
        return ApiResponse.success(levelService.getLevelsByChapterId(chapterId));
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
     * 创建关卡
     *
     * @param level 关卡
     * @return 保存后的关卡
     */
    @PostMapping
    public ApiResponse<Level> createLevel(@RequestBody Level level) {
        try {
            // 确保是新建操作
            level.setId(null);
            return ApiResponse.success(levelService.saveOrUpdateLevel(level));
        } catch (Exception e) {
            return ApiResponse.error("创建关卡失败：" + e.getMessage());
        }
    }

    /**
     * 更新关卡
     *
     * @param id 关卡ID
     * @param level 关卡
     * @return 更新后的关卡
     */
    @PutMapping("/{id}")
    public ApiResponse<Level> updateLevel(@PathVariable Long id, @RequestBody Level level) {
        try {
            level.setId(id); // 确保ID一致
            
            // 获取原有关卡
            Level existingLevel = levelService.getLevelById(id);
            if (existingLevel != null && existingLevel.getCreateTime() != null) {
                // 确保创建时间字段不会丢失
                level.setCreateTime(existingLevel.getCreateTime());
            } else {
                // 设置一个默认的创建时间，避免null
                level.setCreateTime(java.time.LocalDateTime.now());
            }
            
            // 设置更新时间
            level.setUpdateTime(java.time.LocalDateTime.now());
            
            return ApiResponse.success(levelService.saveOrUpdateLevel(level));
        } catch (Exception e) {
            return ApiResponse.error("更新关卡失败：" + e.getMessage());
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
