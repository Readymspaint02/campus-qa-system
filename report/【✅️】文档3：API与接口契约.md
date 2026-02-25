文档3-API与接口契约.md





# 文档3：API与接口契约

文档版本：v1.0 编写日期：2026年2月 编写人：周震 指导教师：邓娜/张陈晨 关联文档： 《详细设计说明书》- 接口章节 《权限矩阵明细表》- RBAC权限注解规范 《微信小程序开发规范》- 微信特有API集成

## 1. 设计原则

1.1 接口设计核心原则

表格

复制

| 原则编号 | 原则名称     | 校园系统问题            | 智能问答系统对策                   |
| -------- | ------------ | ----------------------- | ---------------------------------- |
| I1       | 统一响应格式 | 小程序与Web端格式不一致 | 强制统一包装，含code/message/data  |
| I2       | 微信生态适配 | 登录态维护困难          | 微信登录Code换Token，支持自动续期  |
| I3       | 权限双重校验 | 学生越权访问管理端      | 后端必须独立校验角色，不依赖前端   |
| I4       | 轻量响应     | 小程序包大小限制        | 分页默认10条，字段精简，图片懒加载 |
| I5       | 离线友好     | 校园网不稳定            | 关键数据本地缓存，失败重试机制     |

## 2. RESTful API规范

2.1 基础规范 URL设计： 基础路径：/api/v1/{module} 版本控制：v1（当前版本），通过URL路径显式声明 模块划分：user | qa | notice | guide | feedback | stats | admin 2.2 统一响应格式 成功响应：

JSON

复制

```json
{
"code": "A0000",
"message": "success",
"data": { },
"timestamp": 1707964200000
}
失败响应：
JSON
{
"code": "B2001",
"message": "搜索关键词不能为空",
"data": null,
"timestamp": 1707964200000
}
2.3 微信小程序专用请求头
Authorization: Bearer {JWT_TOKEN}
X-Wechat-Code: {wx.login获取的code}  // 登录专用
3. 错误码体系
3.1 错误码结构
格式：[级别][模块][序号]
示例：B1001 = 业务错误-用户模块-参数错误

级别：A=成功，B=业务错误，C=系统错误
模块：00=通用，01=用户，02=问答，03=通知，04=办事，05=反馈，09=管理
3.2 核心错误码
错误码	含义	场景
B1001	微信登录失败	code无效或过期
B1002	未绑定学号	访问需实名功能
B2001	搜索关键词为空	问答搜索
B2002	问答不存在	查看详情
B3001	通知不存在	查看通知
B3002	无权限查看此通知	不在可见范围
B4001	无权限操作	学生访问管理接口
C5001	分词服务异常	Jieba服务故障
4. 核心接口清单
4.1 用户模块（微信登录）
4.1.1 微信小程序登录
POST /api/v1/user/wx-login

Request:
{
"code": "微信登录code"
}

Response:
{
"code": "A0000",
"data": {
"token": "eyJhbG...",
"refreshToken": "eyJhbG...",
"expiresIn": 7200,
"isNewUser": false,
"isBound": false  // 是否已绑定学号
}
}
4.1.2 绑定学号
POST /api/v1/user/bind-student
Authorization: Bearer {token}

Request:
{
"studentNo": "20243031126",
"realName": "周震",
"grade": "2024",
"major": "计算机科学与技术",
"college": "计算机系",
"phone": "13800138000",
"smsCode": "123456"
}
4.2 问答模块（核心）
4.2.1 智能搜索
POST /api/v1/qa/search

Request:
{
"query": "如何补办学生证",
"categoryId": null,  // 可选，指定分类
"page": 1,
"size": 10
}

Response:
{
"code": "A0000",
"data": {
"query": "如何补办学生证",
"keywords": ["补办", "学生证"],
"intent": {
  "type": "QUERY_PROCESS",
  "category": "STUDENT_AFFAIRS"
},
"results": [
  {
"id": 1001,
"title": "学生证补办流程",
"content": "1. 登录学工系统...",
"categoryName": "学工服务",
"relevanceScore": 0.95,
"clickCount": 1256
  }
],
"hotSearches": ["课表查询", "奖学金申请"]  // 热门搜索推荐
}
}
4.2.2 获取分类树
GET /api/v1/qa/categories

Response:
{
"code": "A0000",
"data": [
{
  "id": 1,
  "name": "教务服务",
  "children": [
{"id": 11, "name": "选课相关"},
{"id": 12, "name": "考试相关"}
  ]
}
]
}
4.2.3 提交反馈（有用/无用）
POST /api/v1/qa/{qaId}/feedback
Authorization: Bearer {token}

Request:
{
"type": "USEFUL",  // USEFUL | USELESS
"content": "很有帮助，谢谢！"  // 可选，文本反馈
}
4.3 通知模块
4.3.1 获取通知列表
GET /api/v1/notice/list?deptId=&urgency=&page=1&size=20
Authorization: Bearer {token}

Response:
{
"code": "A0000",
"data": {
"list": [
  {
"id": 1001,
"title": "关于2025年寒假安排的通知",
"deptName": "教务处",
"urgency": 2,
"publishTime": "2025-01-10T09:00:00",
"isRead": false,
"isRelated": true  // 是否与学生相关（基于年级/专业匹配）
  }
],
"unreadCount": 5
}
}
4.3.2 订阅/取消订阅部门
POST /api/v1/notice/subscription
Authorization: Bearer {token}

Request:
{
"deptId": 1,
"action": "SUBSCRIBE"  // SUBSCRIBE | UNSUBSCRIBE
}
4.4 办事指南模块
4.4.1 获取办事指南列表
GET /api/v1/guide/list?category=&keyword=

Response:
{
"code": "A0000",
"data": [
{
  "id": 101,
  "title": "学生证补办",
  "categoryName": "学工服务",
  "materials": ["身份证复印件", "照片"],
  "location": "学工处201室",
  "contactPhone": "027-12345678",
  "workTime": "周一至周五 8:30-17:00"
}
]
}
5. 微信小程序专用接口
5.1 订阅消息授权
POST /api/v1/wx/subscribe-message
Authorization: Bearer {token}

Request:
{
"templateIds": ["xxx", "yyy"],  // 需要授权的模板ID列表
"scene": 1001  // 场景值
}
5.2 发送订阅消息（服务端调用微信API）
java
// 服务端调用微信订阅消息发送
WxSubscribeMessage message = new WxSubscribeMessage();
message.setTouser(userOpenid);
message.setTemplateId("xxx");
message.setPage("pages/notice/detail?id=1001");
message.setData({
"thing1": {"value": "教务处"},
"thing2": {"value": "期末考试安排通知"},
"time3": {"value": "2025-01-15"}
});
wxMsgService.sendSubscribeMessage(message);
6. 权限控制规范
6.1 后端权限注解
java
// 学生端接口
@PreAuthorize("hasRole('STUDENT')")

// 信息员端接口
@PreAuthorize("hasRole('INFO_OFFICER')")

// 管理端接口
@PreAuthorize("hasRole('ADMIN')")

// 组合权限
@PreAuthorize("hasAnyRole('INFO_OFFICER', 'ADMIN')")
6.2 接口权限矩阵
接口路径	方法	功能权限	数据权限	特殊规则
/api/v1/qa/search	POST	公开	-	限流100/s
/api/v1/qa/{id}/feedback	POST	STUDENT	本人操作	-
/api/v1/notice/create	POST	INFO_OFFICER	本部门	需审核
/api/v1/notice/audit	POST	ADMIN	全部	敏感操作
/api/v1/admin/qa/all	GET	ADMIN	全部	敏感字段脱敏
7. 幂等性设计
场景	幂等键	存储方式	过期时间
微信登录	code	微信服务端	5
```