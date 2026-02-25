const request = require('../../utils/request');

Page({
  data: {
    keyword: '',
    list: []
  },
  onLoad() {
    this.fetchList();
  },
  fetchList() {
    request.get('/campusqa/mini/knowledge/search', { pageNo: 1, pageSize: 10 })
      .then(res => {
        const records = (res.result && res.result.records) || [];
        this.setData({ list: records });
      })
      .catch(() => {
        wx.showToast({ title: '加载失败', icon: 'none' });
      });
  },
  onInput(e) {
    this.setData({ keyword: e.detail.value });
  },
  onSearch() {
    const keyword = (this.data.keyword || '').trim();
    if (!keyword) {
      wx.showToast({ title: '请输入关键词', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: `/pages/search/index?keyword=${encodeURIComponent(keyword)}` });
  },
  openDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/qa-detail/index?id=${id}` });
  }
});
