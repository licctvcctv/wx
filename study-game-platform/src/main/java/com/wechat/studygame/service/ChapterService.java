package com.wechat.studygame.service;

import com.wechat.studygame.model.entity.Chapter;

import java.util.List;

/**
 * 章节服务接口
 */
public interface ChapterService {

    /**
     * 获取所有章节（包括禁用的）
     *
     * @return 章节列表
     */
    List<Chapter> getAllChapters();

    /**
     * 根据学科ID获取所有章节（包括禁用的）
     *
     * @param subjectId 学科ID
     * @return 章节列表
     */
    List<Chapter> getChaptersBySubjectId(Long subjectId);

    /**
     * 根据学科ID获取所有启用的章节
     *
     * @param subjectId 学科ID
     * @return 章节列表
     */
    List<Chapter> getChaptersBySubject(Long subjectId);

    /**
     * 根据ID获取章节
     *
     * @param id 章节ID
     * @return 章节
     */
    Chapter getChapterById(Long id);

    /**
     * 保存或更新章节
     *
     * @param chapter 章节
     * @return 保存后的章节
     */
    Chapter saveOrUpdateChapter(Chapter chapter);

    /**
     * 删除章节
     *
     * @param id 章节ID
     */
    void deleteChapter(Long id);
    
    /**
     * 获取章节总数
     *
     * @return 章节总数
     */
    long getChapterCount();
}
