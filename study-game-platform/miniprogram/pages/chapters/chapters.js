// pages/chapters/chapters.js
const app = getApp();

Page({
  data: {
    subjectId: null,
    subject: null,
    chapters: [],
    loading: true
  },

  onLoad: function (options) {
    if (options.subjectId) {
      this.setData({
        subjectId: options.subjectId
      });
      this.loadSubjectInfo(options.subjectId);
      this.loadChapters(options.subjectId);
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

  // 检查登录状态
  checkLogin: function () {
    if (!app.checkLogin()) {
      return false;
    }
    return true;
  },

  // 加载学科信息
  loadSubjectInfo: function (subjectId) {
    const that = this;
    
    app.request({
      url: '/api/subjects/' + subjectId,
      success: function(res) {
        if (res.code === 200) {
          that.setData({
            subject: res.data
          });
        } else {
          wx.showToast({
            title: '获取学科信息失败',
            icon: 'none'
          });
        }
      },
      fail: function(err) {
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  // 加载章节列表
  loadChapters: function (subjectId) {
    const that = this;
    
    that.setData({ loading: true });
    
    app.request({
      url: '/api/chapters/by-subject/' + subjectId,
      success: function(res) {
        if (res.code === 200) {
          const chapters = res.data;
          
          // 为每个章节处理数据
          chapters.forEach(item => {
            // 将难度级别转换为数组，用于显示星星
            item.difficultyLevel = new Array(item.difficultyLevel || 1).fill(1);
          });
          
          // 获取用户的实际进度
          that.loadUserProgress(chapters);
          
          that.setData({
            chapters: chapters,
            loading: false
          });
        } else {
          that.setData({ loading: false });
          wx.showToast({
            title: '获取章节列表失败',
            icon: 'none'
          });
        }
      },
      fail: function(err) {
        that.setData({ loading: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  // 返回上一页
  goBack: function() {
    wx.navigateBack();
  },

  // 跳转到关卡列表
  goToLevels: function(e) {
    const chapterId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/levels/levels?chapterId=' + chapterId + '&subjectId=' + this.data.subjectId
    });
  },
  
  // 获取用户的实际进度
  loadUserProgress: function(chapters) {
    if (!chapters || chapters.length === 0) return;
    
    const that = this;
    const subjectId = that.data.subjectId;
    
    app.request({
      url: '/api/user/progress/chapters',
      data: { subjectId: subjectId },
      success: function(res) {
        if (res.code === 200 || res.code === 0) {
          const progressData = res.data || [];
          
          // 更新章节进度
          chapters.forEach(chapter => {
            // 查找该章节的进度数据
            const chapterProgress = progressData.find(p => p.id === chapter.id);
            
            if (chapterProgress && chapterProgress.completionRate !== undefined) {
              // 使用后端返回的实际完成率
              chapter.progress = chapterProgress.completionRate + '%';
            } else {
              // 如果没有进度数据，设为0%
              chapter.progress = '0%';
            }
          });
          
          // 更新UI
          that.setData({
            chapters: chapters
          });
        }
      }
    });
  }
});
