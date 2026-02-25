const request = require('../../utils/request');

Page({
  data: {
    list: []
  },
  onLoad() {
    request.get('/campusqa/mini/guide/list', { pageNo: 1, pageSize: 20 })
      .then(res => {
        const records = (res.result && res.result.records) || [];
        this.setData({ list: records });
      })
      .catch(() => {
        wx.showToast({ title: '指南加载失败', icon: 'none' });
      });
  }
});
