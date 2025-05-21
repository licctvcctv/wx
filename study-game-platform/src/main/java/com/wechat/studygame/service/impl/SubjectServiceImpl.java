package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.dto.SubjectProgressDTO;
import com.wechat.studygame.model.entity.Chapter;
import com.wechat.studygame.model.entity.Level;
import com.wechat.studygame.model.entity.Subject;
import com.wechat.studygame.model.entity.UserAnswer;
import com.wechat.studygame.repository.ChapterRepository;
import com.wechat.studygame.repository.LevelRepository;
import com.wechat.studygame.repository.SubjectRepository;
import com.wechat.studygame.repository.UserAnswerRepository;
import com.wechat.studygame.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学科服务实现类
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private LevelRepository levelRepository;
    
    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public List<Subject> getAllActiveSubjects() {
        return subjectRepository.findByStatusOrderBySortOrderAsc(1);
    }

    @Override
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学科不存在"));
    }

    @Override
    public Subject saveOrUpdateSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
    
    @Override
    public long getSubjectCount() {
        return subjectRepository.count();
    }
    
    @Override
    public List<SubjectProgressDTO> getUserSubjectsProgress(Long userId) {
        List<Subject> subjects = getAllActiveSubjects();
        List<SubjectProgressDTO> progressList = new ArrayList<>();
        
        for (Subject subject : subjects) {
            SubjectProgressDTO progress = getUserSubjectProgress(userId, subject.getId());
            progressList.add(progress);
        }
        
        return progressList;
    }
    
    @Override
    public SubjectProgressDTO getUserSubjectProgress(Long userId, Long subjectId) {
        // 获取学科信息
        Subject subject = getSubjectById(subjectId);
        
        // 获取学科的所有章节
        List<Chapter> chapters = chapterRepository.findBySubjectId(subjectId);
        
        // 初始化计数器
        int completedChapters = 0;
        int totalChapters = chapters.size();
        int completedLevels = 0;
        int totalLevels = 0;
        int correctQuestions = 0;
        int totalQuestions = 0;
        
        // 收集章节ID
        List<Long> chapterIds = new ArrayList<>();
        for (Chapter chapter : chapters) {
            chapterIds.add(chapter.getId());
        }
        
        // 获取所有关卡
        List<Level> levels = levelRepository.findByChapterIdIn(chapterIds);
        totalLevels = levels.size();
        
        // 收集关卡ID
        List<Long> levelIds = new ArrayList<>();
        for (Level level : levels) {
            levelIds.add(level.getId());
        }
        
        // 统计用户完成的章节和关卡
        Map<Long, Integer> chapterCompletionMap = new HashMap<>();
        Map<Long, Integer> levelCompletionMap = new HashMap<>();
        
        // 获取用户在这些关卡中的答题记录
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserIdAndLevelIdIn(userId, levelIds);
        
        // 统计正确答题数
        for (UserAnswer answer : userAnswers) {
            if (answer.getIsCorrect()) {
                correctQuestions++;
                
                // 统计关卡完成情况
                if (!levelCompletionMap.containsKey(answer.getLevelId())) {
                    levelCompletionMap.put(answer.getLevelId(), 1);
                } else {
                    levelCompletionMap.put(answer.getLevelId(), levelCompletionMap.get(answer.getLevelId()) + 1);
                }
                
                // 统计章节完成情况
                if (!chapterCompletionMap.containsKey(answer.getChapterId())) {
                    chapterCompletionMap.put(answer.getChapterId(), 1);
                } else {
                    chapterCompletionMap.put(answer.getChapterId(), chapterCompletionMap.get(answer.getChapterId()) + 1);
                }
            }
            totalQuestions++;
        }
        
        // 计算关卡完成数
        // 简化版：用户只要完成了任意题目，这个关卡就算完成了
        completedLevels = levelCompletionMap.size();
        
        // 计算章节完成数
        // 简化版：用户只要完成了任意题目，这个章节就算完成了
        completedChapters = chapterCompletionMap.size();
        
        // 计算总进度
        double progressPercentage = 0.0;
        if (totalLevels > 0) {
            progressPercentage = (double) completedLevels / totalLevels * 100;
        }
        
        // 格式化进度
        DecimalFormat df = new DecimalFormat("#0.0");
        String formattedProgress = df.format(progressPercentage) + "%";
        
        // 创建并返回进度DTO
        SubjectProgressDTO progressDTO = new SubjectProgressDTO();
        progressDTO.setSubjectId(subjectId);
        progressDTO.setSubjectName(subject.getName());
        progressDTO.setProgress(formattedProgress);
        progressDTO.setCompletedChapters(completedChapters);
        progressDTO.setTotalChapters(totalChapters);
        progressDTO.setCompletedLevels(completedLevels);
        progressDTO.setTotalLevels(totalLevels);
        progressDTO.setCorrectQuestions(correctQuestions);
        progressDTO.setTotalQuestions(totalQuestions);
        
        return progressDTO;
    }
}
