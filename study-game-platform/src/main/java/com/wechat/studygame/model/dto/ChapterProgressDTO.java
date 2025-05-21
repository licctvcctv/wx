package com.wechat.studygame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 章节进度数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgressDTO {

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 章节名称
     */
    private String chapterName;

    /**
     * 完成率（百分比）
     */
    private Integer completionRate;

    /**
     * 总题目数
     */
    private Integer totalQuestions;

    /**
     * 已正确回答的题目数
     */
    private Integer correctAnswers;

    /**
     * 关卡进度列表
     */
    private List<LevelProgressDTO> levelProgressList;
}
