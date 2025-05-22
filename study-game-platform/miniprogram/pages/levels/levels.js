// pages/levels/levels.js
const app = getApp();

Page({
  data: {
    chapterId: null,
    subjectId: null,
    chapter: null,
    levels: [],
    loading: true
  },

  onLoad: function (options) {
    if (options.chapterId) {
      this.setData({
        chapterId: options.chapterId,
        subjectId: options.subjectId || null
      });
      this.loadChapterInfo(options.chapterId);
      this.loadLevels(options.chapterId);
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

  // 加载章节信息
  loadChapterInfo: function (chapterId) {
    const that = this;
    
    app.request({
      url: '/api/chapters/' + chapterId,
      success: function(res) {
        if (res.code === 200) {
          that.setData({
            chapter: res.data
          });
        } else {
          wx.showToast({
            title: '获取章节信息失败',
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

  // 加载关卡列表
  loadLevels: function (chapterId) {
    const that = this;
    
    that.setData({ loading: true });
    
    // 先获取关卡列表
    app.request({
      url: '/api/levels/by-chapter/' + chapterId,
      success: function(res) {
        if (res.code === 200) {
          const levels = res.data;
          
          // 处理难度级别星星显示
          levels.forEach(item => {
            item.difficultyLevel = new Array(item.difficultyLevel || 1).fill(1);
          });
          
          // 获取用户完成情况，判断关卡解锁状态
          that.getCompletedLevels(chapterId, levels);
        } else {
          that.setData({ loading: false });
          wx.showToast({
            title: '获取关卡列表失败',
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
  
  // 获取用户完成的关卡信息
  getCompletedLevels: function(chapterId, levels) {
    const that = this;
    
    // 获取用户答题记录和完成情况
    app.request({
      url: '/api/user-progress/chapter/' + chapterId,
      success: function(res) {
        if (res.code === 200) {
          const userProgressData = res.data; // Assuming res.data is the UserProgress list or wrapper
          const levelProgressMap = new Map();
          if (userProgressData && Array.isArray(userProgressData.levelProgressList)) {
            userProgressData.levelProgressList.forEach(p => levelProgressMap.set(p.levelId, p));
          } else if (Array.isArray(userProgressData)) { // If API returns a list directly
             userProgressData.forEach(p => levelProgressMap.set(p.levelId, p));
          }
          
          levels.forEach((level, index) => {
            const progress = levelProgressMap.get(level.id);

            if (progress) {
              // UserProgress exists for this level
              level.isLocked = false;
              if (progress.status === 2) { // Assuming status 2 means 'completed'
                level.statusText = '已完成';
                level.statusClass = 'status-completed';
              } else { // Assuming status 1 (or any other) means 'unlocked' or 'in progress'
                level.statusText = '可挑战';
                level.statusClass = 'status-available';
              }
            } else {
              // No UserProgress for this level
              if (index === 0) {
                // First level is unlocked by default if no progress record
                level.isLocked = false;
                level.statusText = '可挑战';
                level.statusClass = 'status-available';
              } else {
                level.isLocked = true;
                level.statusText = '未解锁';
                level.statusClass = 'status-locked';
              }
            }
          });
          
          that.setData({
            levels: levels,
            loading: false
          });
        } else {
          // 如果获取进度失败，使用默认访问权限(只有第一关可访问)
          that.setDefaultLevelStatus(levels);
        }
      },
      fail: function() {
        // 如果请求失败，使用默认访问权限(只有第一关可访问)
        that.setDefaultLevelStatus(levels);
      }
    });
  },
  
  // 设置默认的关卡状态(当无法获取用户进度时)
  setDefaultLevelStatus: function(levels) {
    const that = this;
    
    levels.forEach((level, index) => {
      if (index === 0) {
        level.isLocked = false;
        level.statusText = '可挑战';
        level.statusClass = 'status-available';
      } else {
        level.isLocked = true;
        level.statusText = '未解锁';
        level.statusClass = 'status-locked';
      }
    });
    // This function is a fallback, so the original logic for setDefaultLevelStatus is fine.
    // It ensures only the first level is playable if progress data is unavailable.
    
    that.setData({
      levels: levels,
      loading: false
    });
  },

  // 返回上一页
  goBack: function() {
    wx.navigateBack();
  },

  // 开始游戏
  startGame: function(e) {
    const levelId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/game/game?levelId=' + levelId + '&chapterId=' + this.data.chapterId + '&subjectId=' + this.data.subjectId
    });
  },

  // 显示关卡锁定提示
  showLockedTip: function() {
    wx.showModal({
      title: '关卡未解锁',
      content: '请先完成前面的关卡才能解锁此关卡',
      showCancel: false,
      confirmText: '我知道了'
    });
  }
});
