const request = require('../../utils/request');

Page({
  data: {
    id: '',
    userId: '',
    item: {},
    rating: 'like',
    feedbackText: ''
  },
  onLoad(options) {
    this.setData({
      id: options.id || '',
      userId: wx.getStorageSync('qa_user_id') || ''
    });
    this.fetchDetail();
  },
  onShow() {
    const userId = wx.getStorageSync('qa_user_id') || '';
    if (userId !== this.data.userId) {
      this.setData({ userId });
    }
  },
  fetchDetail() {
    if (!this.data.id) return;
    request.get('/campusqa/mini/knowledge/detail', { id: this.data.id })
      .then(res => {
        const item = res.result || {};
        this.setData({ item });
        this.recordHistory(item.question || '');
      })
      .catch(() => {
        wx.showToast({ title: '加载详情失败', icon: 'none' });
      });
  },
  recordHistory(query) {
    if (!this.data.userId) {
      return;
    }
    request.post('/campusqa/mini/history', {
      userId: this.data.userId,
      query,
      knowledgeId: this.data.id,
      source: 'detail'
    }).catch(() => {});
  },
  ensureLogin() {
    if (this.data.userId) {
      return true;
    }
    wx.showToast({ title: '请先登录', icon: 'none' });
    setTimeout(() => {
      wx.navigateTo({ url: '/pages/login/index' });
    }, 300);
    return false;
  },
  toggleFavorite() {
    if (!this.ensureLogin()) {
      return;
    }
    request.post('/campusqa/mini/favorite/toggle', {
      userId: this.data.userId,
      knowledgeId: this.data.id
    })
      .then(res => {
        wx.showToast({ title: (res.result || '操作成功'), icon: 'none' });
      })
      .catch(() => {
        wx.showToast({ title: '收藏操作失败', icon: 'none' });
      });
  },
  onRatingChange(e) {
    this.setData({ rating: e.detail.value || 'like' });
  },
  onFeedbackInput(e) {
    this.setData({ feedbackText: (e.detail.value || '').trim() });
  },
  submitFeedback() {
    if (!this.ensureLogin()) {
      return;
    }
    if (!this.data.feedbackText) {
      wx.showToast({ title: '请输入反馈内容', icon: 'none' });
      return;
    }
    request.post('/campusqa/mini/feedback', {
      userId: this.data.userId,
      knowledgeId: this.data.id,
      rating: this.data.rating,
      content: this.data.feedbackText
    })
      .then(() => {
        wx.showToast({ title: '反馈已提交', icon: 'success' });
        this.setData({ feedbackText: '', rating: 'like' });
      })
      .catch(() => {
        wx.showToast({ title: '反馈提交失败', icon: 'none' });
      });
  }
});
