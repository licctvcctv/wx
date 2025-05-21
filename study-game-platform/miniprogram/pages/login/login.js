// pages/login/login.js
const app = getApp();

Page({
  data: {
    username: '',
    password: '',
    loading: false
  },

  onLoad: function (options) {
    // 如果已登录，直接跳转到首页
    if (app.globalData.token) {
      wx.switchTab({
        url: '/pages/subjects/subjects',
      });
    }
  },

  // 用户名输入
  onUsernameInput: function(e) {
    this.setData({
      username: e.detail.value
    });
  },

  // 密码输入
  onPasswordInput: function(e) {
    this.setData({
      password: e.detail.value
    });
  },

  // 执行登录
  doLogin: function() {
    const { username, password } = this.data;
    
    if (!username.trim()) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none'
      });
      return;
    }
    
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }
    
    if (this.data.loading) return;
    
    this.setData({
      loading: true
    });

    // 调用API登录
    wx.request({
      url: app.globalData.baseUrl + '/api/auth/login',
      method: 'POST',
      data: {
        username: username,
        password: password
      },
      success: (res) => {
        if (res.data.code === 200) {
          // 登录成功，保存token和用户信息
          app.globalData.token = res.data.data.token;
          app.globalData.userInfo = res.data.data.user;
          
          wx.showToast({
            title: '登录成功',
            icon: 'success',
            duration: 1500,
            success: function() {
              setTimeout(function() {
                wx.switchTab({
                  url: '/pages/subjects/subjects'
                });
              }, 1500);
            }
          });
        } else {
          // 登录失败
          wx.showToast({
            title: res.data.message || '登录失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '网络错误，请稍后重试',
          icon: 'none'
        });
      },
      complete: () => {
        this.setData({
          loading: false
        });
      }
    });
  },

  // 跳转到注册页面
  goToRegister: function() {
    wx.navigateTo({
      url: '/pages/register/register'
    });
  },

  // 显示隐私政策
  showPrivacyPolicy: function () {
    wx.showModal({
      title: '用户协议与隐私政策',
      content: '本应用尊重并保护所有使用服务用户的个人隐私权。为了给您提供更准确、更有个性化的服务，本应用会按照本隐私权政策的规定使用和披露您的个人信息。',
      showCancel: false,
      confirmText: '我知道了'
    });
  }
});
