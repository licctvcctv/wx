package com.wechat.studygame.model.dto;

import lombok.Data;

/**
 * 仪表盘数据统计DTO
 */
@Data
public class DashboardStats {
    private Long subjectCount;
    private Long chapterCount;
    private Long levelCount;
    private Long questionCount;
    private Long userCount;
}
