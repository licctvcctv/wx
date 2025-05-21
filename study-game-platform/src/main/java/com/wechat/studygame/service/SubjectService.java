package com.wechat.studygame.service;

import com.wechat.studygame.model.dto.SubjectProgressDTO;
import com.wechat.studygame.model.entity.Subject;

import java.util.List;

/**
 * 学科服务接口
 */
public interface SubjectService {

    /**
     * 获取所有学科（包括禁用的）
     *
     * @return 学科列表
     */
    List<Subject> getAllSubjects();

    /**
     * 获取所有启用的学科
     *
     * @return 学科列表
     */
    List<Subject> getAllActiveSubjects();

    /**
     * 根据ID获取学科
     *
     * @param id 学科ID
     * @return 学科
     */
    Subject getSubjectById(Long id);

    /**
     * 保存或更新学科
     *
     * @param subject 学科
     * @return 保存后的学科
     */
    Subject saveOrUpdateSubject(Subject subject);

    /**
     * 删除学科
     *
     * @param id 学科ID
     */
    void deleteSubject(Long id);
    
    /**
     * 获取学科总数
     *
     * @return 学科总数
     */
    long getSubjectCount();
    
    /**
     * 获取用户的学科进度
     *
     * @param userId 用户ID
     * @return 学科进度列表
     */
    List<SubjectProgressDTO> getUserSubjectsProgress(Long userId);
    
    /**
     * 获取用户特定学科的进度
     *
     * @param userId 用户ID
     * @param subjectId 学科ID
     * @return 学科进度
     */
    SubjectProgressDTO getUserSubjectProgress(Long userId, Long subjectId);
}
