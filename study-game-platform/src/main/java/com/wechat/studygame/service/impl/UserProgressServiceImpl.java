package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.entity.UserProgress;
import com.wechat.studygame.repository.UserProgressRepository;
import com.wechat.studygame.repository.LevelRepository; // Added import
import com.wechat.studygame.service.LevelService; // Added import
import com.wechat.studygame.service.UserProgressService;
import com.wechat.studygame.service.UserService;
import com.wechat.studygame.model.entity.Level; // Added import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger; // Added import
import org.slf4j.LoggerFactory; // Added import


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List; // Already present but good to note
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户进度服务实现类
 */
@Service
public class UserProgressServiceImpl implements UserProgressService {

    private static final Logger logger = LoggerFactory.getLogger(UserProgressServiceImpl.class); // Added logger

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LevelRepository levelRepository; // Injected LevelRepository

    @Autowired
    private LevelService levelService; // Injected LevelService

    @Override
    public UserProgress getUserProgressByLevelId(Long userId, Long levelId) {
        return userProgressRepository.findByUserIdAndLevelId(userId, levelId)
                .orElse(null);
    }

    @Override
    public List<UserProgress> getUserProgressByChapterId(Long userId, Long chapterId) {
        return userProgressRepository.findByUserIdAndChapterId(userId, chapterId);
    }

    @Override
    public UserProgress updateUserProgress(UserProgress userProgress) {
        return userProgressRepository.save(userProgress);
    }

    @Override
    public UserProgress initUserProgress(Long userId, Long levelId, Long chapterId, Long subjectId, Integer totalQuestions) {
        UserProgress progress = getUserProgressByLevelId(userId, levelId);
        if (progress == null) {
            progress = new UserProgress();
            progress.setUserId(userId);
            progress.setLevelId(levelId);
            progress.setChapterId(chapterId);
            progress.setSubjectId(subjectId);
            progress.setStatus(1); // 进行中
            progress.setCorrectCount(0);
            progress.setTotalCount(totalQuestions);
            progress.setTotalScore(0);
            progress.setTimeUsed(0L);
            progress.setLastAttemptTime(LocalDateTime.now());
            return userProgressRepository.save(progress);
        }
        
        // 如果已存在进度，更新最后尝试时间
        progress.setLastAttemptTime(LocalDateTime.now());
        progress.setStatus(1); // 重新设置为进行中
        return userProgressRepository.save(progress);
    }

    @Override
    public UserProgress completeLevel(Long userId, Long levelId, Integer correctCount, Integer totalScore, Long timeUsed) {
        UserProgress progress = getUserProgressByLevelId(userId, levelId);
        if (progress == null) {
            throw new RuntimeException("用户关卡进度不存在");
        }
        
        progress.setStatus(2); // 已完成
        progress.setCorrectCount(correctCount);
        progress.setTotalScore(totalScore);
        progress.setTimeUsed(timeUsed);
        progress.setLastAttemptTime(LocalDateTime.now());
        
        // Set first completion time if it's null
        if (progress.getFirstCompletionTime() == null) {
            progress.setFirstCompletionTime(LocalDateTime.now());
        }
        
        // Always update the user's global score with the points earned in this attempt.
        // 'totalScore' parameter here is the score for the current successful attempt.
        // This 'totalScore' is the score obtained in the current attempt for this level.
        userService.updateUserScore(userId, totalScore);
        
        UserProgress savedCurrentProgress = userProgressRepository.save(progress);

        // Unlock next level logic
        unlockNextLevel(userId, levelId, savedCurrentProgress);
        
        return savedCurrentProgress;
    }

    private void unlockNextLevel(Long userId, Long currentLevelId, UserProgress currentProgress) {
        Level currentLevel = levelRepository.findById(currentLevelId).orElse(null);
        if (currentLevel == null) {
            logger.error("Could not find current level with ID: {} to unlock next level.", currentLevelId);
            return;
        }

        List<Level> nextLevels = levelRepository.findByChapterIdAndSortOrderGreaterThanOrderBySortOrderAsc(
                currentLevel.getChapterId(),
                currentLevel.getSortOrder()
        );

        if (!nextLevels.isEmpty()) {
            Level nextLevel = nextLevels.get(0); // Get the immediate next level

            // Check if progress for the next level already exists
            UserProgress nextLevelProgress = getUserProgressByLevelId(userId, nextLevel.getId());

            if (nextLevelProgress == null) {
                // Progress for the next level does not exist, so create it (unlock it)
                UserProgress newNextLevelProgress = new UserProgress();
                newNextLevelProgress.setUserId(userId);
                newNextLevelProgress.setLevelId(nextLevel.getId());
                newNextLevelProgress.setChapterId(nextLevel.getChapterId());
                newNextLevelProgress.setSubjectId(currentProgress.getSubjectId()); // Use subjectId from current progress
                newNextLevelProgress.setStatus(1); // Set as "Unlocked" or "Not yet started"
                newNextLevelProgress.setCorrectCount(0);
                
                // Fetch totalQuestions from the nextLevel entity
                Integer totalQuestionsForNextLevel = nextLevel.getTotalQuestions();
                if (totalQuestionsForNextLevel == null) {
                    // Default or throw error if totalQuestions is critical and not set on Level
                    logger.warn("Total questions for next level ID {} is null. Defaulting to 0.", nextLevel.getId());
                    totalQuestionsForNextLevel = 0; 
                }
                newNextLevelProgress.setTotalCount(totalQuestionsForNextLevel);
                
                newNextLevelProgress.setTotalScore(0);
                newNextLevelProgress.setTimeUsed(0L);
                newNextLevelProgress.setLastAttemptTime(null); // Explicitly null for newly unlocked
                newNextLevelProgress.setFirstCompletionTime(null); // Not completed yet

                userProgressRepository.save(newNextLevelProgress);
                logger.info("User {} unlocked level {}.", userId, nextLevel.getId());
            } else {
                // Progress for next level already exists. Log or ensure status is appropriate if needed.
                // For now, if it exists, we assume it's already handled (e.g. unlocked, in progress, or completed).
                 logger.info("User {} already has progress for level {}. No new unlock needed.", userId, nextLevel.getId());
            }
        } else {
            logger.info("User {} completed level {}, which is the last level in chapter {}. No next level to unlock.",
                    userId, currentLevelId, currentLevel.getChapterId());
        }
    }

    // getLevelLeaderboard method implementation removed
    // getChapterLeaderboard method implementation removed

    @Override
    public Map<String, Object> getChapterCompletionRate(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取用户的所有进度记录
        List<UserProgress> allProgress = userProgressRepository.findAll().stream()
                .filter(progress -> progress.getUserId().equals(userId))
                .collect(Collectors.toList());
        
        // 按章节分组
        Map<Long, List<UserProgress>> progressByChapter = allProgress.stream()
                .collect(Collectors.groupingBy(UserProgress::getChapterId));
        
        List<Map<String, Object>> chaptersData = new ArrayList<>();
        
        for (Map.Entry<Long, List<UserProgress>> entry : progressByChapter.entrySet()) {
            Long chapterId = entry.getKey();
            List<UserProgress> chapterProgress = entry.getValue();
            
            Map<String, Object> chapterData = new HashMap<>();
            chapterData.put("chapterId", chapterId);
            
            // 尝试获取章节名称，如果有ChapterService可以注入并调用
            // chapterData.put("chapterName", chapterService.getChapterById(chapterId).getName());
            // 这里暂时用ID代替
            chapterData.put("chapterName", "章节" + chapterId);
            
            // 统计关卡总数
            int totalLevels = chapterProgress.size();
            chapterData.put("totalLevels", totalLevels);
            
            // 统计已完成关卡数和完成率
            long completedLevels = chapterProgress.stream()
                    .filter(p -> p.getStatus() == 2) // 状态为2表示已完成
                    .count();
            
            chapterData.put("completedLevels", completedLevels);
            
            double completionRate = totalLevels > 0 ? (double) completedLevels / totalLevels * 100 : 0;
            chapterData.put("completionRate", Math.round(completionRate * 10) / 10.0); // 保留一位小数
            
            // 统计章节总得分
            int totalScore = chapterProgress.stream()
                    .mapToInt(UserProgress::getTotalScore)
                    .sum();
            chapterData.put("totalScore", totalScore);
            
            chaptersData.add(chapterData);
        }
        
        result.put("chapters", chaptersData);
        
        // 计算总体完成率
        int totalLevels = allProgress.size();
        long totalCompletedLevels = allProgress.stream()
                .filter(p -> p.getStatus() == 2)
                .count();
        
        double overallCompletionRate = totalLevels > 0 ? (double) totalCompletedLevels / totalLevels * 100 : 0;
        result.put("overallCompletionRate", Math.round(overallCompletionRate * 10) / 10.0);
        
        return result;
    }
}
