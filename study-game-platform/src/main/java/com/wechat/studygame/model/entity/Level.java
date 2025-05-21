package com.wechat.studygame.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 关卡实体类
 */
@Data
@Entity
@Table(name = "levels")
@NoArgsConstructor
@AllArgsConstructor
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属章节ID
     */
    @Column(nullable = false)
    private Long chapterId;

    /**
     * 关卡名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 关卡描述
     */
    private String description;

    /**
     * 关卡难度级别（1-5）
     */
    private Integer difficultyLevel = 1;

    /**
     * 通过所需题目数量
     */
    private Integer requiredCorrectCount;

    /**
     * 是否需要全部答对才能过关
     */
    private Boolean requireAllCorrect = false;

    /**
     * 题目总数
     */
    private Integer totalQuestions;

    /**
     * 每道题的分值基数
     */
    private Integer baseScore = 10;

    /**
     * 排序权重
     */
    private Integer sortOrder = 0;

    /**
     * 是否启用 (0: 禁用, 1: 启用)
     */
    private Integer status = 1;

    /**
     * 创建时间
     */
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
