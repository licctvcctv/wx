// pages/index/index.js
const app = getApp();

Page({
  data: {},

  onLoad: function (options) {
    // 进入应用时，检查登录状态
    setTimeout(() => {
      // 检查是否已经登录
      if (app.globalData.token) {
        // 已登录，直接进入主页
        wx.switchTab({
          url: '/pages/subjects/subjects'
        });
      } else {
        // 未登录，跳转到登录页
        wx.redirectTo({
          url: '/pages/login/login'
        });
      }
    }, 2000); // 展示启动页2秒
  }
});
