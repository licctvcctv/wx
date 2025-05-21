package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.entity.Level;
import com.wechat.studygame.repository.LevelRepository;
import com.wechat.studygame.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 关卡服务实现类
 */
@Service
public class LevelServiceImpl implements LevelService {

    @Autowired
    private LevelRepository levelRepository;

    @Override
    public List<Level> getAllLevels() {
        return levelRepository.findAll();
    }

    @Override
    public List<Level> getLevelsBySubjectId(Long subjectId) {
        return levelRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Level> getLevelsByChapterId(Long chapterId) {
        return levelRepository.findByChapterId(chapterId);
    }

    @Override
    public List<Level> getLevelsByChapter(Long chapterId) {
        return levelRepository.findByChapterIdAndStatusOrderBySortOrderAsc(chapterId, 1);
    }

    @Override
    public Level getLevelById(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("关卡不存在"));
    }

    @Override
    public Level saveOrUpdateLevel(Level level) {
        // 区分新增和更新操作
        if (level.getId() != null) {
            // 更新操作，需要保留原始创建时间
            Level existingLevel = levelRepository.findById(level.getId())
                    .orElseThrow(() -> new RuntimeException("要更新的关卡不存在"));
            
            // 保留原始创建时间
            level.setCreateTime(existingLevel.getCreateTime());
            
            // 更新时间由@PreUpdate注解自动设置
        } else {
            // 新增操作，通过@PrePersist注解自动设置创建时间和更新时间
        }
        
        return levelRepository.save(level);
    }

    @Override
    public void deleteLevel(Long id) {
        levelRepository.deleteById(id);
    }
    
    @Override
    public long getLevelCount() {
        return levelRepository.count();
    }
}
