package com.wechat.studygame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学科进度DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectProgressDTO {
    private Long subjectId;
    private String subjectName;
    private String progress;
    private Integer completedChapters;
    private Integer totalChapters;
    private Integer completedLevels;
    private Integer totalLevels;
    private Integer correctQuestions;
    private Integer totalQuestions;
}
