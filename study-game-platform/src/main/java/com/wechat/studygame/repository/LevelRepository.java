package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.Chapter;
import com.wechat.studygame.model.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关卡数据仓库
 */
@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {

    /**
     * 根据章节ID查询所有关卡
     *
     * @param chapterId 章节ID
     * @return 关卡列表
     */
    List<Level> findByChapterId(Long chapterId);

    /**
     * 根据学科ID查询所有关卡
     *
     * @param subjectId 学科ID
     * @return 关卡列表
     */
    @Query("SELECT l FROM Level l JOIN Chapter c ON l.chapterId = c.id WHERE c.subjectId = :subjectId")
    List<Level> findBySubjectId(@Param("subjectId") Long subjectId);

    /**
     * 根据章节ID查询所有启用的关卡，按排序权重排序
     *
     * @param chapterId 章节ID
     * @param status 状态
     * @return 关卡列表
     */
    List<Level> findByChapterIdAndStatusOrderBySortOrderAsc(Long chapterId, Integer status);

    /**
     * Finds levels within a specific chapter that have a sortOrder greater than a given value,
     * ordered by sortOrder in ascending order.
     *
     * @param chapterId the ID of the chapter
     * @param sortOrder the sort order to compare against
     * @return a list of levels matching the criteria
     */
    List<Level> findByChapterIdAndSortOrderGreaterThanOrderBySortOrderAsc(Long chapterId, Integer sortOrder);
    
    /**
     * 根据多个章节ID查询所有关卡
     *
     * @param chapterIds 章节ID列表
     * @return 关卡列表
     */
    List<Level> findByChapterIdIn(List<Long> chapterIds);
}
