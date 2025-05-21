package com.wechat.studygame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 答题提交请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmitRequest {

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 关卡ID
     */
    private Long levelId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 答题时间（毫秒）
     */
    private Long answerTime;
}
