package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 章节数据仓库
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    /**
     * 根据学科ID查询所有章节，按排序权重排序
     *
     * @param subjectId 学科ID
     * @return 章节列表
     */
    List<Chapter> findBySubjectId(Long subjectId);

    /**
     * 根据学科ID查询所有启用的章节，按排序权重排序
     *
     * @param subjectId 学科ID
     * @param status 状态
     * @return 章节列表
     */
    List<Chapter> findBySubjectIdAndStatusOrderBySortOrderAsc(Long subjectId, Integer status);
}
