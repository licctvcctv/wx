package com.wechat.studygame.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 章节实体类
 */
@Data
@Entity
@Table(name = "chapters")
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属学科ID
     */
    @Column(nullable = false)
    private Long subjectId;

    /**
     * 章节名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 章节描述
     */
    private String description;

    /**
     * 章节图标URL
     */
    private String iconUrl;

    /**
     * 章节难度级别（1-5）
     */
    private Integer difficultyLevel = 1;

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
