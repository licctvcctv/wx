package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目数据仓库
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 根据关卡ID查询所有启用的题目，按排序权重排序
     *
     * @param levelId 关卡ID
     * @param status 状态
     * @return 题目列表
     */
    List<Question> findByLevelIdAndStatusOrderBySortOrderAsc(Long levelId, Integer status);

    /**
     * 根据关卡ID和难度级别查询所有启用的题目
     *
     * @param levelId 关卡ID
     * @param difficultyLevel 难度级别
     * @param status 状态
     * @return 题目列表
     */
    List<Question> findByLevelIdAndDifficultyLevelAndStatusOrderBySortOrderAsc(Long levelId, Integer difficultyLevel, Integer status);

    /**
     * 根据章节ID查询所有启用的题目
     *
     * @param chapterId 章节ID
     * @param status 状态
     * @return 题目列表
     */
    List<Question> findByChapterIdAndStatusOrderBySortOrderAsc(Long chapterId, Integer status);

    /**
     * 根据知识点查询类似题目
     *
     * @param knowledgePoints 知识点
     * @param questionId 排除的题目ID
     * @param limit 限制数量
     * @return 题目列表
     */
    @Query(value = "SELECT * FROM questions WHERE knowledge_points LIKE %:knowledgePoints% AND id != :questionId AND status = 1 LIMIT :limit", nativeQuery = true)
    List<Question> findSimilarQuestions(String knowledgePoints, Long questionId, int limit);

    /**
     * 获取题目总数
     */
    long count();

    /**
     * 分页获取关卡下的题目
     */
    Page<Question> findByLevelId(Long levelId, Pageable pageable);

    /**
     * 分页获取特定类型的题目
     */
    Page<Question> findByType(Integer type, Pageable pageable);

    /**
     * 分页获取内容包含特定关键词的题目
     */
    Page<Question> findByContentContaining(String content, Pageable pageable);

    /**
     * 分页获取关卡下特定类型的题目
     */
    Page<Question> findByLevelIdAndType(Long levelId, Integer type, Pageable pageable);

    /**
     * 分页获取关卡下内容包含特定关键词的题目
     */
    Page<Question> findByLevelIdAndContentContaining(Long levelId, String content, Pageable pageable);

    /**
     * 分页获取特定类型且内容包含特定关键词的题目
     */
    Page<Question> findByTypeAndContentContaining(Integer type, String content, Pageable pageable);

    /**
     * 分页获取关卡下特定类型且内容包含特定关键词的题目
     */
    Page<Question> findByLevelIdAndTypeAndContentContaining(Long levelId, Integer type, String content, Pageable pageable);
    
    /**
     * 计算关卡包含的题目数量
     *
     * @param levelId 关卡ID
     * @return 题目数量
     */
    @Query(value = "SELECT COUNT(*) FROM questions WHERE level_id = :levelId", nativeQuery = true)
    int countQuestionsByLevel(Long levelId);
}
