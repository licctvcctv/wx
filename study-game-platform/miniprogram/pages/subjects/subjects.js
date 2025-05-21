// pages/subjects/subjects.js
const app = getApp();

Page({
  data: {
    userInfo: {},
    subjects: [],
    allSubjects: [], // 用于搜索时的备份
    loading: true
  },

  onLoad: function (options) {
    this.setData({
      userInfo: app.globalData.userInfo || {}
    });
  },

  onShow: function () {
    this.checkLogin();
    this.loadSubjects();
  },

  // 检查登录状态
  checkLogin: function () {
    if (!app.checkLogin()) {
      return false;
    }
    return true;
  },

  // 加载学科列表
  loadSubjects: function () {
    const that = this;
    
    that.setData({ loading: true });
    
    // 获取学科基本信息
    app.request({
      url: '/api/subjects',
      success: function(res) {
        if (res.code === 200) {
          const subjects = res.data;
          
          // 为每个学科添加难度描述
          subjects.forEach(item => {
            switch(item.difficultyLevel) {
              case 1:
                item.difficultyText = '入门';
                break;
              case 2:
                item.difficultyText = '基础';
                break;
              case 3:
                item.difficultyText = '进阶';
                break;
              case 4:
                item.difficultyText = '高级';
                break;
              case 5:
                item.difficultyText = '专家';
                break;
              default:
                item.difficultyText = '入门';
            }
          });
          
          that.setData({
            subjects: subjects,
            allSubjects: subjects
          });
          
          // 获取学科进度信息
          that.loadSubjectProgress();
        } else {
          that.setData({ loading: false });
          wx.showToast({
            title: '获取学科列表失败',
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
  
  // 加载学科进度
  loadSubjectProgress: function() {
    const that = this;
    
    // 获取学科进度信息
    app.request({
      url: '/api/user/progress/subjects',
      success: function(res) {
        if (res.code === 0 || res.code === 200) {
          const progressData = res.data || [];
          console.log('学科进度数据:', progressData);
          
          // 将进度数据合并到学科数据中
          const subjects = that.data.subjects.map(subject => {
            const progress = progressData.find(p => p.subjectId === subject.id);
            if (progress) {
              subject.progress = progress.progress;
              subject.completedChapters = progress.completedChapters;
              subject.totalChapters = progress.totalChapters;
              subject.completedLevels = progress.completedLevels;
              subject.totalLevels = progress.totalLevels;
              subject.correctQuestions = progress.correctQuestions;
              subject.totalQuestions = progress.totalQuestions;
            } else {
              subject.progress = '0.0%';
            }
            return subject;
          });
          
          that.setData({
            subjects: subjects,
            allSubjects: subjects,
            loading: false
          });
        } else {
          // 如果获取进度失败，仍然显示学科列表，进度显示为0%
          const subjects = that.data.subjects.map(subject => {
            subject.progress = '0.0%';
            return subject;
          });
          
          that.setData({
            subjects: subjects,
            allSubjects: subjects,
            loading: false
          });
        }
      },
      fail: function(err) {
        // 如果获取进度失败，仍然显示学科列表，进度显示为0%
        const subjects = that.data.subjects.map(subject => {
          subject.progress = '0.0%';
          return subject;
        });
        
        that.setData({
          subjects: subjects,
          allSubjects: subjects,
          loading: false
        });
      }
    });
  },

  // 搜索学科
  onSearchInput: function(e) {
    const keyword = e.detail.value.trim().toLowerCase();
    if (!keyword) {
      this.setData({
        subjects: this.data.allSubjects
      });
      return;
    }
    
    const filtered = this.data.allSubjects.filter(item => 
      item.name.toLowerCase().includes(keyword) || 
      (item.description && item.description.toLowerCase().includes(keyword))
    );
    
    this.setData({
      subjects: filtered
    });
  },

  // 跳转到章节列表
  goToChapters: function(e) {
    const subjectId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/chapters/chapters?subjectId=' + subjectId
    });
  }
});
