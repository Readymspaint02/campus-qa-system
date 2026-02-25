const request = require('../../utils/request');

Page({
  data: {
    keyword: '',
    list: []
  },
  onLoad(options) {
    const keyword = options.keyword ? decodeURIComponent(options.keyword) : '';
    this.setData({ keyword });
    this.doSearch();
  },
  onInput(e) {
    this.setData({ keyword: e.detail.value });
  },
  doSearch() {
    const keyword = (this.data.keyword || '').trim();
    request.get('/campusqa/mini/knowledge/search', { keyword, pageNo: 1, pageSize: 20 })
      .then(res => {
        const records = (res.result && res.result.records) || [];
        this.setData({ list: records });
        this.recordSearchHistory(keyword);
      })
      .catch(() => {
        wx.showToast({ title: '搜索失败', icon: 'none' });
      });
  },
  recordSearchHistory(keyword) {
    const userId = wx.getStorageSync('qa_user_id') || '';
    if (!userId || !keyword) {
      return;
    }
    request.post('/campusqa/mini/history', {
      userId,
      query: keyword,
      source: 'search'
    }).catch(() => {});
  },
  openDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/qa-detail/index?id=${id}` });
  }
});
