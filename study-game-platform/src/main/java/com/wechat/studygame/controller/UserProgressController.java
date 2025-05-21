package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.dto.CategoryWithCountDTO;
import com.wechat.studygame.model.dto.ChapterProgressDTO;
import com.wechat.studygame.model.dto.LevelProgressDTO;
import com.wechat.studygame.model.dto.SubjectProgressDTO;
import com.wechat.studygame.model.entity.Level;
import com.wechat.studygame.service.ChapterService;
import com.wechat.studygame.service.LevelService;
import com.wechat.studygame.service.QuestionService;
import com.wechat.studygame.service.SubjectService;
import com.wechat.studygame.service.UserAnswerService;
import com.wechat.studygame.service.UserProgressService;
import com.wechat.studygame.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户学习进度控制器
 */
@RestController
@RequestMapping({"/api/user/progress", "/api/user-progress"})
public class UserProgressController {

    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private UserProgressService userProgressService;
    
    @Autowired
    private ChapterService chapterService;
    
    @Autowired
    private LevelService levelService;
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private UserAnswerService userAnswerService;

    /**
     * 获取用户所有学科的学习进度
     *
     * @return 学科进度列表
     */
    @GetMapping("/subjects")
    public ApiResponse<List<SubjectProgressDTO>> getUserSubjectsProgress() {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取用户学科进度
            List<SubjectProgressDTO> progress = subjectService.getUserSubjectsProgress(userId);
            
            return ApiResponse.success(progress);
        } catch (Exception e) {
            return ApiResponse.error("获取学科进度失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户特定学科的学习进度
     *
     * @param subjectId 学科ID
     * @return 学科进度
     */
    @GetMapping("/subjects/{subjectId}")
    public ApiResponse<SubjectProgressDTO> getUserSubjectProgress(@PathVariable Long subjectId) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取用户特定学科进度
            SubjectProgressDTO progress = subjectService.getUserSubjectProgress(userId, subjectId);
            
            return ApiResponse.success(progress);
        } catch (Exception e) {
            return ApiResponse.error("获取学科进度失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户在指定学科下的所有章节进度
     *
     * @param subjectId 学科ID (可选)
     * @return 章节进度列表
     */
    @GetMapping("/chapters")
    public ApiResponse<List<CategoryWithCountDTO>> getUserChaptersProgress(
            @RequestParam(required = false) Long subjectId) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取章节列表并转换为CategoryWithCountDTO
            List<CategoryWithCountDTO> chaptersDTO = new ArrayList<>();
            
            // 获取章节实体列表
            List<com.wechat.studygame.model.entity.Chapter> chapterEntities;
            if (subjectId != null) {
                // 如果指定了学科ID，只获取该学科下的章节
                chapterEntities = chapterService.getChaptersBySubjectId(subjectId);
            } else {
                // 否则获取所有章节
                chapterEntities = chapterService.getAllChapters();
            }
            
            // 将Chapter实体转换为CategoryWithCountDTO
            for (com.wechat.studygame.model.entity.Chapter entity : chapterEntities) {
                CategoryWithCountDTO dto = new CategoryWithCountDTO();
                dto.setId(entity.getId());
                dto.setName(entity.getName());
                dto.setWrongCount(0); // 默认设置为0，因为这里我们只关心进度
                
                // 通过答题记录计算该章节的完成率
                Long chapterId = entity.getId();
                int correctAnswers = userAnswerService.countCorrectAnswersByChapter(userId, chapterId);
                int totalQuestions = userAnswerService.countQuestionsByChapter(chapterId);
                
                // 计算完成率，当章节没有题目时显示100%
                int completionRate;
                if (totalQuestions == 0) {
                    // 如果章节没有题目，记为已完成100%
                    completionRate = 100;
                    System.out.println("章节ID:" + chapterId + " 没有题目，设置完成率为100%");
                } else {
                    // 正常计算完成率
                    completionRate = (int) Math.round((double) correctAnswers / totalQuestions * 100);
                }
                
                dto.setCompletionRate(completionRate);
                
                chaptersDTO.add(dto);
            }
            
            return ApiResponse.success(chaptersDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取章节进度失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户在特定章节的详细进度，包括每个关卡的完成情况
     *
     * @param chapterId 章节ID
     * @return 章节详细进度
     */
    @GetMapping("/chapter/{chapterId}")
    public ApiResponse<ChapterProgressDTO> getChapterDetailedProgress(@PathVariable Long chapterId) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 创建章节进度对象
            ChapterProgressDTO chapterProgress = new ChapterProgressDTO();
            
            // 获取章节基本信息
            com.wechat.studygame.model.entity.Chapter chapter = chapterService.getChapterById(chapterId);
            chapterProgress.setChapterId(chapterId);
            chapterProgress.setChapterName(chapter.getName());
            
            // 计算章节总体完成情况
            int correctAnswers = userAnswerService.countCorrectAnswersByChapter(userId, chapterId);
            int totalQuestions = userAnswerService.countQuestionsByChapter(chapterId);
            
            chapterProgress.setCorrectAnswers(correctAnswers);
            chapterProgress.setTotalQuestions(totalQuestions);
            
            // 计算章节完成率
            int completionRate;
            if (totalQuestions == 0) {
                completionRate = 100; // 如果没有题目，认为是100%完成
            } else {
                completionRate = (int) Math.round((double) correctAnswers / totalQuestions * 100);
            }
            chapterProgress.setCompletionRate(completionRate);
            
            // 获取章节下所有关卡
            List<Level> levels = levelService.getLevelsByChapterId(chapterId);
            
            // 创建关卡进度列表
            List<LevelProgressDTO> levelProgressList = new ArrayList<>();
            
            // 填充每个关卡的进度信息
            for (Level level : levels) {
                LevelProgressDTO levelProgress = new LevelProgressDTO();
                levelProgress.setLevelId(level.getId());
                levelProgress.setLevelName(level.getName());
                
                // 查询关卡的问题总数
                int levelTotalQuestions = questionService.countQuestionsByLevel(level.getId());
                levelProgress.setTotalQuestions(levelTotalQuestions);
                
                // 查询用户在该关卡正确回答的问题数量
                int levelCorrectAnswers = userAnswerService.countCorrectAnswersByLevel(userId, level.getId());
                levelProgress.setCorrectAnswers(levelCorrectAnswers);
                
                // 计算关卡完成率
                int levelCompletionRate;
                if (levelTotalQuestions == 0) {
                    levelCompletionRate = 100; // 如果没有题目，认为是100%完成
                } else {
                    levelCompletionRate = (int) Math.round((double) levelCorrectAnswers / levelTotalQuestions * 100);
                }
                levelProgress.setCompletionRate(levelCompletionRate);
                
                // 判断关卡是否已完成
                // 如果有设置需要正确答题的数量，则使用该值判断，否则以100%完成率为标准
                boolean isCompleted;
                if (level.getRequiredCorrectCount() != null) {
                    isCompleted = levelCorrectAnswers >= level.getRequiredCorrectCount();
                } else if (level.getRequireAllCorrect() != null && level.getRequireAllCorrect()) {
                    isCompleted = levelCorrectAnswers == levelTotalQuestions;
                } else {
                    isCompleted = levelCompletionRate == 100;
                }
                levelProgress.setCompleted(isCompleted);
                
                levelProgressList.add(levelProgress);
            }
            
            chapterProgress.setLevelProgressList(levelProgressList);
            
            return ApiResponse.success(chapterProgress);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取章节详细进度失败：" + e.getMessage());
        }
    }
}
