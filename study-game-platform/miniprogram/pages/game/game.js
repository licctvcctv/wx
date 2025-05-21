// pages/game/game.js
const app = getApp();

Page({
  data: {
    levelId: null,
    chapterId: null,
    subjectId: null,
    gameStarted: false,
    gameCompleted: false,
    currentQuestion: null,
    totalQuestions: 0,
    currentIndex: 0,
    progress: null,
    userAnswer: '',
    userAnswers: [], // 存储多选题答案
    answerResult: null,
    timeUsed: 0,
    timerInterval: null,
    // leaderboard: [], // Removed leaderboard data property
    debug: '' // 用于调试显示
  },

  onLoad: function (options) {
    if (options.levelId && options.chapterId && options.subjectId) {
      this.setData({
        levelId: options.levelId,
        chapterId: options.chapterId,
        subjectId: options.subjectId
      });
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  onShow: function () {
    this.checkLogin();
  },

  onUnload: function() {
    // 清除计时器
    if (this.data.timerInterval) {
      clearInterval(this.data.timerInterval);
    }
  },

  checkLogin: function () {
    if (app.checkLogin()) {
      // 已登录，可以开始游戏
      this.setData({
        userInfo: app.globalData.userInfo
      });
    } else {
      wx.redirectTo({
        url: '/pages/login/login'
      });
    }
  },

  startGame: function () {
    const that = this;
    wx.showLoading({
      title: '加载中...',
    });

    // 开始计时
    let seconds = 0;
    const timerInterval = setInterval(() => {
      seconds++;
      that.setData({
        timeUsed: seconds
      });
    }, 1000);

    that.setData({
      timerInterval: timerInterval
    });

    // 调用开始游戏接口
    app.request({
      url: '/api/game/start',
      method: 'POST',
      dataType: 'json',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data: {
        levelId: that.data.levelId,
        chapterId: that.data.chapterId,
        subjectId: that.data.subjectId
      },
      success: function (res) {
        wx.hideLoading();
        console.log("完整的API响应:", JSON.stringify(res));
        
        if (res.code === 0 || res.code === 200) {
          // 获取题目数据
          const question = res.data.question || {};
          console.log("原始题目数据:", JSON.stringify(question));
          
          // 解析options字符串
          try {
            if (question.options && typeof question.options === 'string') {
              // 尝试解析JSON字符串
              const optionsArray = JSON.parse(question.options);
              if (Array.isArray(optionsArray)) {
                question.optionA = optionsArray[0] || '';
                question.optionB = optionsArray[1] || '';
                question.optionC = optionsArray[2] || '';
                question.optionD = optionsArray[3] || '';
                console.log("解析选项数组成功:", optionsArray);
              }
            }
          } catch (error) {
            console.error("解析选项失败:", error);
            // 解析失败时的备用方案
            question.optionA = question.optionA || '选项A';
            question.optionB = question.optionB || '选项B';
            question.optionC = question.optionC || '选项C';
            question.optionD = question.optionD || '选项D';
          }
          
          console.log("处理后的题目数据:", JSON.stringify(question));
          
          // 将数字类型转换为字符串类型以便于前端判断
          if (question.type === 1) {
            question.type = 'SINGLE_CHOICE'; // 单选题
          } else if (question.type === 2) {
            question.type = 'MULTIPLE_CHOICE'; // 多选题
          } else if (question.type === 3) {
            question.type = 'TRUE_FALSE'; // 判断题
          } else if (question.type === 4) {
            question.type = 'FILL_BLANK'; // 填空题
          }
          
          that.setData({
            gameStarted: true,
            currentQuestion: question,
            totalQuestions: res.data.totalQuestions || 5,
            currentIndex: res.data.currentIndex || 0,
            progress: res.data.progress || {},
            userAnswer: '',
            userAnswers: [],
            answerResult: null,
            debug: '题目类型: ' + question.type // 显示题目类型用于调试
          });
        } else {
          wx.showToast({
            title: res.message || '开始游戏失败',
            icon: 'none'
          });
        }
      },
      fail: function (error) {
        wx.hideLoading();
        console.error('开始游戏失败:', error);
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        });
      }
    });
  },

  // 选择答案
  selectAnswer: function(e) {
    console.log('选择选项:', e.currentTarget.dataset.answer);
    const answer = e.currentTarget.dataset.answer;
    const question = this.data.currentQuestion;
    
    // 强制更新UI提示
    wx.showToast({
      title: '选择了选项' + answer,
      icon: 'none',
      duration: 500
    });
    
    const that = this;
    
    // 判断是否为多选题
    if (question.type === 'MULTIPLE_CHOICE' || question.type == 2) {
      console.log('多选题处理');
      
      // 获取当前已选答案，确保是数组
      let answers = this.data.userAnswers || [];
      console.log('当前已选:', answers);
      
      // 深拷贝数组，避免引用问题
      answers = JSON.parse(JSON.stringify(answers));
      
      // 检查选项是否已存在
      const index = answers.indexOf(answer);
      if (index > -1) {
        // 已选中，取消选择
        answers.splice(index, 1);
      } else {
        // 未选中，添加选择
        answers.push(answer);
      }
      
      // 将多个选项按字母排序并合并为字符串
      const userAnswer = answers.length > 0 ? answers.sort().join(',') : '';
      
      console.log('多选更新 - 已选项:', answers);
      console.log('多选更新 - 合并字符串:', userAnswer);
      
      // 使用setTimeout确保UI更新
      setTimeout(function() {
        that.setData({
          userAnswers: answers,
          userAnswer: userAnswer,
          debug: '选中答案: ' + userAnswer + ' | 多选项: ' + JSON.stringify(answers)
        });
        
        // 检查更新后的状态
        console.log('更新后的状态:', that.data.userAnswers, that.data.userAnswer);
      }, 50);
    } else {
      console.log('单选题处理');
      // 单选题逻辑
      this.setData({
        userAnswer: answer,
        userAnswers: [answer],
        debug: '选中答案: ' + answer
      });
    }
  },

  // 提交答案
  submitAnswer: function () {
    if (!this.data.userAnswer) {
      wx.showToast({
        title: '请选择答案',
        icon: 'none'
      });
      return;
    }

    const that = this;
    wx.showLoading({
      title: '提交中...',
    });

    // 计算答题用时（秒）
    const answerTime = Math.min(30, Math.floor(Math.random() * 10) + 5); // 随机生成5-15秒的答题时间，真实情况下应该精确计算

    // 调用提交答案接口
    app.request({
      url: '/api/game/submit-answer',
      method: 'POST',
      dataType: 'json',
      header: {
        'content-type': 'application/json'
      },
      data: {
        levelId: that.data.levelId,
        questionId: that.data.currentQuestion.id,
        userAnswer: that.data.userAnswer,
        answerTime: answerTime
      },
      success: function (res) {
        wx.hideLoading();
        if ([0, 200].includes(res.code)) {
          if (res.data.levelFailed === true) {
            // Handle "Fail Fast" scenario
            const accuracyPercent = (res.data.attemptAccuracy * 100).toFixed(1) + '%';
            let failContent = `${res.data.message || '答题错误，闯关失败！'}\n`;
            failContent += `本次得分：${res.data.attemptScore}\n`;
            failContent += `正确率：${accuracyPercent}`;

            wx.showModal({
              title: '闯关失败',
              content: failContent,
              showCancel: false,
              confirmText: '返回关卡',
              success: function (modalRes) {
                if (modalRes.confirm) {
                  wx.navigateBack(); // Navigate back to the previous page (e.g., level list)
                }
              }
            });
            return; // Stop further processing
          }

          // If not levelFailed, then the answer was correct.
          // Original logic for correct answer / next question / level completion:
          that.setData({
            answerResult: {
              isCorrect: true, // If levelFailed is false, isCorrect for the question must be true
              score: res.data.score
            },
            progress: res.data.progress
          });

          if (res.data.isLastQuestion) {
            setTimeout(() => {
              that.completeGame();
            }, 1500); // Delay to show feedback for the last question
          } else {
            // Load next question (since it was a correct answer and not the last question)
            const nextQuestion = res.data.nextQuestion || {};
            console.log("原始下一题数据:", JSON.stringify(nextQuestion));
            
            try {
              if (nextQuestion.options && typeof nextQuestion.options === 'string') {
                const optionsArray = JSON.parse(nextQuestion.options);
                if (Array.isArray(optionsArray)) {
                  nextQuestion.optionA = optionsArray[0] || '';
                  nextQuestion.optionB = optionsArray[1] || '';
                  nextQuestion.optionC = optionsArray[2] || '';
                  nextQuestion.optionD = optionsArray[3] || '';
                }
              }
            } catch (error) {
              console.error("解析下一题选项失败:", error);
              nextQuestion.optionA = nextQuestion.optionA || '选项A';
              nextQuestion.optionB = nextQuestion.optionB || '选项B';
              nextQuestion.optionC = nextQuestion.optionC || '选项C';
              nextQuestion.optionD = nextQuestion.optionD || '选项D';
            }
            
            if (nextQuestion.type === 1) nextQuestion.type = 'SINGLE_CHOICE';
            else if (nextQuestion.type === 2) nextQuestion.type = 'MULTIPLE_CHOICE';
            else if (nextQuestion.type === 3) nextQuestion.type = 'TRUE_FALSE';
            else if (nextQuestion.type === 4) nextQuestion.type = 'FILL_BLANK';
            
            console.log("处理后的下一题数据:", JSON.stringify(nextQuestion));
            
            setTimeout(() => {
              that.setData({
                currentQuestion: nextQuestion,
                currentIndex: res.data.currentIndex,
                userAnswer: '',
                userAnswers: [],
                answerResult: null,
                debug: '题目类型: ' + nextQuestion.type
              });
            }, 1500); // Delay to show feedback before loading next question
          }
        } else {
          wx.showToast({
            title: res.message || '提交答案失败',
            icon: 'none'
          });
        }
      },
      fail: function () {
        wx.hideLoading();
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        });
      }
    });
  },

  // 完成游戏
  completeGame: function () {
    const that = this;
    
    // 停止计时
    if (that.data.timerInterval) {
      clearInterval(that.data.timerInterval);
    }

    wx.showLoading({
      title: '提交成绩...',
    });

    // 调用完成游戏接口
    app.request({
      url: '/api/game/complete',
      method: 'POST',
      dataType: 'json',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data: {
        levelId: that.data.levelId,
        timeUsed: that.data.timeUsed
      },
      success: function (res) {
        wx.hideLoading();
        if (res.code === 0 || res.code === 200) {
          that.setData({
            gameCompleted: true,
            progress: res.data.progress
            // leaderboard: res.data.leaderboard // Removed leaderboard data setting
          });
        } else {
          wx.showToast({
            title: res.message || '提交成绩失败',
            icon: 'none'
          });
        }
      },
      fail: function () {
        wx.hideLoading();
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        });
      }
    });
  },

  // 返回关卡列表
  backToLevels: function () {
    wx.navigateBack({
      delta: 1
    });
  },

  // 查看错题
  viewWrongQuestions: function () {
    wx.navigateTo({
      url: '/pages/wrong-questions/wrong-questions?levelId=' + this.data.levelId
    });
  },

  // 重新开始游戏
  restartGame: function () {
    this.setData({
      gameStarted: false,
      gameCompleted: false,
      currentQuestion: null,
      userAnswer: '',
      answerResult: null,
      timeUsed: 0
    });
  },

  // 格式化时间显示
  formatTime: function (seconds) {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  },
  
  // 重试当前题目
  retryQuestion: function() {
    console.log('重试当前题目');
    // 清空用户选择，允许重新选择
    this.setData({
      userAnswer: '',
      userAnswers: [],
      answerResult: null,
      debug: '题目类型: ' + this.data.currentQuestion.type
    });
    
    // 显示重试提示
    wx.showToast({
      title: '请重新作答',
      icon: 'none',
      duration: 1500
    });
  }
});
