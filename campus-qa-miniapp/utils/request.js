const { BASE_URL } = require('./config');

function request(method, url, data, options = {}) {
  const { showError = false, errorMessage = '\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5' } = options;
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + url,
      method,
      data,
      success: (res) => {
        if (res.statusCode !== 200) {
          const message = `\u7f51\u7edc\u5f02\u5e38(${res.statusCode})`;
          if (showError) {
            wx.showToast({ title: message, icon: 'none' });
          }
          const error = new Error(message);
          error.statusCode = res.statusCode;
          error.response = res.data;
          reject(error);
          return;
        }

        const body = res.data || {};
        if (body.success === false) {
          const message = body.message || errorMessage;
          if (showError) {
            wx.showToast({ title: message, icon: 'none' });
          }
          const error = new Error(message);
          error.code = body.code;
          error.response = body;
          reject(error);
          return;
        }

        resolve(body);
      },
      fail: (err) => {
        if (showError) {
          wx.showToast({ title: '\u7f51\u7edc\u8fde\u63a5\u5931\u8d25', icon: 'none' });
        }
        reject(err);
      },
    });
  });
}

function get(url, data, options) {
  return request('GET', url, data, options);
}

function post(url, data, options) {
  return request('POST', url, data, options);
}

module.exports = { get, post };