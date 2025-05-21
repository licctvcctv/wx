package com.wechat.studygame.controller;

import com.wechat.studygame.model.dto.AnswerSubmitRequest;
import com.wechat.studygame.model.dto.ApiResponse;
import com.wechat.studygame.model.entity.Question;
import com.wechat.studygame.model.entity.UserProgress;
import com.wechat.studygame.service.QuestionService;
import com.wechat.studygame.service.UserAnswerService;
import com.wechat.studygame.service.UserProgressService;
import com.wechat.studygame.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏控制器
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserProgressService userProgressService;

    /**
     * 开始闯关
     *
     * @param levelId 关卡ID
     * @param chapterId 章节ID
     * @param subjectId 学科ID
     * @return 关卡的第一个题目
     */
    @PostMapping("/start")
    public ApiResponse<Map<String, Object>> startLevel(
            @RequestParam Long levelId,
            @RequestParam Long chapterId,
            @RequestParam Long subjectId) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取关卡的所有题目
            List<Question> questions = questionService.getQuestionsByLevel(levelId);
            if (questions.isEmpty()) {
                return ApiResponse.error("当前关卡没有题目");
            }
            
            // 初始化用户进度
            UserProgress progress = userProgressService.initUserProgress(
                    userId, levelId, chapterId, subjectId, questions.size());
            
            // 返回第一道题
            Question firstQuestion = questions.get(0);
            
            Map<String, Object> result = new HashMap<>();
            result.put("progress", progress);
            result.put("question", firstQuestion);
            result.put("totalQuestions", questions.size());
            result.put("currentIndex", 0);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("开始闯关失败：" + e.getMessage());
        }
    }

    /**
     * 提交答案
     *
     * @param request 答题请求
     * @return 答题结果和下一题
     */
    @PostMapping("/submit-answer")
    public ApiResponse<Map<String, Object>> submitAnswer(@RequestBody AnswerSubmitRequest request) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取题目信息
            Question question = questionService.getQuestionById(request.getQuestionId());
            
            // 检查答案是否正确
            boolean isCorrect = questionService.checkAnswer(question.getId(), request.getUserAnswer());
            
            // 检查用户是否已经正确回答过这个题目 (This is for UserAnswer history, not attempt scoring)
            // boolean alreadyAnsweredCorrectly = userAnswerService.hasCorrectlyAnswered(userId, question.getId());
            
            // 计算得分 - Score is awarded if the current answer is correct for this attempt.
            int score = isCorrect ? question.getScore() : 0;
            
            // 保存答题记录
            userAnswerService.saveUserAnswer(
                    userId, question.getId(), request.getLevelId(),
                    question.getChapterId(), question.getSubjectId(),
                    request.getUserAnswer(), isCorrect, score, request.getAnswerTime());
            
            // 获取用户进度
            UserProgress progress = userProgressService.getUserProgressByLevelId(userId, request.getLevelId());

            // 获取关卡所有题目 for calculating currentIndex and for next question logic
            List<Question> questions = questionService.getQuestionsByLevel(request.getLevelId());
            int currentIndex = -1;
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).getId().equals(question.getId())) {
                    currentIndex = i;
                    break;
                }
            }

            Map<String, Object> result = new HashMap<>();

            if (!isCorrect) {
                // Handle incorrect answer - FAIL FAST
                result.put("isCorrect", false);
                result.put("levelFailed", true);
                result.put("message", "答题错误，闯关失败！");
                
                // Score and correct count are from UserProgress *before* this incorrect answer.
                // The UserAnswer for the incorrect answer itself has score 0.
                // So, progress.getTotalScore() and progress.getCorrectCount() are already correct.
                result.put("attemptScore", progress.getTotalScore());
                
                int totalAttemptedQuestions = currentIndex + 1; // Number of questions presented up to and including this one
                double accuracy = 0.0;
                if (totalAttemptedQuestions > 0) {
                    // progress.getCorrectCount() is the count of correct answers *before* this incorrect one
                    accuracy = (double) progress.getCorrectCount() / totalAttemptedQuestions;
                }
                result.put("attemptAccuracy", accuracy);
                // UserProgress status remains 'in progress'. No further updates to UserProgress for incorrect answer.
                return ApiResponse.success(result);
            } else {
                // CORRECT ANSWER LOGIC
                // Update progress (score, correctCount) for the current correct answer
                progress.setCorrectCount(progress.getCorrectCount() + 1);
                progress.setTotalScore(progress.getTotalScore() + score); // score is from question.getScore()
                progress = userProgressService.updateUserProgress(progress);

                result.put("isCorrect", true);
                result.put("score", score); // Score for this specific question
                result.put("progress", progress); // Updated progress

                if (currentIndex < questions.size() - 1) {
                    Question nextQuestion = questions.get(currentIndex + 1);
                    result.put("nextQuestion", nextQuestion);
                    result.put("currentIndex", currentIndex + 1);
                    result.put("isLastQuestion", false);
                } else {
                    result.put("isLastQuestion", true);
                    // Note: The actual level completion (setting UserProgress status to 2)
                    // happens in the /complete endpoint, not here.
                }
                return ApiResponse.success(result);
            }
        } catch (Exception e) {
            return ApiResponse.error("提交答案失败：" + e.getMessage());
        }
    }

    /**
     * 完成关卡
     *
     * @param levelId 关卡ID
     * @param timeUsed 用时（秒）
     * @return 完成结果
     */
    @PostMapping("/complete")
    public ApiResponse<Map<String, Object>> completeLevel(
            @RequestParam Long levelId,
            @RequestParam Long timeUsed) {
        try {
            // 获取当前用户ID
            Long userId = UserUtil.getCurrentUserId();
            
            // 获取用户进度
            UserProgress progress = userProgressService.getUserProgressByLevelId(userId, levelId);
            if (progress == null) {
                return ApiResponse.error("未找到用户关卡进度");
            }
            
            // 完成关卡
            progress = userProgressService.completeLevel(
                    userId, levelId, progress.getCorrectCount(), progress.getTotalScore(), timeUsed);
            
            // Leaderboard feature removed
            // List<UserProgress> leaderboard = userProgressService.getLevelLeaderboard(levelId, 10); 
            
            Map<String, Object> result = new HashMap<>();
            result.put("progress", progress);
            // result.put("leaderboard", leaderboard); // Leaderboard feature removed
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("完成关卡失败：" + e.getMessage());
        }
    }
}
