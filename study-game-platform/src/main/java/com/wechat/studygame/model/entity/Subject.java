package com.wechat.studygame.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 学科实体类
 */
@Data
@Entity
@Table(name = "subjects")
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学科名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 学科描述
     */
    private String description;

    /**
     * 学科图标URL
     */
    private String iconUrl;

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
