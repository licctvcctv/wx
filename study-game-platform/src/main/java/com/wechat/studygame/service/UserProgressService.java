package com.wechat.studygame.service;

import com.wechat.studygame.model.entity.UserProgress;

import java.util.List;
import java.util.Map;

/**
 * 用户进度服务接口
 */
public interface UserProgressService {

    /**
     * 获取用户在指定关卡的进度
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 用户进度
     */
    UserProgress getUserProgressByLevelId(Long userId, Long levelId);

    /**
     * 获取用户在指定章节的所有关卡进度
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 用户进度列表
     */
    List<UserProgress> getUserProgressByChapterId(Long userId, Long chapterId);

    /**
     * 更新用户关卡进度
     *
     * @param userProgress 用户进度
     * @return 更新后的用户进度
     */
    UserProgress updateUserProgress(UserProgress userProgress);

    /**
     * 初始化用户关卡进度
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @param chapterId 章节ID
     * @param subjectId 学科ID
     * @param totalQuestions 题目总数
     * @return 初始化的用户进度
     */
    UserProgress initUserProgress(Long userId, Long levelId, Long chapterId, Long subjectId, Integer totalQuestions);

    /**
     * 完成关卡，更新进度状态
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @param correctCount 正确题目数量
     * @param totalScore 总分数
     * @param timeUsed 用时（秒）
     * @return 更新后的用户进度
     */
    UserProgress completeLevel(Long userId, Long levelId, Integer correctCount, Integer totalScore, Long timeUsed);

    // getLevelLeaderboard method signature removed
    // getChapterLeaderboard method signature removed

    /**
     * 获取用户章节完成率统计
     * 
     * @param userId 用户ID
     * @return 章节完成率统计
     */
    Map<String, Object> getChapterCompletionRate(Long userId);
}
