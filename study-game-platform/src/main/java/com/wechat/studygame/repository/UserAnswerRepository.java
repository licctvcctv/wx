package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户答题记录数据仓库
 */
@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    /**
     * 查询用户在指定关卡中的答题记录
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 答题记录列表
     */
    List<UserAnswer> findByUserIdAndLevelId(Long userId, Long levelId);

    /**
     * 查询用户在指定章节中的答题记录
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 答题记录列表
     */
    List<UserAnswer> findByUserIdAndChapterId(Long userId, Long chapterId);

    /**
     * 查询用户在指定学科中的答题记录
     *
     * @param userId 用户ID
     * @param subjectId 学科ID
     * @return 答题记录列表
     */
    List<UserAnswer> findByUserIdAndSubjectId(Long userId, Long subjectId);

    /**
     * 查询用户在指定章节中的错题记录
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @param isCorrect 是否正确
     * @return 错题记录列表
     */
    List<UserAnswer> findByUserIdAndChapterIdAndIsCorrect(Long userId, Long chapterId, Boolean isCorrect);
    
    /**
     * 查询用户在指定关卡中的错题记录
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @param isCorrect 是否正确
     * @return 错题记录列表
     */
    List<UserAnswer> findByUserIdAndLevelIdAndIsCorrect(Long userId, Long levelId, Boolean isCorrect);
    
    /**
     * 查询用户在指定学科中的错题记录
     *
     * @param userId 用户ID
     * @param subjectId 学科ID
     * @param isCorrect 是否正确
     * @return 错题记录列表
     */
    List<UserAnswer> findByUserIdAndSubjectIdAndIsCorrect(Long userId, Long subjectId, Boolean isCorrect);
    
    /**
     * 查询用户所有的错题记录
     *
     * @param userId 用户ID
     * @param isCorrect 是否正确
     * @return 错题记录列表
     */
    List<UserAnswer> findByUserIdAndIsCorrect(Long userId, Boolean isCorrect);
    
    /**
     * 查询用户在多个关卡中的答题记录
     *
     * @param userId 用户ID
     * @param levelIds 关卡ID列表
     * @return 答题记录列表
     */
    List<UserAnswer> findByUserIdAndLevelIdIn(Long userId, List<Long> levelIds);

    /**
     * 查询用户首次答错的题目ID列表
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 题目ID列表
     */
    @Query("SELECT ua.questionId FROM UserAnswer ua WHERE ua.userId = :userId AND ua.chapterId = :chapterId AND ua.isCorrect = false AND ua.isFirstAttempt = true")
    List<Long> findWrongQuestionIds(Long userId, Long chapterId);

    /**
     * 查询用户所有答题记录
     *
     * @param userId 用户ID
     * @return 答题记录列表
     */
    List<UserAnswer> findByUserId(Long userId);
    
    /**
     * 检查用户是否已经正确回答过某题
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 如果有正确回答记录返回true，否则返回false
     */
    @Query("SELECT COUNT(ua) > 0 FROM UserAnswer ua WHERE ua.userId = :userId AND ua.questionId = :questionId AND ua.isCorrect = true")
    boolean hasCorrectlyAnswered(Long userId, Long questionId);
    
    /**
     * 获取用户在指定章节中的正确答题数量
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 正确答题数量
     */
    @Query(value = "SELECT COUNT(DISTINCT ua.question_id) FROM user_answers ua JOIN questions q ON ua.question_id = q.id JOIN levels l ON q.level_id = l.id WHERE ua.user_id = :userId AND l.chapter_id = :chapterId AND ua.is_correct = true", nativeQuery = true)
    int countCorrectAnswersByChapter(Long userId, Long chapterId);
    
    /**
     * 获取章节包含的总题目数量
     *
     * @param chapterId 章节ID
     * @return 题目数量
     */
    @Query(value = "SELECT COUNT(*) FROM questions q JOIN levels l ON q.level_id = l.id WHERE l.chapter_id = :chapterId", nativeQuery = true)
    int countQuestionsByChapter(Long chapterId);

    /**
     * 获取用户在指定关卡中的正确答题数量
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 正确答题数量
     */
    @Query(value = "SELECT COUNT(DISTINCT question_id) FROM user_answers WHERE user_id = :userId AND level_id = :levelId AND is_correct = true", nativeQuery = true)
    int countCorrectAnswersByLevel(Long userId, Long levelId);
}
