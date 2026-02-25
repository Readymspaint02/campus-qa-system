Page({
  data: {
    userId: ''
  },
  onLoad() {
    const userId = wx.getStorageSync('qa_user_id') || '';
    this.setData({ userId });
  },
  onInput(e) {
    this.setData({ userId: (e.detail.value || '').trim() });
  },
  onLogin() {
    const userId = (this.data.userId || '').trim();
    if (!userId) {
      wx.showToast({ title: '请输入学号/用户ID', icon: 'none' });
      return;
    }
    wx.setStorageSync('qa_user_id', userId);
    wx.showToast({ title: '登录成功', icon: 'success' });
    setTimeout(() => {
      wx.switchTab({ url: '/pages/profile/index' });
    }, 300);
  }
});
