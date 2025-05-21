package com.wechat.studygame.service;

import com.wechat.studygame.model.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 题目服务接口
 */
public interface QuestionService {

    /**
     * 根据关卡ID获取所有启用的题目
     *
     * @param levelId 关卡ID
     * @return 题目列表
     */
    List<Question> getQuestionsByLevel(Long levelId);

    /**
     * 根据关卡ID和难度级别获取所有启用的题目
     *
     * @param levelId 关卡ID
     * @param difficultyLevel 难度级别
     * @return 题目列表
     */
    List<Question> getQuestionsByLevelAndDifficulty(Long levelId, Integer difficultyLevel);

    /**
     * 根据ID获取题目
     *
     * @param id 题目ID
     * @return 题目
     */
    Question getQuestionById(Long id);

    /**
     * 检查用户答案是否正确
     *
     * @param questionId 题目ID
     * @param userAnswer 用户答案
     * @return 是否正确
     */
    boolean checkAnswer(Long questionId, String userAnswer);
    
    /**
     * 获取关卡包含的总题目数量
     *
     * @param levelId 关卡ID
     * @return 题目数量
     */
    int countQuestionsByLevel(Long levelId);

    /**
     * 获取类似题目
     *
     * @param questionId 题目ID
     * @param limit 限制数量
     * @return 类似题目列表
     */
    List<Question> getSimilarQuestions(Long questionId, int limit);

    /**
     * 保存或更新题目
     *
     * @param question 题目
     * @return 保存后的题目
     */
    Question saveOrUpdateQuestion(Question question);

    /**
     * 删除题目
     *
     * @param id 题目ID
     */
    void deleteQuestion(Long id);
    
    /**
     * 获取题目总数
     * 
     * @return 题目总数
     */
    long getQuestionCount();
    
    /**
     * 分页获取所有题目
     * 
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> getAllQuestions(Pageable pageable);
    
    /**
     * 创建新题目
     * 
     * @param question 题目信息
     * @return 创建后的题目
     */
    Question createQuestion(Question question);
    
    /**
     * 更新题目
     * 
     * @param question 题目信息
     * @return 更新后的题目
     */
    Question updateQuestion(Question question);
    
    /**
     * 根据关卡ID分页查询题目
     * 
     * @param levelId 关卡ID
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> findQuestionsByLevel(Long levelId, Pageable pageable);
    
    /**
     * 根据题目类型分页查询题目
     * 
     * @param type 题目类型
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> findQuestionsByType(String type, Pageable pageable);
    
    /**
     * 根据题目内容分页查询题目
     * 
     * @param content 题目内容关键词
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> findQuestionsByContent(String content, Pageable pageable);
    
    /**
     * 根据关卡ID和题目类型分页查询题目
     * 
     * @param levelId 关卡ID
     * @param type 题目类型
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> findQuestionsByLevelAndType(Long levelId, String type, Pageable pageable);
    
    /**
     * 根据关卡ID和题目内容分页查询题目
     * 
     * @param levelId 关卡ID
     * @param content 题目内容关键词
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> findQuestionsByLevelAndContent(Long levelId, String content, Pageable pageable);
    
    /**
     * 根据题目类型和内容分页查询题目
     * 
     * @param type 题目类型
     * @param content 题目内容关键词
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> findQuestionsByTypeAndContent(String type, String content, Pageable pageable);
    
    /**
     * 根据关卡ID、题目类型和内容分页查询题目
     * 
     * @param levelId 关卡ID
     * @param type 题目类型
     * @param content 题目内容关键词
     * @param pageable 分页参数
     * @return 题目分页结果
     */
    Page<Question> findQuestionsByLevelAndTypeAndContent(Long levelId, String type, String content, Pageable pageable);
}
