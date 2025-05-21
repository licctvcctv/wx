package com.wechat.studygame.service;

import com.wechat.studygame.model.dto.CategoryWithCountDTO;
import com.wechat.studygame.model.entity.Question;
import com.wechat.studygame.model.entity.UserAnswer;

import java.util.List;
import java.util.Map;

/**
 * 用户答题记录服务接口
 */
public interface UserAnswerService {

    /**
     * 保存用户答题记录
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param levelId 关卡ID
     * @param chapterId 章节ID
     * @param subjectId 学科ID
     * @param userAnswer 用户答案
     * @param isCorrect 是否正确
     * @param score 得分
     * @param answerTime 答题时间（毫秒）
     * @return 保存后的答题记录
     */
    UserAnswer saveUserAnswer(Long userId, Long questionId, Long levelId, Long chapterId, Long subjectId,
                             String userAnswer, Boolean isCorrect, Integer score, Long answerTime);

    /**
     * 获取用户在指定关卡的答题记录
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 答题记录列表
     */
    List<UserAnswer> getUserAnswersByLevelId(Long userId, Long levelId);

    /**
     * 获取用户在指定章节的错题记录
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 错题记录列表
     */
    List<UserAnswer> getWrongAnswersByChapterId(Long userId, Long chapterId);

    /**
     * 检查用户是否已经正确回答过某题
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 如果有正确回答记录返回true，否则返回false
     */
    boolean hasCorrectlyAnswered(Long userId, Long questionId);
    
    /**
     * 获取用户在指定章节中的正确答题数量
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 正确答题数量
     */
    int countCorrectAnswersByChapter(Long userId, Long chapterId);
    
    /**
     * 获取用户在指定关卡中的正确答题数量
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 正确答题数量
     */
    int countCorrectAnswersByLevel(Long userId, Long levelId);
    
    /**
     * 获取章节包含的总题目数量
     *
     * @param chapterId 章节ID
     * @return 题目数量
     */
    int countQuestionsByChapter(Long chapterId);
    
    /**
     * 获取用户错题列表
     *
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 错题列表
     */
    List<Question> getWrongQuestions(Long userId, Long chapterId);
    
    /**
     * 获取用户在指定关卡的错题列表
     *
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 错题列表
     */
    List<Question> getWrongQuestionsByLevel(Long userId, Long levelId);
    
    /**
     * 获取用户在指定章节的错题列表
     * 
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @return 错题列表
     */
    List<Question> getWrongQuestionsByChapter(Long userId, Long chapterId);
    
    /**
     * 获取用户在指定学科的错题列表
     * 
     * @param userId 用户ID
     * @param subjectId 学科ID
     * @return 错题列表
     */
    List<Question> getWrongQuestionsBySubject(Long userId, Long subjectId);
    
    /**
     * 获取用户所有错题列表
     * 
     * @param userId 用户ID
     * @return 错题列表
     */
    List<Question> getAllWrongQuestions(Long userId);

    /**
     * 获取错题详细分析
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错题分析，包含题目信息、用户答案、正确答案、解析和相似题目
     */
    Map<String, Object> getWrongQuestionAnalysis(Long userId, Long questionId);

    /**
     * 获取包含错题的关卡列表
     *
     * @param userId 用户ID
     * @return 包含错题的关卡列表
     */
    List<CategoryWithCountDTO> getLevelsWithWrongQuestions(Long userId);

    /**
     * 获取包含错题的章节列表
     *
     * @param userId 用户ID
     * @return 包含错题的章节列表
     */
    List<CategoryWithCountDTO> getChaptersWithWrongQuestions(Long userId);

    /**
     * 获取包含错题的学科列表
     *
     * @param userId 用户ID
     * @return 包含错题的学科列表
     */
    List<CategoryWithCountDTO> getSubjectsWithWrongQuestions(Long userId);

    /**
     * 判断题目是否为第一次答题
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 是否为第一次答题
     */
    boolean isFirstAttempt(Long userId, Long questionId);

    /**
     * 获取用户学习统计数据
     *
     * @param userId 用户ID
     * @return 学习统计数据
     */
    Map<String, Object> getUserLearningStats(Long userId);
    
    /**
     * 获取用户学习趋势数据（最近7天）
     *
     * @param userId 用户ID
     * @return 学习趋势数据
     */
    Map<String, Object> getUserLearningTrend(Long userId);
    
    /**
     * 获取用户各科目学习情况
     *
     * @param userId 用户ID
     * @return 各科目学习情况
     */
    Map<String, Object> getSubjectLearningStats(Long userId);
}
