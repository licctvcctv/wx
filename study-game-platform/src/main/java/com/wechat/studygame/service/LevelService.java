package com.wechat.studygame.service;

import com.wechat.studygame.model.entity.Level;

import java.util.List;

/**
 * 关卡服务接口
 */
public interface LevelService {

    /**
     * 获取所有关卡（包括禁用的）
     *
     * @return 关卡列表
     */
    List<Level> getAllLevels();

    /**
     * 根据学科ID获取所有关卡（包括禁用的）
     *
     * @param subjectId 学科ID
     * @return 关卡列表
     */
    List<Level> getLevelsBySubjectId(Long subjectId);

    /**
     * 根据章节ID获取所有关卡（包括禁用的）
     *
     * @param chapterId 章节ID
     * @return 关卡列表
     */
    List<Level> getLevelsByChapterId(Long chapterId);

    /**
     * 根据章节ID获取所有启用的关卡
     *
     * @param chapterId 章节ID
     * @return 关卡列表
     */
    List<Level> getLevelsByChapter(Long chapterId);

    /**
     * 根据ID获取关卡
     *
     * @param id 关卡ID
     * @return 关卡
     */
    Level getLevelById(Long id);

    /**
     * 保存或更新关卡
     *
     * @param level 关卡
     * @return 保存后的关卡
     */
    Level saveOrUpdateLevel(Level level);

    /**
     * 删除关卡
     *
     * @param id 关卡ID
     */
    void deleteLevel(Long id);
    
    /**
     * 获取关卡总数
     *
     * @return 关卡总数
     */
    long getLevelCount();
}
