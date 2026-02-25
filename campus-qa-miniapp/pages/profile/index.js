const request = require('../../utils/request');

Page({
  data: {
    userId: '',
    loading: false,
    historyList: [],
    favoriteList: [],
    subscribeList: [],
  },
  onLoad() {
    this._pageActive = true;
    this._requestSeq = 0;
  },
  onShow() {
    this._pageActive = true;
    this.loadUserAndData();
  },
  onHide() {
    this._pageActive = false;
  },
  onUnload() {
    this._pageActive = false;
    this._requestSeq = (this._requestSeq || 0) + 1;
  },
  safeSetData(patch) {
    if (!this._pageActive) {
      return;
    }
    this.setData(patch);
  },
  loadUserAndData() {
    const userId = wx.getStorageSync('qa_user_id') || '';
    this.safeSetData({ userId });
    if (!userId) {
      this.safeSetData({ historyList: [], favoriteList: [], subscribeList: [] });
      return;
    }
    this.fetchProfileData(userId);
  },
  fetchProfileData(userId) {
    const requestSeq = (this._requestSeq || 0) + 1;
    this._requestSeq = requestSeq;
    this.safeSetData({ loading: true });

    const safeGet = (url, params) =>
      request.get(url, params, { showError: false }).catch(() => null);

    Promise.all([
      safeGet('/campusqa/mini/history/list', { userId, pageNo: 1, pageSize: 20 }),
      safeGet('/campusqa/mini/favorite/list', { userId, pageNo: 1, pageSize: 20 }),
      safeGet('/campusqa/mini/subscribe/list', { userId, pageNo: 1, pageSize: 20 }),
    ])
      .then(([historyRes, favoriteRes, subscribeRes]) => {
        if (!this._pageActive || requestSeq !== this._requestSeq) {
          return;
        }
        const historyList = (historyRes && historyRes.result && historyRes.result.records) || [];
        const favoriteList = (favoriteRes && favoriteRes.result && favoriteRes.result.records) || [];
        const subscribeList = (subscribeRes && subscribeRes.result && subscribeRes.result.records) || [];
        this.safeSetData({ historyList, favoriteList, subscribeList });

        if (!historyRes || !favoriteRes || !subscribeRes) {
          wx.showToast({ title: '\u90e8\u5206\u6570\u636e\u52a0\u8f7d\u5931\u8d25', icon: 'none' });
        }
      })
      .catch(() => {
        if (this._pageActive) {
          wx.showToast({ title: '\u4e2a\u4eba\u4e2d\u5fc3\u52a0\u8f7d\u5931\u8d25', icon: 'none' });
        }
      })
      .finally(() => {
        if (requestSeq === this._requestSeq) {
          this.safeSetData({ loading: false });
        }
      });
  },
  goLogin() {
    wx.navigateTo({ url: '/pages/login/index' });
  },
  logout() {
    wx.removeStorageSync('qa_user_id');
    this.safeSetData({ userId: '', historyList: [], favoriteList: [], subscribeList: [] });
    wx.showToast({ title: '\u5df2\u9000\u51fa', icon: 'none' });
  },
  openHistoryDetail(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) {
      return;
    }
    wx.navigateTo({ url: `/pages/qa-detail/index?id=${id}` });
  },
  openFavoriteDetail(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) {
      return;
    }
    wx.navigateTo({ url: `/pages/qa-detail/index?id=${id}` });
  },
});
