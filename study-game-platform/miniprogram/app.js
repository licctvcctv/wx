// app.js
App({
  globalData: {
    userInfo: null,
    token: '',
    baseUrl: 'http://localhost:8080',  // 后端API地址，部署时需要修改
  },

  onLaunch() {
    // 获取本地存储的登录信息
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token && userInfo) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
    }
  },

  // 检查登录状态
  checkLogin: function() {
    if (!this.globalData.token) {
      wx.navigateTo({
        url: '/pages/login/login',
      });
      return false;
    }
    return true;
  },

  // 退出登录
  logout: function() {
    this.globalData.token = '';
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
    
    wx.reLaunch({
      url: '/pages/login/login',
    });
  },

  // 发起请求的统一方法
  request: function(options) {
    const that = this;
    
    // 自动添加token
    const header = options.header || {};
    if (this.globalData.token) {
      header['Authorization'] = 'Bearer ' + this.globalData.token;
    }
    
    // 完整的请求地址
    const url = options.url.startsWith('http') ? options.url : this.globalData.baseUrl + options.url;
    
    wx.request({
      url: url,
      data: options.data,
      method: options.method || 'GET',
      header: header,
      success: function(res) {
        if (res.statusCode === 401) {
          // token过期或无效，跳转到登录页
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none',
            complete: function() {
              setTimeout(function() {
                that.logout();
              }, 1500);
            }
          });
        } else if (res.statusCode === 403) {
          // 权限不足
          wx.showToast({
            title: '您没有权限执行此操作',
            icon: 'none'
          });
          if (options.fail) options.fail(res);
        } else if (res.statusCode === 404) {
          // 资源不存在
          wx.showToast({
            title: '请求的资源不存在',
            icon: 'none'
          });
          if (options.fail) options.fail(res);
        } else if (res.statusCode === 500) {
          // 服务器错误
          wx.showToast({
            title: '服务器错误，请稍后再试',
            icon: 'none'
          });
          if (options.fail) options.fail(res);
        } else if (res.data.code !== 0 && res.data.code !== 200 && options.fail) {
          // 业务错误
          wx.showToast({
            title: res.data.message || '操作失败',
            icon: 'none'
          });
          options.fail(res.data);
        } else if (options.success) {
          // 请求成功
          options.success(res.data);
        }
      },
      fail: function(res) {
        // 网络错误
        wx.showToast({
          title: '网络连接失败，请检查网络设置',
          icon: 'none'
        });
        if (options.fail) {
          options.fail(res);
        }
      },
      complete: function(res) {
        if (options.complete) {
          options.complete(res);
        }
      }
    });
  }
});
