package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.dto.CategoryWithCountDTO;
import com.wechat.studygame.model.entity.Chapter;
import com.wechat.studygame.model.entity.Level;
import com.wechat.studygame.model.entity.Question;
import com.wechat.studygame.model.entity.Subject;
import com.wechat.studygame.model.entity.UserAnswer;
import com.wechat.studygame.repository.ChapterRepository;
import com.wechat.studygame.repository.LevelRepository;
import com.wechat.studygame.repository.SubjectRepository;
import com.wechat.studygame.repository.UserAnswerRepository;
import com.wechat.studygame.service.QuestionService;
import com.wechat.studygame.service.UserAnswerService;
import com.fasterxml.jackson.core.JsonProcessingException; // For logging
import com.fasterxml.jackson.databind.ObjectMapper; // For logging
import org.slf4j.Logger; // For logging
import org.slf4j.LoggerFactory; // For logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户答题记录服务实现类
 */
@Service
public class UserAnswerServiceImpl implements UserAnswerService {

    private static final Logger logger = LoggerFactory.getLogger(UserAnswerServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper(); // For structured logging of results

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private LevelRepository levelRepository;
    
    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public UserAnswer saveUserAnswer(Long userId, Long questionId, Long levelId, Long chapterId, Long subjectId,
                                    String userAnswer, Boolean isCorrect, Integer score, Long answerTime) {
        // 判断是否为第一次答题
        boolean isFirstAttempt = isFirstAttempt(userId, questionId);
        
        UserAnswer answer = new UserAnswer();
        answer.setUserId(userId);
        answer.setQuestionId(questionId);
        answer.setLevelId(levelId);
        answer.setChapterId(chapterId);
        answer.setSubjectId(subjectId);
        answer.setUserAnswer(userAnswer);
        answer.setIsCorrect(isCorrect);
        answer.setScore(score);
        answer.setAnswerTime(answerTime);
        answer.setIsFirstAttempt(isFirstAttempt);
        
        return userAnswerRepository.save(answer);
    }

    @Override
    public List<UserAnswer> getUserAnswersByLevelId(Long userId, Long levelId) {
        return userAnswerRepository.findByUserIdAndLevelId(userId, levelId);
    }

    @Override
    public List<UserAnswer> getWrongAnswersByChapterId(Long userId, Long chapterId) {
        return userAnswerRepository.findByUserIdAndChapterIdAndIsCorrect(userId, chapterId, false);
    }

    @Override
    public List<Question> getWrongQuestions(Long userId, Long chapterId) {
        List<Long> wrongQuestionIds = userAnswerRepository.findWrongQuestionIds(userId, chapterId);
        List<Question> wrongQuestions = new ArrayList<>();
        
        for (Long questionId : wrongQuestionIds) {
            try {
                Question question = questionService.getQuestionById(questionId);
                wrongQuestions.add(question);
            } catch (Exception e) {
                // 忽略不存在的题目
            }
        }
        
        return wrongQuestions;
    }

    @Override
    public List<Question> getWrongQuestionsByLevel(Long userId, Long levelId) {
        // 获取用户在指定关卡中回答错误的题目ID列表
        List<UserAnswer> wrongAnswers = userAnswerRepository.findByUserIdAndLevelIdAndIsCorrect(userId, levelId, false);
        return getQuestionsFromAnswers(wrongAnswers);
    }
    
    @Override
    public List<Question> getWrongQuestionsByChapter(Long userId, Long chapterId) {
        System.out.println("Getting wrong questions by chapter: " + chapterId + " for user: " + userId);
        
        try {
            // 原先的方法
            List<Question> chapterQuestions = getWrongQuestions(userId, chapterId);
            
            System.out.println("Found " + chapterQuestions.size() + " wrong questions directly associated with chapter");
            
            // 如果没有数据，尝试其他方法
            if (chapterQuestions.isEmpty()) {
                // 获取所有错题
                List<Question> allWrongQuestions = getAllWrongQuestions(userId);
                
                // 获取当前章节相关的关卡ID
                List<Level> levelsInChapter = levelRepository.findByChapterId(chapterId);
                Set<Long> levelIdsInChapter = levelsInChapter.stream()
                        .map(Level::getId)
                        .collect(Collectors.toSet());
                
                System.out.println("Found " + levelIdsInChapter.size() + " levels in chapter " + chapterId);
                
                // 过滤出属于当前章节的错题
                List<Question> filteredQuestions = allWrongQuestions.stream()
                        .filter(question -> {
                            // 先获取题目的关卡ID
                            Long questionLevelId = question.getLevelId();
                            return levelIdsInChapter.contains(questionLevelId);
                        })
                        .collect(Collectors.toList());
                
                System.out.println("Filtered to " + filteredQuestions.size() + " questions for chapter " + chapterId);
                return filteredQuestions;
            }
            
            return chapterQuestions;
        } catch (Exception e) {
            System.out.println("Error getting wrong questions by chapter: " + e.getMessage());
            e.printStackTrace();
            // 出错时返回空列表
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Question> getWrongQuestionsBySubject(Long userId, Long subjectId) {
        System.out.println("Getting wrong questions by subject: " + subjectId + " for user: " + userId);
        
        try {
            // 原先的方法
            List<UserAnswer> wrongAnswers = userAnswerRepository.findByUserIdAndSubjectIdAndIsCorrect(userId, subjectId, false);
            List<Question> subjectQuestions = getQuestionsFromAnswers(wrongAnswers);
            
            System.out.println("Found " + subjectQuestions.size() + " wrong questions directly associated with subject");
            
            // 如果没有数据，通过题目学科ID过滤所有错题
            if (subjectQuestions.isEmpty()) {
                List<Question> allWrongQuestions = getAllWrongQuestions(userId);
                System.out.println("Found total " + allWrongQuestions.size() + " wrong questions, filtering by subject " + subjectId);
                
                // 先打印所有错题的学科ID信息进行调试
                System.out.println("Analyzing subject IDs of all wrong questions:");
                allWrongQuestions.forEach(q -> {
                    System.out.println("Question ID: " + q.getId() + 
                                   ", Subject ID: " + q.getSubjectId() + 
                                   ", Question content: " + 
                                   (q.getContent() != null && q.getContent().length() > 20 ? 
                                    q.getContent().substring(0, 20) + "..." : q.getContent()));
                });
                
                // 过滤出当前学科的错题
                List<Question> filteredQuestions = allWrongQuestions.stream()
                        .filter(question -> {
                            // 检查题目学科ID是否匹配
                            Long questionSubjectId = question.getSubjectId();
                            boolean matches = (questionSubjectId != null) && questionSubjectId.equals(subjectId);
                            
                            if (!matches) {
                                // 如果题目的subjectId为空，尝试获取关联学科
                                Long levelId = question.getLevelId();
                                if (levelId != null) {
                                    // 尝试通过关卡 -> 章节 -> 学科的关系获取学科
                                    try {
                                        Level level = levelRepository.findById(levelId).orElse(null);
                                        if (level != null) {
                                            Long chapterId = level.getChapterId();
                                            if (chapterId != null) {
                                                Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
                                                if (chapter != null && subjectId.equals(chapter.getSubjectId())) {
                                                    System.out.println("Question " + question.getId() + 
                                                                   " matched through level/chapter relationship");
                                                    return true;
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Error finding subject relationship: " + e.getMessage());
                                    }
                                }
                            }
                            
                            return matches;
                        })
                        .collect(Collectors.toList());
                
                System.out.println("Filtered to " + filteredQuestions.size() + " questions for subject " + subjectId);
                
                // 如果过滤后还是没有错题，则尝试返回所有错题
                if (filteredQuestions.isEmpty()) {
                    System.out.println("No questions found for subject, returning all wrong questions");
                    return allWrongQuestions;
                }
                
                return filteredQuestions;
            }
            
            return subjectQuestions;
        } catch (Exception e) {
            System.out.println("Error getting wrong questions by subject: " + e.getMessage());
            e.printStackTrace();
            // 出错时返回空列表
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Question> getAllWrongQuestions(Long userId) {
        // 获取用户所有回答错误的题目ID列表
        List<UserAnswer> wrongAnswers = userAnswerRepository.findByUserIdAndIsCorrect(userId, false);
        return getQuestionsFromAnswers(wrongAnswers);
    }
    
    /**
     * 从答题记录中提取题目列表
     * 
     * @param answers 答题记录列表
     * @return 题目列表
     */
    private List<Question> getQuestionsFromAnswers(List<UserAnswer> answers) {
        // 提取不重复的题目ID
        List<Long> questionIds = answers.stream()
                .map(UserAnswer::getQuestionId)
                .distinct()
                .collect(Collectors.toList());
        
        List<Question> questions = new ArrayList<>();
        for (Long questionId : questionIds) {
            try {
                Question question = questionService.getQuestionById(questionId);
                questions.add(question);
            } catch (Exception e) {
                // 忽略不存在的题目
            }
        }
        
        return questions;
    }

    @Override
    public Map<String, Object> getWrongQuestionAnalysis(Long userId, Long questionId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取题目信息
            Question question = questionService.getQuestionById(questionId);
            result.put("question", question);
            
            // 获取用户的错误答案历史
            List<UserAnswer> wrongAnswers = userAnswerRepository.findAll().stream()
                    .filter(ua -> ua.getUserId().equals(userId) && ua.getQuestionId().equals(questionId) && !ua.getIsCorrect())
                    .collect(Collectors.toList());
            
            if (!wrongAnswers.isEmpty()) {
                // 取最近一次的错误答案
                UserAnswer latestWrongAnswer = wrongAnswers.get(wrongAnswers.size() - 1);
                result.put("userAnswer", latestWrongAnswer.getUserAnswer());
                
                // 统计用户错误次数
                result.put("wrongCount", wrongAnswers.size());
            }
            
            // 获取相似题目（用于训练）
            List<Question> similarQuestions = questionService.getSimilarQuestions(questionId, 3);
            result.put("similarQuestions", similarQuestions);
            
            // 生成错误分析
            result.put("analysis", generateErrorAnalysis(question, wrongAnswers));
            
            // 添加相似题目的正确答案（用于后续训练验证）
            List<Map<String, Object>> trainingQuestions = new ArrayList<>();
            if (similarQuestions != null && !similarQuestions.isEmpty()) {
                for (Question similar : similarQuestions) {
                    Map<String, Object> trainingQuestion = new HashMap<>();
                    trainingQuestion.put("question", similar);
                    trainingQuestion.put("correctAnswer", similar.getAnswer());
                    trainingQuestions.add(trainingQuestion);
                }
            }
            result.put("trainingQuestions", trainingQuestions);
            
            // 计算错误率（如果用户多次尝试该题目）
            List<UserAnswer> allAttempts = userAnswerRepository.findAll().stream()
                    .filter(ua -> ua.getUserId().equals(userId) && ua.getQuestionId().equals(questionId))
                    .collect(Collectors.toList());
            
            if (!allAttempts.isEmpty()) {
                double errorRate = (double) wrongAnswers.size() / allAttempts.size() * 100;
                result.put("errorRate", Math.round(errorRate * 10) / 10.0);
                result.put("totalAttempts", allAttempts.size());
            }
        } catch (Exception e) {
            result.put("error", "获取错题分析失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 生成错误分析
     * 
     * @param question 题目
     * @param wrongAnswers 错误答案列表
     * @return 错误分析内容
     */
    private Map<String, Object> generateErrorAnalysis(Question question, List<UserAnswer> wrongAnswers) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 1. 分析用户选择的错误选项分布
        Map<String, Integer> wrongOptionCounts = new HashMap<>();
        for (UserAnswer answer : wrongAnswers) {
            String option = answer.getUserAnswer();
            wrongOptionCounts.put(option, wrongOptionCounts.getOrDefault(option, 0) + 1);
        }
        analysis.put("wrongOptionDistribution", wrongOptionCounts);
        
        // 2. 找出最常选择的错误选项
        String mostChosenWrongOption = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : wrongOptionCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostChosenWrongOption = entry.getKey();
            }
        }
        analysis.put("mostChosenWrongOption", mostChosenWrongOption);
        
        // 3. 根据题目难度和错误模式生成分析文本
        String analysisText = "这道题目您答错了，";
        
        // 根据题目难度提供不同的分析
        if (question.getDifficultyLevel() != null) {
            if (question.getDifficultyLevel() <= 1) {
                analysisText += "这是一道基础题目，建议您复习相关基础知识点。";
            } else if (question.getDifficultyLevel() == 2) {
                analysisText += "这是一道中等难度题目，需要理解相关概念及应用。";
            } else {
                analysisText += "这是一道难度较高的题目，需要深入理解并灵活应用相关知识点。";
            }
        }
        
        // 添加知识点分析
        if (question.getKnowledgePoints() != null && !question.getKnowledgePoints().isEmpty()) {
            analysisText += " 涉及的知识点: " + question.getKnowledgePoints();
        }
        
        // 如果有题目解析，则添加
        if (question.getAnalysis() != null && !question.getAnalysis().isEmpty()) {
            analysisText += " " + question.getAnalysis();
        }
        
        // 添加推荐
        analysisText += " 我们为您准备了类似的题目进行训练，完成训练可以加深对知识点的理解。";
        
        analysis.put("analysisText", analysisText);
        
        return analysis;
    }

    @Override
    public boolean isFirstAttempt(Long userId, Long questionId) {
        // 查询用户对该题的所有答题记录
        return userAnswerRepository.findAll().stream()
                .noneMatch(ua -> ua.getUserId().equals(userId) && ua.getQuestionId().equals(questionId));
    }

    @Override
    public Map<String, Object> getUserLearningStats(Long userId) {
        logger.info("BEGIN - getUserLearningStats for userId: {}", userId);
        Map<String, Object> stats = new HashMap<>();
        
        List<UserAnswer> allAnswers = userAnswerRepository.findByUserId(userId);
        if (allAnswers == null) { // Defensive check
            allAnswers = new ArrayList<>();
            logger.warn("userAnswerRepository.findByUserId({}) returned null. Initializing to empty list.", userId);
        }
        
        // 计算总答题数
        int totalAnswers = allAnswers.size();
        stats.put("totalAnswers", totalAnswers);
        
        // 计算正确答题数和正确率
        long correctAnswers = allAnswers.stream()
                .filter(UserAnswer::getIsCorrect)
                .count();
        
        double correctRate = totalAnswers > 0 ? (double) correctAnswers / totalAnswers * 100 : 0;
        stats.put("correctAnswers", correctAnswers);
        stats.put("correctRate", Math.round(correctRate * 10) / 10.0); // 保留一位小数
        
        // 计算总获得积分
        int totalScore = allAnswers.stream()
                .mapToInt(UserAnswer::getScore)
                .sum();
        stats.put("totalScore", totalScore);
        
        // 计算平均答题时间（毫秒）
        double avgAnswerTime = allAnswers.stream()
                .mapToLong(UserAnswer::getAnswerTime)
                .average()
                .orElse(0);
        stats.put("avgAnswerTime", Math.round(avgAnswerTime * 10) / 10.0); // 保留一位小数
        
        // 计算不同题型的答题情况
        Map<String, Long> questionTypeStatsByName = allAnswers.stream()
                .collect(Collectors.groupingBy(
                        answer -> {
                            Question question = null;
                            try {
                                if (answer.getQuestionId() == null) {
                                    logger.warn("UserAnswer with ID {} has null questionId.", answer.getId());
                                    return "UNKNOWN_QUESTION_ID_NULL";
                                }
                                question = questionService.getQuestionById(answer.getQuestionId());
                                if (question == null) {
                                    logger.warn("Question not found for ID {}, referenced by UserAnswer ID {}. Marking as UNKNOWN_QUESTION_NOT_FOUND.", answer.getQuestionId(), answer.getId());
                                    return "UNKNOWN_QUESTION_NOT_FOUND";
                                }
                                if (question.getType() == null) {
                                    logger.warn("Question ID {} has null type. Marking as UNKNOWN_TYPE_NULL.", question.getId());
                                    return "UNKNOWN_TYPE_NULL";
                                }
                                switch (question.getType()) {
                                    case 1: return "SINGLE_CHOICE";
                                    case 2: return "MULTIPLE_CHOICE";
                                    case 3: return "TRUE_FALSE";
                                    case 4: return "FILL_BLANK";
                                    default:
                                        logger.warn("Question ID {} has unrecognized type: {}. Marking as UNKNOWN_OTHER_TYPE.", question.getId(), question.getType());
                                        return "UNKNOWN_OTHER_TYPE (" + question.getType() + ")";
                                }
                            } catch (Exception e) {
                                logger.error("Error retrieving question or type for UserAnswer ID {}: {}", answer.getId(), e.getMessage(), e);
                                return "UNKNOWN_ERROR_FETCHING_TYPE";
                            }
                        },
                        Collectors.counting()
                ));
        stats.put("questionTypeStats", questionTypeStatsByName);
        
        // 计算错题数量
        long wrongAnswers = totalAnswers - correctAnswers;
        stats.put("wrongAnswers", wrongAnswers);

        try {
            logger.info("END - getUserLearningStats for userId: {}. Stats: {}", userId, objectMapper.writeValueAsString(stats));
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing learning stats to JSON for logging for userId: {}", userId, e);
        }
        return stats;
    }
    
    @Override
    public Map<String, Object> getUserLearningTrend(Long userId) {
        logger.info("BEGIN - getUserLearningTrend for userId: {}", userId);
        Map<String, Object> trend = new HashMap<>();
        
        List<UserAnswer> allAnswers = userAnswerRepository.findByUserId(userId);
        if (allAnswers == null) { // Defensive check
            allAnswers = new ArrayList<>();
            logger.warn("userAnswerRepository.findByUserId({}) returned null. Initializing to empty list for trend.", userId);
        }
        
        // 获取最近7天的日期
        List<String> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            last7Days.add(date.toString());
        }
        
        // 按日期分组，计算每天的答题数和正确率
        Map<String, List<UserAnswer>> answersByDate = allAnswers.stream()
                .collect(Collectors.groupingBy(
                        answer -> {
                            if (answer.getCreateTime() == null) {
                                // This case should ideally be prevented by database constraints (NOT NULL on create_time)
                                logger.warn("UserAnswer with ID {} has null createTime. Defaulting to current date for trend calculation.", answer.getId());
                                return LocalDate.now().toString();
                            }
                            return answer.getCreateTime().toLocalDate().toString();
                        }
                ));
        
        List<Integer> dailyAnswerCounts = new ArrayList<>();
        List<Double> dailyCorrectRates = new ArrayList<>();
        
        for (String date : last7Days) {
            List<UserAnswer> answersOfDay = answersByDate.getOrDefault(date, new ArrayList<>());
            dailyAnswerCounts.add(answersOfDay.size());
            
            if (!answersOfDay.isEmpty()) {
                long correctCount = answersOfDay.stream()
                        .filter(UserAnswer::getIsCorrect)
                        .count();
                double correctRate = (double) correctCount / answersOfDay.size() * 100;
                dailyCorrectRates.add(Math.round(correctRate * 10) / 10.0); // 保留一位小数
            } else {
                dailyCorrectRates.add(0.0);
            }
        }
        
        trend.put("dates", last7Days);
        trend.put("answerCounts", dailyAnswerCounts);
        trend.put("correctRates", dailyCorrectRates);

        try {
            logger.info("END - getUserLearningTrend for userId: {}. Trend: {}", userId, objectMapper.writeValueAsString(trend));
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing learning trend to JSON for logging for userId: {}", userId, e);
        }
        return trend;
    }
    
    @Override
    public Map<String, Object> getSubjectLearningStats(Long userId) {
        logger.info("BEGIN - getSubjectLearningStats for userId: {}", userId);
        Map<String, Object> subjectStatsResult = new HashMap<>();
        
        List<UserAnswer> allAnswers = userAnswerRepository.findByUserId(userId);
        if (allAnswers == null) { // Defensive check
            allAnswers = new ArrayList<>();
            logger.warn("userAnswerRepository.findByUserId({}) returned null. Initializing to empty list for subject stats.", userId);
        }
        
        // Filter out records with null subjectId first, then group
        Map<Long, List<UserAnswer>> answersBySubject = allAnswers.stream()
                .filter(answer -> {
                    if (answer.getSubjectId() == null) {
                        logger.warn("UserAnswer with ID {} has null subjectId. Excluding from subject stats.", answer.getId());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.groupingBy(UserAnswer::getSubjectId));
        
        List<Map<String, Object>> subjectsData = new ArrayList<>();
        
        for (Map.Entry<Long, List<UserAnswer>> entry : answersBySubject.entrySet()) {
            Long subjectId = entry.getKey();
            List<UserAnswer> subjectAnswers = entry.getValue();
            
            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("subjectId", subjectId);
            
            // Fetch actual subject name
            String subjectName = subjectRepository.findById(subjectId)
                                                 .map(Subject::getName)
                                                 .orElse("学科 " + subjectId); // Default if not found
            subjectData.put("subjectName", subjectName);
            
            // Answer count for this subject
            int currentSubjectTotalAnswers = subjectAnswers.size();
            subjectData.put("answerCount", currentSubjectTotalAnswers);
            
            // Correct rate for this subject
            long correctAnswersInSubject = subjectAnswers.stream()
                    .filter(UserAnswer::getIsCorrect) // Assumes getIsCorrect returns boolean
                    .count();
            
            double correctRate = 0.0;
            if (currentSubjectTotalAnswers > 0) {
                correctRate = (double) correctAnswersInSubject / currentSubjectTotalAnswers * 100;
            }
            subjectData.put("correctRate", Math.round(correctRate * 10) / 10.0);
            
            // Average score for this subject
            double avgScore = subjectAnswers.stream()
                    .mapToInt(UserAnswer::getScore) // Assumes getScore returns int
                    .average()
                    .orElse(0.0); // Default to 0.0 if no scores for this subject
            subjectData.put("avgScore", Math.round(avgScore * 10) / 10.0);
            
            subjectsData.add(subjectData);
        }
        
        subjectStatsResult.put("subjects", subjectsData);

        try {
            logger.info("END - getSubjectLearningStats for userId: {}. Stats: {}", userId, objectMapper.writeValueAsString(subjectStatsResult));
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing subject learning stats to JSON for logging for userId: {}", userId, e);
        }
        return subjectStatsResult;
    }
    
    @Override
    public List<CategoryWithCountDTO> getLevelsWithWrongQuestions(Long userId) {
        List<CategoryWithCountDTO> result = new ArrayList<>();
        
        try {
            // 获取用户所有的错题答题记录
            List<UserAnswer> wrongAnswers = userAnswerRepository.findByUserIdAndIsCorrect(userId, false);
            
            System.out.println("Total wrong answers for user " + userId + ": " + wrongAnswers.size());
            
            // 检查是否有关卡ID
            long answersWithLevelId = wrongAnswers.stream()
                    .filter(answer -> answer.getLevelId() != null)
                    .count();
            
            System.out.println("Wrong answers with level ID: " + answersWithLevelId);
            
            // 如果没有关卡ID，添加所有关卡
            if (answersWithLevelId == 0) {
                System.out.println("No levels with wrong questions found, adding sample data");
                // 查询所有关卡
                List<Level> allLevels = levelRepository.findAll();
                for (Level level : allLevels) {
                    CategoryWithCountDTO dto = new CategoryWithCountDTO();
                    dto.setId(level.getId());
                    dto.setName(level.getName());
                    dto.setWrongCount(0); // 默认为0
                    result.add(dto);
                }
                
                // 确保至少有一个数据
                if (result.isEmpty() && !wrongAnswers.isEmpty()) {
                    CategoryWithCountDTO dto = new CategoryWithCountDTO();
                    dto.setId(1L);
                    dto.setName("所有关卡");
                    dto.setWrongCount(wrongAnswers.size());
                    result.add(dto);
                }
                
                return result;
            }
            
            // 按关卡ID分组统计
            Map<Long, Long> levelCountMap = wrongAnswers.stream()
                    .filter(answer -> answer.getLevelId() != null)
                    .collect(Collectors.groupingBy(UserAnswer::getLevelId, Collectors.counting()));
            
            System.out.println("Level count map size: " + levelCountMap.size());
            
            // 获取关卡信息并构建结果
            for (Map.Entry<Long, Long> entry : levelCountMap.entrySet()) {
                Long levelId = entry.getKey();
                Long count = entry.getValue();
                
                try {
                    Level level = levelRepository.findById(levelId).orElse(null);
                    if (level != null) {
                        CategoryWithCountDTO dto = new CategoryWithCountDTO();
                        dto.setId(levelId);
                        dto.setName(level.getName());
                        dto.setWrongCount(count.intValue());
                        result.add(dto);
                    } else {
                        System.out.println("Could not find level with ID: " + levelId);
                        // 仍然添加但用默认名称
                        CategoryWithCountDTO dto = new CategoryWithCountDTO();
                        dto.setId(levelId);
                        dto.setName("关卡 " + levelId);
                        dto.setWrongCount(count.intValue());
                        result.add(dto);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing level with ID " + levelId + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getLevelsWithWrongQuestions: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 按错题数量降序排序
        result.sort((a, b) -> b.getWrongCount() - a.getWrongCount());
        
        System.out.println("Returning " + result.size() + " levels with wrong questions");
        return result;
    }
    
    @Override
    public List<CategoryWithCountDTO> getChaptersWithWrongQuestions(Long userId) {
        List<CategoryWithCountDTO> result = new ArrayList<>();
        
        try {
            // 获取用户所有的错题答题记录
            List<UserAnswer> wrongAnswers = userAnswerRepository.findByUserIdAndIsCorrect(userId, false);
            
            System.out.println("Total wrong answers for user " + userId + ": " + wrongAnswers.size());
            
            // 检查是否有章节ID
            long answersWithChapterId = wrongAnswers.stream()
                    .filter(answer -> answer.getChapterId() != null)
                    .count();
            
            System.out.println("Wrong answers with chapter ID: " + answersWithChapterId);
            
            // 如果没有章节ID，添加所有章节
            if (answersWithChapterId == 0) {
                System.out.println("No chapters with wrong questions found, adding all chapters");
                // 查询所有章节
                List<Chapter> allChapters = chapterRepository.findAll();
                for (Chapter chapter : allChapters) {
                    CategoryWithCountDTO dto = new CategoryWithCountDTO();
                    dto.setId(chapter.getId());
                    dto.setName(chapter.getName());
                    dto.setWrongCount(0); // 默认为0
                    result.add(dto);
                }
                
                // 确保至少有一个数据
                if (result.isEmpty() && !wrongAnswers.isEmpty()) {
                    CategoryWithCountDTO dto = new CategoryWithCountDTO();
                    dto.setId(1L);
                    dto.setName("所有章节");
                    dto.setWrongCount(wrongAnswers.size());
                    result.add(dto);
                }
                
                return result;
            }
            
            // 按章节ID分组统计
            Map<Long, Long> chapterCountMap = wrongAnswers.stream()
                    .filter(answer -> answer.getChapterId() != null)
                    .collect(Collectors.groupingBy(UserAnswer::getChapterId, Collectors.counting()));
            
            System.out.println("Chapter count map size: " + chapterCountMap.size());
            
            // 获取章节信息并构建结果
            for (Map.Entry<Long, Long> entry : chapterCountMap.entrySet()) {
                Long chapterId = entry.getKey();
                Long count = entry.getValue();
                
                try {
                    Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
                    if (chapter != null) {
                        CategoryWithCountDTO dto = new CategoryWithCountDTO();
                        dto.setId(chapterId);
                        dto.setName(chapter.getName());
                        dto.setWrongCount(count.intValue());
                        result.add(dto);
                    } else {
                        System.out.println("Could not find chapter with ID: " + chapterId);
                        // 仍然添加但用默认名称
                        CategoryWithCountDTO dto = new CategoryWithCountDTO();
                        dto.setId(chapterId);
                        dto.setName("章节 " + chapterId);
                        dto.setWrongCount(count.intValue());
                        result.add(dto);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing chapter with ID " + chapterId + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getChaptersWithWrongQuestions: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 按错题数量降序排序
        result.sort((a, b) -> b.getWrongCount() - a.getWrongCount());
        
        System.out.println("Returning " + result.size() + " chapters with wrong questions");
        return result;
    }
    
    @Override
    public List<CategoryWithCountDTO> getSubjectsWithWrongQuestions(Long userId) {
        List<CategoryWithCountDTO> result = new ArrayList<>();
        
        try {
            // 获取用户所有的错题答题记录
            List<UserAnswer> wrongAnswers = userAnswerRepository.findByUserIdAndIsCorrect(userId, false);
            
            System.out.println("Total wrong answers for user " + userId + ": " + wrongAnswers.size());
            
            // 检查是否有学科ID
            long answersWithSubjectId = wrongAnswers.stream()
                    .filter(answer -> answer.getSubjectId() != null)
                    .count();
            
            System.out.println("Wrong answers with subject ID: " + answersWithSubjectId);
            
            // 如果没有学科ID，添加所有学科
            if (answersWithSubjectId == 0) {
                System.out.println("No subjects with wrong questions found, adding all subjects");
                // 查询所有学科
                List<Subject> allSubjects = subjectRepository.findAll();
                System.out.println("Found " + allSubjects.size() + " subjects in database");
                
                for (Subject subject : allSubjects) {
                    CategoryWithCountDTO dto = new CategoryWithCountDTO();
                    dto.setId(subject.getId());
                    dto.setName(subject.getName());
                    dto.setWrongCount(0); // 默认为0
                    result.add(dto);
                }
                
                // 确保至少有一个数据
                if (result.isEmpty() && !wrongAnswers.isEmpty()) {
                    CategoryWithCountDTO dto = new CategoryWithCountDTO();
                    dto.setId(1L);
                    dto.setName("所有学科");
                    dto.setWrongCount(wrongAnswers.size());
                    result.add(dto);
                }
                
                return result;
            }
            
            // 按学科ID分组统计
            Map<Long, Long> subjectCountMap = wrongAnswers.stream()
                    .filter(answer -> answer.getSubjectId() != null)
                    .collect(Collectors.groupingBy(UserAnswer::getSubjectId, Collectors.counting()));
            
            System.out.println("Subject count map size: " + subjectCountMap.size());
            
            // 获取学科信息并构建结果
            for (Map.Entry<Long, Long> entry : subjectCountMap.entrySet()) {
                Long subjectId = entry.getKey();
                Long count = entry.getValue();
                
                try {
                    Subject subject = subjectRepository.findById(subjectId).orElse(null);
                    if (subject != null) {
                        CategoryWithCountDTO dto = new CategoryWithCountDTO();
                        dto.setId(subjectId);
                        dto.setName(subject.getName());
                        dto.setWrongCount(count.intValue());
                        result.add(dto);
                    } else {
                        System.out.println("Could not find subject with ID: " + subjectId);
                        // 仍然添加但用默认名称
                        CategoryWithCountDTO dto = new CategoryWithCountDTO();
                        dto.setId(subjectId);
                        dto.setName("学科 " + subjectId);
                        dto.setWrongCount(count.intValue());
                        result.add(dto);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing subject with ID " + subjectId + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getSubjectsWithWrongQuestions: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 按错题数量降序排序
        result.sort((a, b) -> b.getWrongCount() - a.getWrongCount());
        
        System.out.println("Returning " + result.size() + " subjects with wrong questions");
        return result;
    }
    
    @Override
    public boolean hasCorrectlyAnswered(Long userId, Long questionId) {
        return userAnswerRepository.hasCorrectlyAnswered(userId, questionId);
    }
    
    @Override
    public int countCorrectAnswersByChapter(Long userId, Long chapterId) {
        int count = userAnswerRepository.countCorrectAnswersByChapter(userId, chapterId);
        System.out.println("用户ID:" + userId + "，章节ID:" + chapterId + "，正确答题数:" + count);
        return count;
    }
    
    @Override
    public int countQuestionsByChapter(Long chapterId) {
        int count = userAnswerRepository.countQuestionsByChapter(chapterId);
        System.out.println("章节ID:" + chapterId + "，题目总数:" + count);
        return count;
    }
    
    @Override
    public int countCorrectAnswersByLevel(Long userId, Long levelId) {
        int count = userAnswerRepository.countCorrectAnswersByLevel(userId, levelId);
        System.out.println("用户ID:" + userId + "，关卡ID:" + levelId + "，正确答题数:" + count);
        return count;
    }
}
