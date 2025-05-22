// pages/userStats/userStats.js
const app = getApp();

Page({
  data: {
    userStats: {}, // From /api/user/stats (learningStats + totalScore)
    chapterStats: {}, // From /api/user/stats (chapterCompletionRate)
    subjectStats: {}, // From /api/user/stats/subjects
    learningTrend: {}, // From /api/user/stats/trend
    maxAnswerCount: 10, // Default for trend chart scaling
    loading: true,
    pageError: null, // For displaying page-level errors
    firstLoad: true, // To show initial full page loading
    ecChartOption: null // For ECharts
  },

  onReady: function () {
    // Attempt to initialize chart after component is ready
    // This might be too early if data isn't fetched yet.
    // Better to initialize after data is available.
  },

  onLoad: function (options) {
    this.loadAllStats();
  },

  onShow: function() {
    // Check login status every time the page is shown,
    // especially if the user might have logged out and returned.
    // However, avoid re-loading data if it's not the first load 
    // unless a pull-down-refresh mechanism is implemented.
    if (!this.data.firstLoad) {
        this.checkLoginStatus(); // Just check login, don't reload data unless necessary
    }
  },
  
  checkLoginStatus: function(callback) {
    if (!app.globalData.token) {
      wx.showModal({
        title: '提示',
        content: '请先登录才能查看学习统计。',
        confirmText: '去登录',
        cancelText: '返回',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login',
            });
          } else if (res.cancel) {
            wx.navigateBack();
          }
        }
      });
      return false; // Not logged in
    }
    if (callback) callback(); // Proceed with callback if logged in
    return true; // Logged in
  },

  loadAllStats: function() {
    this.setData({ loading: true, pageError: null, firstLoad: false });

    // Check login before attempting to load stats
    if (!this.checkLoginStatus()) {
        this.setData({ loading: false, pageError: "请先登录。" }); // Or rely on modal's navigation
        return;
    }

    const fetchUserStats = app.request({ url: '/api/user/stats', method: 'GET' });
    const fetchSubjectStats = app.request({ url: '/api/user/stats/subjects', method: 'GET' });
    const fetchLearningTrend = app.request({ url: '/api/user/stats/trend', method: 'GET' });

    Promise.all([fetchUserStats, fetchSubjectStats, fetchLearningTrend])
      .then(([userStatsRes, subjectStatsRes, learningTrendRes]) => {
        console.log('Raw userStatsRes:', userStatsRes);
        if (userStatsRes.code === 0 || userStatsRes.code === 200) {
          const learningStatsData = userStatsRes.data.learningStats || {};
          const totalUserScore = userStatsRes.data.totalScore || 0; // Global user score
          const userStatsCombined = {
            ...learningStatsData,
            totalScore: totalUserScore 
          };
          this.setData({
            userStats: userStatsCombined,
            chapterStats: userStatsRes.data.chapterCompletionRate || {}
          });
          console.log('Processed userStats:', this.data.userStats);
          console.log('Processed chapterStats:', this.data.chapterStats);
        } else {
          throw new Error(`获取学习统计失败: ${userStatsRes.message || '未知错误'}`);
        }

        console.log('Raw subjectStatsRes:', subjectStatsRes);
        if (subjectStatsRes.code === 0 || subjectStatsRes.code === 200) {
          this.setData({ subjectStats: subjectStatsRes.data || {} });
          console.log('Processed subjectStats:', this.data.subjectStats);
        } else {
          throw new Error(`获取学科学习情况失败: ${subjectStatsRes.message || '未知错误'}`);
        }
        
        console.log('Raw learningTrendRes:', learningTrendRes);
        if (learningTrendRes.code === 0 || learningTrendRes.code === 200) {
          const trendData = learningTrendRes.data || { dates: [], answerCounts: [], correctRates: [] };
          let maxCount = 0;
          if (trendData.answerCounts && trendData.answerCounts.length > 0) {
            maxCount = Math.max(...trendData.answerCounts, 0);
          }
          this.setData({
            learningTrend: trendData,
            maxAnswerCount: maxCount > 0 ? maxCount : 10,
          });
          this.initTrendChart(trendData); // Initialize or update chart with new data
          console.log('Processed learningTrend:', this.data.learningTrend);
        } else {
          throw new Error(`获取学习趋势失败: ${learningTrendRes.message || '未知错误'}`);
        }
      })
      .catch(error => {
        console.error('加载学习统计数据失败:', error);
        this.setData({
          pageError: `加载数据失败: ${error.message || '请检查网络连接或稍后重试。'}`,
          userStats: {}, // Clear potentially partial data
          chapterStats: {},
          subjectStats: {},
          learningTrend: {}
        });
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },
  
  handleRetry: function() {
    this.loadAllStats();
  },

  initTrendChart: function(trendData) {
    if (!trendData || !trendData.dates || trendData.dates.length === 0) {
      this.setData({ ecChartOption: null }); // Clear chart if no data
      return;
    }

    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          crossStyle: {
            color: '#999'
          }
        }
      },
      legend: {
        data: ['答题数', '正确率']
      },
      xAxis: [
        {
          type: 'category',
          data: trendData.dates.map(this.formatDate), // Use formatted dates
          axisPointer: {
            type: 'shadow'
          }
        }
      ],
      yAxis: [
        {
          type: 'value',
          name: '答题数',
          min: 0,
          // max: this.data.maxAnswerCount, // Or let ECharts decide
          axisLabel: {
            formatter: '{value} 题'
          }
        },
        {
          type: 'value',
          name: '正确率',
          min: 0,
          max: 100,
          axisLabel: {
            formatter: '{value} %'
          }
        }
      ],
      series: [
        {
          name: '答题数',
          type: 'bar',
          data: trendData.answerCounts
        },
        {
          name: '正确率',
          type: 'line',
          yAxisIndex: 1, // Use the second y-axis
          data: trendData.correctRates
        }
      ]
    };
    this.setData({ ecChartOption: option });

    // If ec-canvas component is used and needs manual init/setOption
    // This part depends on how ec-canvas is set up in WXML
    const chartComponent = this.selectComponent('#learning-trend-chart'); // Assuming ec-canvas has id="learning-trend-chart"
    if (chartComponent) {
        // The ec-canvas component usually initializes itself if `ec` prop is bound
        // Forcing a setOption might be needed if you want to update it dynamically
        // without re-rendering the whole component.
        // However, simply setting ecChartOption in data and binding {{ecChartOption}}
        // to ec.option in WXML is often enough for the component to pick it up.
        // If the component has an init method:
        // chartComponent.init((canvas, width, height, dpr) => {
        //   const chart = echarts.init(canvas, null, {width: width, height: height, devicePixelRatio: dpr});
        //   chart.setOption(option);
        //   return chart;
        // });
    } else {
        console.warn("ECharts component '#learning-trend-chart' not found. Chart will not be rendered by selectComponent.");
    }
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
