package com.wechat.studygame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关卡进度数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelProgressDTO {

    /**
     * 关卡ID
     */
    private Long levelId;

    /**
     * 关卡名称
     */
    private String levelName;

    /**
     * 完成率（百分比）
     */
    private Integer completionRate;

    /**
     * 关卡总题目数
     */
    private Integer totalQuestions;

    /**
     * 已正确回答的题目数
     */
    private Integer correctAnswers;

    /**
     * 是否已完成 (当完成率达到100%或满足关卡通关条件时为true)
     */
    private Boolean completed;
}
