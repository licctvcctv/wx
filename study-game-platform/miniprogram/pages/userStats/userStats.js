// pages/userStats/userStats.js
const app = getApp();

Page({
  data: {
    userStats: {},
    chapterStats: {},
    subjectStats: {},
    learningTrend: {},
    maxAnswerCount: 0,
    loading: true
  },

  onLoad: function (options) {
    this.checkLoginStatus();
    this.loadUserStats();
  },

  checkLoginStatus: function() {
    if (!app.globalData.token) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        confirmText: '去登录',
        success(res) {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login',
            });
          } else {
            wx.navigateBack();
          }
        }
      });
    }
  },

  loadUserStats: function() {
    this.setData({ loading: true });
    
    // 获取用户学习统计概览
    this.getUserLearningStats();
    
    // 获取学科学习情况
    this.getSubjectLearningStats();
    
    // 获取学习趋势
    this.getUserLearningTrend();
  },

  getUserLearningStats: function() {
    app.request({
      url: '/api/user/stats',
      method: 'GET',
      success: (res) => {
        if (res.code === 0 || res.code === 200) {
          // 提取学习统计数据
          const learningStats = res.data.learningStats || {};
          // 提取总分
          const totalScore = res.data.totalScore || 0;
          // 合并数据
          const userStats = {
            ...learningStats,
            totalScore: totalScore
          };
          
          // 提取章节完成率
          const chapterStats = res.data.chapterCompletionRate || {};
          
          this.setData({
            userStats: userStats,
            chapterStats: chapterStats
          });
        } else {
          wx.showToast({
            title: '获取学习统计失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('获取学习统计失败:', err);
        wx.showToast({
          title: '网络错误，请稍后再试',
          icon: 'none'
        });
      }
    });
  },

  getSubjectLearningStats: function() {
    app.request({
      url: '/api/user/stats/subjects',
      method: 'GET',
      success: (res) => {
        if (res.code === 0 || res.code === 200) {
          this.setData({
            subjectStats: res.data
          });
        } else {
          wx.showToast({
            title: '获取学科学习情况失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('获取学科学习情况失败:', err);
      }
    });
  },

  getUserLearningTrend: function() {
    app.request({
      url: '/api/user/stats/trend',
      method: 'GET',
      success: (res) => {
        if (res.code === 0 || res.code === 200) {
          const trend = res.data;
          
          // 找出最大答题数，用于图表高度计算
          let maxCount = 0;
          if (trend.answerCounts && trend.answerCounts.length > 0) {
            maxCount = Math.max(...trend.answerCounts);
          }
          
          this.setData({
            learningTrend: trend,
            maxAnswerCount: maxCount > 0 ? maxCount : 10, // 至少设置为10，避免除0错误
            loading: false
          });
        } else {
          wx.showToast({
            title: '获取学习趋势失败',
            icon: 'none'
          });
          this.setData({ loading: false });
        }
      },
      fail: (err) => {
        console.error('获取学习趋势失败:', err);
        this.setData({ loading: false });
      }
    });
  },

  // 格式化日期，将YYYY-MM-DD转为MM/DD
  formatDate: function(dateStr) {
    if (!dateStr) return '';
    const parts = dateStr.split('-');
    if (parts.length === 3) {
      return parts[1] + '/' + parts[2];
    }
    return dateStr;
  },

  goBack: function() {
    wx.navigateBack();
  }
});
