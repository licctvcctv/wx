// pages/register/register.js
const app = getApp();

Page({
  data: {
    username: '',
    password: '',
    password2: '',
    nickname: '',
    loading: false
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

  // 确认密码输入
  onPassword2Input: function(e) {
    this.setData({
      password2: e.detail.value
    });
  },

  // 昵称输入
  onNicknameInput: function(e) {
    this.setData({
      nickname: e.detail.value
    });
  },

  // 执行注册
  doRegister: function() {
    const { username, password, password2, nickname } = this.data;
    
    // 表单验证
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
    
    if (password !== password2) {
      wx.showToast({
        title: '两次输入的密码不一致',
        icon: 'none'
      });
      return;
    }
    
    if (!nickname.trim()) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      });
      return;
    }
    
    if (this.data.loading) return;
    
    this.setData({
      loading: true
    });

    console.log('正在注册...', {
      username,
      password,
      nickname
    });

    // 调用API注册
    wx.request({
      url: app.globalData.baseUrl + '/api/auth/register',
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      data: {
        username: username,
        password: password,
        nickname: nickname
      },
      success: (res) => {
        console.log('注册响应:', res);
        if (res.data && res.data.code === 200) {
          // 注册成功
          wx.showToast({
            title: '注册成功',
            icon: 'success',
            duration: 1500,
            success: () => {
              setTimeout(() => {
                // 注册成功后跳转到登录页
                wx.navigateBack();
              }, 1500);
            }
          });
        } else {
          // 注册失败
          wx.showToast({
            title: (res.data && res.data.message) || '注册失败，请重试',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('注册请求失败:', err);
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

  // 跳转到登录页面
  goToLogin: function() {
    wx.navigateBack();
  }
});
