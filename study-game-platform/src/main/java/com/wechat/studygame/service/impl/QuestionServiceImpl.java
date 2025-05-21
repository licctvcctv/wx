package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.entity.Question;
import com.wechat.studygame.repository.QuestionRepository;
import com.wechat.studygame.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题目服务实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public List<Question> getQuestionsByLevel(Long levelId) {
        return questionRepository.findByLevelIdAndStatusOrderBySortOrderAsc(levelId, 1);
    }

    @Override
    public List<Question> getQuestionsByLevelAndDifficulty(Long levelId, Integer difficultyLevel) {
        return questionRepository.findByLevelIdAndDifficultyLevelAndStatusOrderBySortOrderAsc(levelId, difficultyLevel, 1);
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
    }

    @Override
    public boolean checkAnswer(Long questionId, String userAnswer) {
        Question question = getQuestionById(questionId);
        
        // 根据题目类型进行不同的答案比对
        switch (question.getType()) {
            case 1: // 单选题
            case 2: // 多选题
            case 3: // 判断题
                return question.getAnswer().trim().equalsIgnoreCase(userAnswer.trim());
            case 4: // 填空题
                return question.getAnswer().trim().equalsIgnoreCase(userAnswer.trim());
            case 5: // 简答题
                // 简答题需要更复杂的比对逻辑，这里简化处理
                return question.getAnswer().contains(userAnswer) || userAnswer.contains(question.getAnswer());
            default:
                return false;
        }
    }

    @Override
    public List<Question> getSimilarQuestions(Long questionId, int limit) {
        Question question = getQuestionById(questionId);
        
        // 如果知识点为空，则根据相同难度级别和类型查找
        if (question.getKnowledgePoints() == null || question.getKnowledgePoints().isEmpty()) {
            // 查找相同难度和类型的题目
            Integer difficulty = question.getDifficultyLevel();
            Integer type = question.getType();
            
            // 使用JPA动态查询，排除当前题目
            List<Question> similarQuestions = questionRepository.findAll().stream()
                    .filter(q -> !q.getId().equals(questionId)) // 排除当前题目
                    .filter(q -> q.getStatus() != null && q.getStatus() == 1) // 只包含启用的题目
                    .filter(q -> q.getType() != null && q.getType().equals(type)) // 相同类型
                    .filter(q -> q.getDifficultyLevel() != null && q.getDifficultyLevel().equals(difficulty)) // 相同难度
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
                    
            return similarQuestions;
        }
        
        // 如果有知识点，则使用基于知识点的查询
        return questionRepository.findSimilarQuestions(question.getKnowledgePoints(), questionId, limit);
    }

    @Override
    public Question saveOrUpdateQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
    
    @Override
    public long getQuestionCount() {
        return questionRepository.count();
    }
    
    @Override
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }
    
    @Override
    public Question createQuestion(Question question) {
        // 创建新题目时确保ID为null，以便自动生成
        question.setId(null);
        return questionRepository.save(question);
    }
    
    @Override
    public Question updateQuestion(Question question) {
        // 更新前确保题目存在
        getQuestionById(question.getId());
        return questionRepository.save(question);
    }
    
    @Override
    public Page<Question> findQuestionsByLevel(Long levelId, Pageable pageable) {
        return questionRepository.findByLevelId(levelId, pageable);
    }
    
    @Override
    public Page<Question> findQuestionsByType(String type, Pageable pageable) {
        // 将字符串类型转换成对应的整数类型
        Integer typeValue = convertTypeStringToInt(type);
        return questionRepository.findByType(typeValue, pageable);
    }
    
    @Override
    public Page<Question> findQuestionsByContent(String content, Pageable pageable) {
        return questionRepository.findByContentContaining(content, pageable);
    }
    
    @Override
    public Page<Question> findQuestionsByLevelAndType(Long levelId, String type, Pageable pageable) {
        Integer typeValue = convertTypeStringToInt(type);
        return questionRepository.findByLevelIdAndType(levelId, typeValue, pageable);
    }
    
    @Override
    public Page<Question> findQuestionsByLevelAndContent(Long levelId, String content, Pageable pageable) {
        return questionRepository.findByLevelIdAndContentContaining(levelId, content, pageable);
    }
    
    @Override
    public Page<Question> findQuestionsByTypeAndContent(String type, String content, Pageable pageable) {
        Integer typeValue = convertTypeStringToInt(type);
        return questionRepository.findByTypeAndContentContaining(typeValue, content, pageable);
    }
    
    @Override
    public Page<Question> findQuestionsByLevelAndTypeAndContent(Long levelId, String type, String content, Pageable pageable) {
        Integer typeValue = convertTypeStringToInt(type);
        return questionRepository.findByLevelIdAndTypeAndContentContaining(levelId, typeValue, content, pageable);
    }
    
    /**
     * 将字符串类型转换为对应的整数类型
     */
    private Integer convertTypeStringToInt(String type) {
        if (type == null) return null;
        
        switch (type.toUpperCase()) {
            case "SINGLE_CHOICE":
                return 1;
            case "MULTIPLE_CHOICE":
                return 2;
            case "TRUE_FALSE":
                return 3;
            case "FILL_BLANK":
                return 4;
            case "SHORT_ANSWER":
                return 5;
            default:
                try {
                    return Integer.parseInt(type);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("无效的题目类型: " + type);
                }
        }
    }
    
    @Override
    public int countQuestionsByLevel(Long levelId) {
        return questionRepository.countQuestionsByLevel(levelId);
    }
}
