package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户进度数据仓库
 */
@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    /**
     * 查询用户在指定关卡的进度
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 用户进度
     */
    Optional<UserProgress> findByUserIdAndLevelId(Long userId, Long levelId);

    /**
     * 查询用户在指定章节的所有关卡进度
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 用户进度列表
     */
    List<UserProgress> findByUserIdAndChapterId(Long userId, Long chapterId);

    /**
     * 查询用户在指定学科的所有关卡进度
     *
     * @param userId 用户ID
     * @param subjectId 学科ID
     * @return 用户进度列表
     */
    List<UserProgress> findByUserIdAndSubjectId(Long userId, Long subjectId);

    /**
     * 查询用户的所有进度记录
     *
     * @param userId 用户ID
     * @return 用户进度列表
     */
    List<UserProgress> findByUserId(Long userId);

    /**
     * 查询指定关卡的排行榜
     *
     * @param levelId 关卡ID
     * @param limit 限制数量
     * @return 用户进度列表
     */
    @Query(value = "SELECT * FROM user_progress up WHERE up.level_id = :levelId AND up.status = 2 ORDER BY up.total_score DESC, up.time_used ASC LIMIT :limit", nativeQuery = true)
    List<UserProgress> findTopScoresByLevel(Long levelId, int limit);

    /**
     * 查询指定章节的排行榜
     *
     * @param chapterId 章节ID
     * @param limit 限制数量
     * @return 用户进度列表
     */
    @Query(value = "SELECT up.user_id as userId, SUM(up.total_score) as totalScore FROM user_progress up WHERE up.chapter_id = :chapterId AND up.status = 2 GROUP BY up.user_id ORDER BY totalScore DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopScoresByChapter(Long chapterId, int limit);
}
