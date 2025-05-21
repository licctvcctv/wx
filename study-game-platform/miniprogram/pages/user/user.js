// pages/user/user.js
const app = getApp();

Page({
  data: {
    userInfo: {},
    isLoggedIn: false
  },

  onLoad: function (options) {
    this.checkLoginStatus();
  },

  onShow: function () {
    this.checkLoginStatus();
  },

  // 检查登录状态
  checkLoginStatus: function() {
    if (app.globalData.token && app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        isLoggedIn: true
      });
    } else {
      this.setData({
        userInfo: {},
        isLoggedIn: false
      });
    }
  },

  // 我的学习统计
  goToMyStats: function() {
    if (!this.data.isLoggedIn) {
      this.promptLogin();
      return;
    }
    
    wx.navigateTo({
      url: '/pages/userStats/userStats',
    });
  },

  // 我的错题本
  goToWrongQuestions: function() {
    console.log('点击了错题本，登录状态:', this.data.isLoggedIn);
    
    // 恢复登录检查
    if (!this.data.isLoggedIn) {
      this.promptLogin();
      return;
    }
    
    try {
      console.log('尝试切换到错题本标签页');
      
      // 错题本是 TabBar 页面，必须使用 switchTab 而不是 navigateTo
      wx.switchTab({
        url: '/pages/wrong-questions/wrong-questions',
        success: function() {
          console.log('成功切换到错题本标签页');
        },
        fail: function(err) {
          console.error('切换标签失败:', err);
        }
      });
    } catch (error) {
      console.error('跳转错题本页面发生错误:', error);
    }
  },

  // 意见反馈
  goToFeedback: function() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  },

  // 关于我们
  showAbout: function() {
    wx.showModal({
      title: '关于学习闯关平台',
      content: '学习闯关平台是一款面向初中生的趣味学习应用，通过游戏化的方式激发学习兴趣，提高学习效率。',
      showCancel: false,
      confirmText: '我知道了'
    });
  },

  // 退出登录
  logout: function() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout();
        }
      }
    });
  },

  // 提示登录
  promptLogin: function() {
    wx.showModal({
      title: '提示',
      content: '请先登录',
      confirmText: '去登录',
      success: (res) => {
        if (res.confirm) {
          wx.navigateTo({
            url: '/pages/login/login',
          });
        }
      }
    });
  }
});
