package com.wechat.studygame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类及错题数量DTO
 * 用于传输分类信息和该分类下错题数量的数据对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithCountDTO {
    
    /**
     * 分类ID（学科ID、章节ID或关卡ID）
     */
    private Long id;
    
    /**
     * 分类名称（学科名称、章节名称或关卡名称）
     */
    private String name;
    
    /**
     * 错题数量
     */
    private Integer wrongCount;
    
    /**
     * 完成率（百分比）
     */
    private Integer completionRate;
}
