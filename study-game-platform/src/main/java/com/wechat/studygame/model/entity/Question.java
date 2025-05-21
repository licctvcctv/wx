package com.wechat.studygame.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 题目实体类
 */
@Data
@Entity
@Table(name = "questions")
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属关卡ID
     */
    @Column(nullable = false)
    private Long levelId;

    /**
     * 所属章节ID（冗余字段，方便查询）
     */
    private Long chapterId;

    /**
     * 所属学科ID（冗余字段，方便查询）
     */
    private Long subjectId;

    /**
     * 题目内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 题目类型 (1: 单选题, 2: 多选题, 3: 判断题, 4: 填空题, 5: 简答题)
     */
    private Integer type;

    /**
     * 题目选项，JSON格式
     */
    @Column(columnDefinition = "TEXT")
    private String options;

    /**
     * 正确答案
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    /**
     * 题目解析
     */
    @Column(columnDefinition = "TEXT")
    private String analysis;

    /**
     * 涉及知识点
     */
    private String knowledgePoints;

    /**
     * 题目难度级别（1-5）
     */
    private Integer difficultyLevel = 1;

    /**
     * 分值
     */
    private Integer score = 10;

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
