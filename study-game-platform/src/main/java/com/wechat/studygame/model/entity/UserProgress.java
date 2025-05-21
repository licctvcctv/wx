package com.wechat.studygame.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户关卡进度实体类
 */
@Data
@Entity
@Table(name = "user_progress")
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 关卡ID
     */
    @Column(nullable = false)
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
     * 进度状态 (0: 未开始, 1: 进行中, 2: 已完成)
     */
    private Integer status = 0;

    /**
     * 正确题目数量
     */
    private Integer correctCount = 0;

    /**
     * 题目总数
     */
    private Integer totalCount = 0;

    /**
     * 获得的总分数
     */
    private Integer totalScore = 0;

    /**
     * 用时（秒）
     */
    private Long timeUsed = 0L;

    /**
     * 最后一次闯关时间
     */
    private LocalDateTime lastAttemptTime;

    /**
     * 首次完成时间
     */
    private LocalDateTime firstCompletionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
