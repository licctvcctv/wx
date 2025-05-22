// pages/wrong-questions/wrong-questions.js
const app = getApp();

Page({
  data: {
    selectedTab: 'levels',
    levelId: null,
    chapterId: null,
    subjectId: null,
    wrongQuestions: [],
    loading: false,
    emptyTips: '暂无错题记录',
    // 分类相关数据
    categoryList: [],
    showCategoryDetail: false,
    currentCategoryId: null,
    currentCategoryName: '',
    categoryTypeName: '关卡', // 当前所选分类类型的名称：关卡/章节/学科
    // 错题分析相关数据
    currentAnalysis: null,
    showAnalysisPanel: false,
    trainingQuestions: [],
    currentTrainingIndex: 0,
    trainingMode: false,
    trainingAnswer: '',
    trainingResult: null,
    // --- Re-attempt Modal Data ---
    showReattemptModal: false,
    reattemptQuestion: null,
    userSelectedReattemptAnswer: '',
    reattemptFeedback: '',
    reattemptFeedbackIsCorrect: false
    // --- End Re-attempt Modal Data ---
  },

  onLoad: function (options) {
    console.log('错题本页面加载，参数:', options);
    
    if (options.levelId) {
      this.setData({
        selectedTab: 'levels',
        levelId: options.levelId
      });
    } else if (options.chapterId) {
      this.setData({
        selectedTab: 'chapters',
        chapterId: options.chapterId
      });
    } else if (options.subjectId) {
      this.setData({
        selectedTab: 'subjects',
        subjectId: options.subjectId
      });
    }
    
    // 页面载入时立即加载分类列表
    this.loadCategoryList();
  },

  onShow: function () {
    // 加载分类列表
    this.loadCategoryList();
  },

  switchTab: function (e) {
    const tab = e.currentTarget.dataset.tab;
    
    // 切换到不同的分类标签
    this.setData({
      selectedTab: tab,
      wrongQuestions: [], // 清空现有错题列表
      categoryList: [], // 清空分类列表
      showCategoryDetail: false, // 返回到分类列表模式
      currentCategoryId: null,
      currentCategoryName: '',
      categoryTypeName: tab === 'levels' ? '关卡' : 
                       tab === 'chapters' ? '章节' : 
                       tab === 'subjects' ? '学科' : '分类',
      emptyTips: tab === 'levels' ? '暂无关卡错题记录' : 
                tab === 'chapters' ? '暂无章节错题记录' : 
                tab === 'subjects' ? '暂无学科错题记录' : '暂无错题记录'
    });
    
    // 加载分类列表
    this.loadCategoryList();
  },

  goBack: function () {
    wx.navigateBack();
  },
  
  // 加载分类列表
  loadCategoryList: function() {
    const that = this;
    that.setData({
      loading: true
    });
    
    // 根据选择的分类类型加载相应的分类列表
    const { selectedTab } = that.data;
    let url = '';
    
    if (selectedTab === 'subjects') {
      url = '/api/subjects/with-wrong-questions';
    } else if (selectedTab === 'chapters') {
      url = '/api/chapters/with-wrong-questions';
    } else {
      url = '/api/levels/with-wrong-questions';
    }
    
    // 调用接口获取分类列表
    const app = getApp();
    wx.showLoading({
      title: '加载中',
    });
    
    app.request({
      url: url,
      success: function(res) {
        // 打印响应数据以便调试
        console.log('分类数据响应:', that.data.selectedTab, res);
        
        if (res.code === 0 || res.code === 200) {
          // 处理分类数据
          let categoryList = res.data || [];
          
          console.log('分类列表数据:', categoryList);
          
          // 添加错题数量计数如果没有
          categoryList.forEach(item => {
            if (!item.wrongCount) {
              item.wrongCount = 0;
            }
          });
          
          that.setData({
            categoryList: categoryList,
            loading: false
          });
        } else {
          that.setData({
            categoryList: [],
            loading: false
          });
          wx.showToast({
            title: res.message || '获取分类失败',
            icon: 'none'
          });
        }
      },
      fail: function() {
        that.setData({
          categoryList: [],
          loading: false
        });
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        });
      },
      complete: function() {
        wx.hideLoading();
      }
    });
  },
  
  // 选择某个分类
  selectCategory: function(e) {
    const categoryId = e.currentTarget.dataset.id;
    const categoryName = e.currentTarget.dataset.name;
    console.log('选择分类:', categoryId);
    
    // 记录所选分类
    this.setData({
      currentCategoryId: categoryId,
      currentCategoryName: categoryName,
      showCategoryDetail: true,
      loading: true,
      wrongQuestions: [] // 清空原有错题数据
    });
    
    // 加载错题数据
    this.loadWrongQuestionsByCategory(categoryId);
  },
  
  // 返回分类列表
  backToCategories: function() {
    this.setData({
      showCategoryDetail: false,
      currentCategoryId: null,
      currentCategoryName: '',
      wrongQuestions: [] // 清空原有错题数据
    });
  },

  // 加载特定分类的错题
  loadWrongQuestionsByCategory: function(categoryId) {
    const that = this;
    
    // 如果没有传入categoryId，则使用当前存储的categoryId
    if (!categoryId) {
      categoryId = that.data.currentCategoryId;
    }
    
    console.log('开始加载分类错题，ID:', categoryId);
    
    if (!categoryId) {
      console.log('没有分类 ID，无法加载错题');
      that.setData({loading: false});
      return;
    }

    let url = '/api/wrong-questions';
    let params = {};

    // 根据不同的分类类型设置参数
    if (that.data.selectedTab === 'levels') {
      params.levelId = categoryId;
    } else if (that.data.selectedTab === 'chapters') {
      params.chapterId = categoryId;
    } else if (that.data.selectedTab === 'subjects') {
      params.subjectId = categoryId;
    }

    console.log(`加载${that.data.categoryTypeName}错题:`, categoryId);

    console.log('发送错题请求:', url, params);
    
    app.request({
      url: url,
      data: params,
      success: function(res) {
        console.log('错题请求响应:', res);
        
        if (res.code === 0 || res.code === 200) {
          let wrongQuestions = (res.data || []);
          console.log('返回的错题数量:', wrongQuestions.length);
          
          // 如果是按学科类型查询，并且有学科ID参数，在前端进行过滤
          if (that.data.selectedTab === 'subjects' && params.subjectId) {
            console.log('在前端筛选学科', params.subjectId, '的错题');
            
            // 直接筛选出当前学科的错题，如果题目的subjectId匹配
            wrongQuestions = wrongQuestions.filter(q => {
              // 如果题目直接属于当前学科
              if (q.subjectId && q.subjectId.toString() === params.subjectId.toString()) {
                console.log('匹配到学科题目:', q.id);
                return true;
              }
              
              // 如果题目有学科名称，尝试根据名称匹配
              if (q.subjectName) {
                // 获取当前学科名称（从分类列表中查找）
                const currentCategory = that.data.categoryList.find(c => c.id.toString() === params.subjectId.toString());
                if (currentCategory && currentCategory.name === q.subjectName) {
                  console.log('匹配到学科名称题目:', q.id);
                  return true;
                }
              }
              
              // 如果没有关联学科，我们将题目包含在结果中，用户可以查看所有错题
              if (!q.subjectId) {
                return true;
              }
              
              return false;
            });
            
            console.log('前端过滤后的错题数量:', wrongQuestions.length);
            
            // 完成过滤后更新UI
            that.processAndDisplayQuestions(wrongQuestions, categoryId);
            return; // 异步处理，提前返回
          }
          
          // 处理错题数据 - 添加需要的属性
          const processedQuestions = wrongQuestions.map(question => {
            // 初始化所有必要属性，确保数据结构一致
            if (!question.explanation && question.analysis) {
              question.explanation = question.analysis;
            }
            
            // 添加显示控制属性
            question.showDetail = false;
            
            // 解析options字符串
            try {
              if (question.options && typeof question.options === 'string') {
                const optionsArray = JSON.parse(question.options);
                if (Array.isArray(optionsArray)) {
                  question.optionA = optionsArray[0] || '';
                  question.optionB = optionsArray[1] || '';
                  question.optionC = optionsArray[2] || '';
                  question.optionD = optionsArray[3] || '';
                }
              }
            } catch (e) {
              console.error('解析选项失败:', e);
              // 当解析失败时提供默认选项
              question.optionA = '选项A';
              question.optionB = '选项B';
              question.optionC = '选项C';
              question.optionD = '选项D';
            }
            
            // 如果选项还是没有，这里手动创建示例选项
            if (!question.optionA && !question.optionB) {
              question.optionA = '选项A';
              question.optionB = '选项B';
              question.optionC = '选项C';
              question.optionD = '选项D';
            }
            
            // 处理题目类型
            if (question.type === 1 || question.type === 'SINGLE_CHOICE') {
              question.type = 'SINGLE_CHOICE';
            } else if (question.type === 2 || question.type === 'MULTIPLE_CHOICE') {
              question.type = 'MULTIPLE_CHOICE';
            } else if (question.type === 3 || question.type === 'TRUE_FALSE') {
              question.type = 'TRUE_FALSE';
            } else if (question.type === 4 || question.type === 'FILL_BLANK') {
              question.type = 'FILL_BLANK';
            }
            
            return question;
          });
          
          // 处理完成的错题数据
          console.log('准备显示错题数据，数量:', processedQuestions.length);
          
          that.setData({
            wrongQuestions: processedQuestions,
            loading: false,
            // 确保页面在分类详情模式
            showCategoryDetail: true,
            currentCategoryId: categoryId,
            // 更新当前分类名称（如果有）
            currentCategoryName: that.data.currentCategoryName || '已选分类'
          });
          
          // 检查是否有数据
          if (processedQuestions.length === 0) {
            wx.showToast({
              title: '该分类没有错题',
              icon: 'none'
            });
          }
        } else {
          that.setData({
            wrongQuestions: [],
            loading: false
          });
          wx.showToast({
            title: res.message || '获取错题失败',
            icon: 'none'
          });
        }
      },
      fail: function() {
        that.setData({
          wrongQuestions: [],
          loading: false
        });
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        });
      }
    });
  },
  
  // 处理和显示错题数据
  processAndDisplayQuestions: function(wrongQuestions, categoryId) {
    const that = this;
    console.log('开始处理错题数据，数量:', wrongQuestions.length);
    
    // 处理错题数据 - 添加需要的属性
    const processedQuestions = wrongQuestions.map(question => {
      // 初始化所有必要属性，确保数据结构一致
      if (!question.explanation && question.analysis) {
        question.explanation = question.analysis;
      }
      
      // 添加显示控制属性
      question.showDetail = false;
      
      // 解析options字符串
      try {
        if (question.options && typeof question.options === 'string') {
          const optionsArray = JSON.parse(question.options);
          if (Array.isArray(optionsArray)) {
            question.optionA = optionsArray[0] || '';
            question.optionB = optionsArray[1] || '';
            question.optionC = optionsArray[2] || '';
            question.optionD = optionsArray[3] || '';
          }
        }
        
        // 如果选项还是没有，这里手动创建示例选项
        if (!question.optionA && !question.optionB) {
          question.optionA = '选项A';
          question.optionB = '选项B';
          question.optionC = '选项C';
          question.optionD = '选项D';
        }
      } catch (e) {
        console.error('解析选项失败:', e);
        // 当解析失败时提供默认选项
        question.optionA = '选项A';
        question.optionB = '选项B';
        question.optionC = '选项C';
        question.optionD = '选项D';
      }
      
      // 处理题目类型
      if (question.type === 1 || question.type === '1') {
        question.typeText = '单选题';
      } else if (question.type === 2 || question.type === '2') {
        question.typeText = '多选题';
      } else if (question.type === 3 || question.type === '3') {
        question.typeText = '判断题';
      } else {
        question.typeText = '其他';
      }
      
      return question;
    });
    
    console.log('处理后的错题数据:', processedQuestions.length);
    
    that.setData({
      wrongQuestions: processedQuestions,
      loading: false,
      // 确保页面在分类详情模式
      showCategoryDetail: true,
      currentCategoryId: categoryId,
      // 更新当前分类名称（如果有）
      currentCategoryName: that.data.currentCategoryName || '已选分类'
    });
    
    // 检查是否有数据
    if (processedQuestions.length === 0) {
      wx.showToast({
        title: '该分类没有错题',
        icon: 'none'
      });
    }
  },
  
  // 已经换成了 loadWrongQuestionsByCategory
  loadWrongQuestions: function() {
    // 兼容旧版本代码，调用新功能
    if (this.data.selectedTab) {
      this.loadCategoryList();
    }
  },
  
  viewQuestionDetail: function (e) {
    const index = e.currentTarget.dataset.index;
    const wrongQuestions = this.data.wrongQuestions;
    
    // 切换当前题目的展开/收起状态
    wrongQuestions[index].showDetail = !wrongQuestions[index].showDetail;
    
    this.setData({
      wrongQuestions: wrongQuestions
    });
  },

  // 新增：显示错题分析
  showAnalysis: function (e) {
    const questionId = e.currentTarget.dataset.id;
    const that = this;
    
    // 显示加载状态
    wx.showLoading({
      title: '加载分析中...',
    });
    
    // 请求错题分析接口
    app.request({
      url: `/api/wrong-questions/${questionId}/analysis`,
      method: 'GET',
      success: function (res) {
        wx.hideLoading();
        if (res.code === 0 || res.code === 200) {
          console.log("错题分析数据:", res.data);
          
          // 确保分析数据中的题目选项处理正确
          const question = res.data.question;
          if (question && question.options) {
            try {
              const optionsArray = JSON.parse(question.options);
              if (Array.isArray(optionsArray)) {
                question.optionA = optionsArray[0] || '';
                question.optionB = optionsArray[1] || '';
                question.optionC = optionsArray[2] || '';
                question.optionD = optionsArray[3] || '';
              }
            } catch (error) {
              console.error("解析原题选项失败:", error);
            }
          }
          
          // 处理训练题目的选项
          if (res.data.trainingQuestions) {
            res.data.trainingQuestions.forEach(item => {
              if (item.question && item.question.options) {
                try {
                  const optionsArray = JSON.parse(item.question.options);
                  if (Array.isArray(optionsArray)) {
                    item.question.optionA = optionsArray[0] || '';
                    item.question.optionB = optionsArray[1] || '';
                    item.question.optionC = optionsArray[2] || '';
                    item.question.optionD = optionsArray[3] || '';
                  }
                } catch (error) {
                  console.error("解析训练题选项失败:", error);
                }
              }
            });
          }
          
          that.setData({
            currentAnalysis: res.data,
            showAnalysisPanel: true,
            trainingQuestions: res.data.trainingQuestions || [],
            currentTrainingIndex: 0,
            trainingMode: false, // 初始不显示训练模式
            trainingAnswer: '',
            trainingResult: null
          });
        } else {
          wx.showToast({
            title: res.message || '获取分析失败',
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
  
  // 关闭分析面板
  closeAnalysis: function () {
    this.setData({
      showAnalysisPanel: false,
      currentAnalysis: null,
      trainingMode: false
    });
  },
  
  // 开始训练模式
  startTraining: function () {
    if (this.data.trainingQuestions.length === 0) {
      wx.showToast({
        title: '暂无训练题目',
        icon: 'none'
      });
      return;
    }
    
    this.setData({
      trainingMode: true,
      trainingAnswer: '',
      trainingResult: null
    });
  },
  
  // 选择训练答案
  selectTrainingAnswer: function (e) {
    const answer = e.currentTarget.dataset.answer;
    this.setData({
      trainingAnswer: answer
    });
  },
  
  // 提交训练答案
  submitTrainingAnswer: function () {
    if (!this.data.trainingAnswer) {
      wx.showToast({
        title: '请选择答案',
        icon: 'none'
      });
      return;
    }
    
    const currentQuestion = this.data.trainingQuestions[this.data.currentTrainingIndex];
    const isCorrect = this.data.trainingAnswer === currentQuestion.correctAnswer;
    
    this.setData({
      trainingResult: {
        isCorrect: isCorrect,
        correctAnswer: currentQuestion.correctAnswer
      }
    });
  },
  
  // 下一道训练题
  nextTrainingQuestion: function () {
    const nextIndex = this.data.currentTrainingIndex + 1;
    
    if (nextIndex >= this.data.trainingQuestions.length) {
      // 训练结束
      wx.showToast({
        title: '训练完成！',
        icon: 'success'
      });
      
      this.setData({
        trainingMode: false
      });
      return;
    }
    
    this.setData({
      currentTrainingIndex: nextIndex,
      trainingAnswer: '',
      trainingResult: null
    });
  },
  
  // 返回到分析面板
  backToAnalysis: function () {
    this.setData({
      trainingMode: false,
      trainingQuestions: [],
      currentTrainingIndex: 0,
      trainingAnswer: '',
      trainingResult: null
    });
  },
  
  // 获取学科名称
  fetchSubjectName: function(question) {
    if (!question || !question.subjectId) return;
    
    // 调用API获取学科信息
    app.request({
      url: '/api/subjects/' + question.subjectId,
      method: 'GET',
      success: (res) => {
        if (res.code === 0 || res.code === 200) {
          // 更新题目对象中的学科名称
          const index = this.data.wrongQuestions.findIndex(q => q.id === question.id);
          if (index > -1) {
            let updatedQuestions = this.data.wrongQuestions;
            updatedQuestions[index].subjectName = res.data.name;
            this.setData({
              wrongQuestions: updatedQuestions
            });
          }
        }
      }
    });
  },
  
  // 获取章节名称
  fetchChapterName: function(question) {
    if (!question || !question.chapterId) return;
    
    // 调用API获取章节信息
    app.request({
      url: '/api/chapters/' + question.chapterId,
      method: 'GET',
      success: (res) => {
        if (res.code === 0 || res.code === 200) {
          // 更新题目对象中的章节名称
          const index = this.data.wrongQuestions.findIndex(q => q.id === question.id);
          if (index > -1) {
            let updatedQuestions = this.data.wrongQuestions;
            updatedQuestions[index].chapterName = res.data.name;
            this.setData({
              wrongQuestions: updatedQuestions
            });
          }
        }
      }
    });
  },
  
  // 获取关卡名称
  fetchLevelName: function(question) {
    if (!question || !question.levelId) return;
    
    // 调用API获取关卡信息
    app.request({
      url: '/api/levels/' + question.levelId,
      method: 'GET',
      success: (res) => {
        if (res.code === 0 || res.code === 200) {
          // 更新题目对象中的关卡名称
          const index = this.data.wrongQuestions.findIndex(q => q.id === question.id);
          if (index > -1) {
            let updatedQuestions = this.data.wrongQuestions;
            updatedQuestions[index].levelName = res.data.name;
            this.setData({
              wrongQuestions: updatedQuestions
            });
          }
        }
      }
    });
  },

  // --- Re-attempt Functionality ---
  parseQuestionOptions: function(question) {
    const parsedOptions = [];
    if (!question) return parsedOptions;

    // Assuming question.type is already normalized (e.g., 'SINGLE_CHOICE', 'TRUE_FALSE')
    // And optionA, optionB, etc. are populated by processAndDisplayQuestions
    
    if (question.type === 'TRUE_FALSE') {
        // Handle TRUE_FALSE specifically if options are not A/B/C/D
        // For now, assume they might use optionA for True, optionB for False
        if (question.optionA) parsedOptions.push({ key: 'A', text: question.optionA }); // Or use 'T' as key
        if (question.optionB) parsedOptions.push({ key: 'B', text: question.optionB }); // Or use 'F' as key
    } else {
        // For SINGLE_CHOICE, MULTIPLE_CHOICE (though re-attempt is usually for single answer)
        if (question.optionA) parsedOptions.push({ key: 'A', text: question.optionA });
        if (question.optionB) parsedOptions.push({ key: 'B', text: question.optionB });
        if (question.optionC) parsedOptions.push({ key: 'C', text: question.optionC });
        if (question.optionD) parsedOptions.push({ key: 'D', text: question.optionD });
    }
    
    // Fallback if options were not in optionA..D but in a JSON string that wasn't fully parsed earlier
    // This part might be redundant if processAndDisplayQuestions is robust
    if (parsedOptions.length === 0 && question.options && typeof question.options === 'string') {
        try {
            const optionsArray = JSON.parse(question.options);
            if (Array.isArray(optionsArray)) {
                const optionKeys = ['A', 'B', 'C', 'D'];
                optionsArray.forEach((optText, index) => {
                    if (index < optionKeys.length) {
                        parsedOptions.push({ key: optionKeys[index], text: optText });
                    }
                });
            }
        } catch (e) {
            console.error("Error parsing question.options in parseQuestionOptions:", e);
        }
    }
    console.log("Parsed options for reattempt: ", parsedOptions);
    return parsedOptions;
  },

  handleOpenReattemptModal: function(e) {
    const question = e.currentTarget.dataset.question;
    console.log("Opening reattempt modal for question:", question);
    const parsedOptions = this.parseQuestionOptions(question);

    this.setData({
      reattemptQuestion: {
        ...question,
        parsedOptions: parsedOptions,
        // Ensure 'answer' field exists, falling back to 'correctAnswer'
        answer: question.correctAnswer || question.answer 
      },
      showReattemptModal: true,
      userSelectedReattemptAnswer: '',
      reattemptFeedback: '',
      reattemptFeedbackIsCorrect: false
    });
  },

  selectReattemptAnswer: function(e) {
    this.setData({
      userSelectedReattemptAnswer: e.currentTarget.dataset.answerKey
    });
  },

  submitReattemptAnswer: function() {
    if (!this.data.userSelectedReattemptAnswer) {
      wx.showToast({ title: '请选择答案', icon: 'none' });
      return;
    }
    const question = this.data.reattemptQuestion;
    const isCorrect = this.data.userSelectedReattemptAnswer === question.answer;
    let feedbackMessage = '';

    if (isCorrect) {
      feedbackMessage = '回答正确！';
    } else {
      feedbackMessage = `回答错误。正确答案是: ${question.answer}。`;
      // Note: question.explanation should contain the analysis text.
    }
    this.setData({
      reattemptFeedback: feedbackMessage,
      reattemptFeedbackIsCorrect: isCorrect
    });
  },

  closeReattemptModal: function() {
    this.setData({
      showReattemptModal: false,
      userSelectedReattemptAnswer: '', // Reset for next time
      reattemptFeedback: '',
      reattemptFeedbackIsCorrect: false
      // reattemptQuestion: null, // Optional: clear question to save memory if modals are heavy
    });
  }
  // --- End Re-attempt Functionality ---
});
