package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.entity.UserProgress;
import com.wechat.studygame.repository.UserProgressRepository;
import com.wechat.studygame.service.UserProgressService;
import com.wechat.studygame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户进度服务实现类
 */
@Service
public class UserProgressServiceImpl implements UserProgressService {

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private UserService userService;

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
        
        // 如果是首次完成
        if (progress.getFirstCompletionTime() == null) {
            progress.setFirstCompletionTime(LocalDateTime.now());
            
            // 更新用户总积分
            userService.updateUserScore(userId, totalScore);
        }
        
        return userProgressRepository.save(progress);
    }

    @Override
    public List<UserProgress> getLevelLeaderboard(Long levelId, int limit) {
        return userProgressRepository.findTopScoresByLevel(levelId, limit);
    }

    @Override
    public List<Map<String, Object>> getChapterLeaderboard(Long chapterId, int limit) {
        List<Object[]> results = userProgressRepository.findTopScoresByChapter(chapterId, limit);
        
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", (Long) result[0]);
            map.put("totalScore", (Long) result[1]);
            return map;
        }).collect(Collectors.toList());
    }

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
