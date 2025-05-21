package com.wechat.studygame.service.impl;

import com.wechat.studygame.model.entity.Chapter;
import com.wechat.studygame.repository.ChapterRepository;
import com.wechat.studygame.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 章节服务实现类
 */
@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Override
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    @Override
    public List<Chapter> getChaptersBySubjectId(Long subjectId) {
        return chapterRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Chapter> getChaptersBySubject(Long subjectId) {
        return chapterRepository.findBySubjectIdAndStatusOrderBySortOrderAsc(subjectId, 1);
    }

    @Override
    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("章节不存在"));
    }

    @Override
    public Chapter saveOrUpdateChapter(Chapter chapter) {
        // 区分新增和更新操作
        if (chapter.getId() != null) {
            // 更新操作，需要保留原始创建时间
            Chapter existingChapter = chapterRepository.findById(chapter.getId())
                    .orElseThrow(() -> new RuntimeException("要更新的章节不存在"));
            
            // 保留原始创建时间
            chapter.setCreateTime(existingChapter.getCreateTime());
            
            // 更新时间由@PreUpdate注解自动设置
        } else {
            // 新增操作，通过@PrePersist注解自动设置创建时间和更新时间
        }
        
        return chapterRepository.save(chapter);
    }

    @Override
    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
    }
    
    @Override
    public long getChapterCount() {
        return chapterRepository.count();
    }
}
