package com.wechat.studygame.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户答题记录实体类
 */
@Data
@Entity
@Table(name = "user_answers")
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 题目ID
     */
    @Column(nullable = false)
    private Long questionId;

    /**
     * 关卡ID
     */
    private Long levelId;

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 学科ID
     */
    private Long subjectId;

    /**
     * 用户的答案
     */
    @Column(columnDefinition = "TEXT")
    private String userAnswer;

    /**
     * 是否回答正确
     */
    private Boolean isCorrect;

    /**
     * 获得的分数
     */
    private Integer score = 0;

    /**
     * 答题时间（毫秒）
     */
    private Long answerTime;

    /**
     * 是否是首次答题
     */
    private Boolean isFirstAttempt = true;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }
}
