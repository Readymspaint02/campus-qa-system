# 【✅答辩闭环实操步骤（IDEA版）】
日期：2026-02-22  
适用项目：JeecgBoot 校园问答系统（后端 + Vue3 管理端 + 微信小程序）

---

## 1. 先回答你最关心的三个问题
1. IDEA 里到底启动哪个？  
只启动 `org.jeecg.JeecgSystemApplication`（单体启动类），不要启动 cloud 相关启动器。  
参考：`jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/java/org/jeecg/JeecgSystemApplication.java:22`

2. 后端启动目录是哪个？  
`jeecg-boot/jeecg-module-system/jeecg-system-start`

3. 登录账号用哪个？  
管理端：`admin / 123456`  
参考：`README.md:70`  
小程序演示用户：`stu001`（主演示用户）、`stu002`（切换对比用户）  
参考：`report/m2_integration.ps1:85`

---

## 2. 启动前一次性准备（首次必做）
1. 确认 MySQL 已有 `jeecg-boot` 库，且后端配置可连通。  
参考：`jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/resources/application-dev.yml:152`

2. 执行菜单与种子数据 SQL（至少一次）。  
- `jeecg-boot/db/campusqa-menu.sql`  
- `jeecg-boot/db/campusqa-seed-hbut.sql`

3. 若你之前遇到 Shiro rememberMe 报错，先清一次浏览器 Cookie（`localhost`/`127.0.0.1` 下的 `rememberMe`），或用无痕窗口登录后台。

---

## 3. 后端：IDEA 启动步骤（精确到点击）
1. IDEA 打开项目根目录：`d:\desk02\todo_26\codex\zhou_zhen\campus-qa-system`
2. 左侧找到并展开：`jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/java/org/jeecg/JeecgSystemApplication.java`
3. 右键 `JeecgSystemApplication` -> `Run 'JeecgSystemApplication'`
4. 等待控制台出现：`Started JeecgSystemApplication` 和本地地址  
通常是：`http://localhost:8080/jeecg-boot`  
参考：`report/backend-start-m2.log:223`

建议 Run Configuration 关键项：
- Main class：`org.jeecg.JeecgSystemApplication`
- JDK：17
- Active profile：`dev`（可在 VM options 或 Program arguments 配）

---

## 4. 管理端 Vue3：启动步骤
1. 在 IDEA Terminal 进入目录：
```powershell
cd d:\desk02\todo_26\codex\zhou_zhen\campus-qa-system\jeecgboot-vue3
```
2. 首次安装依赖：
```powershell
pnpm install
```
3. 启动前端：
```powershell
pnpm dev
```
4. 浏览器打开：`http://localhost:3100`  
参考：`jeecgboot-vue3/.env:2`
5. 登录：`admin / 123456`（验证码按页面输入）

---

## 5. 微信小程序：启动步骤
1. 打开微信开发者工具 -> 导入项目目录：  
`d:\desk02\todo_26\codex\zhou_zhen\campus-qa-system\campus-qa-miniapp`
2. 确认后端基地址是：`http://127.0.0.1:8080/jeecg-boot`  
参考：`campus-qa-miniapp/utils/config.js:1`
3. 若出现“request 合法域名”报错：  
开发者工具 -> 详情 -> 本地设置 -> 勾选“不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”。

---

## 6. 账号与角色（答辩时这样说）
1. 管理员（后台）：`admin / 123456`
2. 学生A（小程序主流程）：`stu001`
3. 学生B（切换对比）：`stu002`

小程序登录就是保存 `userId` 到本地，不依赖微信授权。  
参考：`campus-qa-miniapp/pages/login/index.wxml:4`、`campus-qa-miniapp/pages/profile/index.js:15`

---

## 7. 一条龙闭环演示（按这个顺序点，最稳）

### 阶段A：后台开场（30-60秒）
1. 浏览器登录后台。
2. 左侧展开 `校园问答` 菜单。
3. 依次点开并停留 2-3 秒：
- `问答库`
- `通知公告`
- `办事指南`
- `分类管理`
- `反馈管理`
- `统计概览`

菜单名称依据：`jeecg-boot/db/campusqa-menu.sql:21` 到 `jeecg-boot/db/campusqa-menu.sql:27`

### 阶段B：小程序产生日志与反馈数据（2-3分钟）
1. 小程序底部点 `我的` -> `去登录`。
2. 输入 `stu001` -> 点 `保存并登录`。
3. 切回 `问答` Tab，输入关键词（例如“补考”）-> `搜索`。
4. 点开一个问答详情。
5. 点 `收藏/取消收藏`（生成收藏数据）。
6. 在“提交反馈”输入一句话（如“这条答案很好，但希望补充办理地点”）-> 点 `提交反馈`。
7. 回到 `我的`，确认：
- `我的收藏` 非空
- `查询历史` 非空

页面与按钮依据：  
`campus-qa-miniapp/pages/index/index.wxml:3`、`campus-qa-miniapp/pages/qa-detail/index.wxml:7`、`campus-qa-miniapp/pages/qa-detail/index.wxml:23`、`campus-qa-miniapp/pages/profile/index.wxml:20`

### 阶段C：补齐“我的订阅”和“意图统计”数据（1分钟）
小程序当前没有“订阅开关”按钮，演示前用一条接口补数据：

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://127.0.0.1:8080/jeecg-boot/campusqa/mini/subscribe/toggle" `
  -ContentType "application/json" `
  -Body '{"userId":"stu001","categoryId":"CQA_CAT_EDU"}'
```

再打一条 ask 接口，让“意图识别分布”一定有数据：

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://127.0.0.1:8080/jeecg-boot/campusqa/mini/ask" `
  -ContentType "application/json" `
  -Body '{"question":"教务处在哪里，办理休学流程是什么？","userId":"stu001"}'
```

然后回小程序 `我的` 页下拉刷新，`我的订阅` 应该出现记录。  
接口依据：`report/m3_integration.ps1:94`、`report/m2_integration.ps1:88`

### 阶段D：切换用户演示数据隔离（40秒）
1. 小程序 `我的` -> `更换账号` -> 输入 `stu002` -> 保存。
2. 回 `我的`，可展示“收藏/订阅/历史为空或明显不同”。
3. 再切回 `stu001`，历史与收藏恢复。

切换按钮依据：`campus-qa-miniapp/pages/profile/index.wxml:15`

### 阶段E：后台处理反馈（1分钟）
1. 回管理端 -> `校园问答` -> `反馈管理`。
2. 搜索 `用户ID = stu001`。
3. 点击该条记录右侧 `回复`。
4. 在弹窗填写：
- `回复内容`：如“已补充说明，感谢反馈”
- `已处理`：打开（是）
5. 保存。

动作依据：`jeecgboot-vue3/src/views/campusqa/feedback/index.vue:39`、`jeecgboot-vue3/src/views/campusqa/feedback/feedback.data.ts:77`

### 阶段F：后台统计回看（40秒）
1. 进入 `统计概览`。
2. 点击右上 `刷新`。
3. 口播观察点：
- `反馈总数/已处理反馈/反馈处理率` 已变化
- `热门问题 TOP10` 有数据
- `意图识别分布` 有数据（来自刚才 `/mini/ask`）

页面依据：`jeecgboot-vue3/src/views/campusqa/stats/index.vue:5`

---

## 8. 对应你的答辩截图顺序（直接对齐）
1. 阶段A 对应：S11-S15  
2. 阶段B+C+D 对应：S05-S10  
3. 阶段F 对应：S15  
4. 接口证据（S16-S22）：建议直接运行 `report/m3_integration.ps1` 后截图 `report/m3-api-results.json`

---

## 9. 现场常见故障 30 秒排查
1. 看不到“校园问答”菜单：重新执行 `campusqa-menu.sql`，然后后台退出重登。
2. 登录反复报 rememberMe/Shiro 警告：清 `rememberMe` Cookie 或开无痕窗口。
3. 小程序请求失败（合法域名）：打开微信开发者工具“不校验合法域名”。
4. 小程序连不到后端：检查 `campus-qa-miniapp/utils/config.js` 是否是 `127.0.0.1:8080/jeecg-boot`。

---

## 10. 最短答辩闭环口令（你可直接照做）
1. 启动后端（IDEA：`JeecgSystemApplication`）  
2. 启动前端（`jeecgboot-vue3` 目录执行 `pnpm dev`）  
3. 管理端 `admin/123456` 登录并展开“校园问答”  
4. 小程序 `stu001`：搜索 -> 详情 -> 收藏 -> 提交反馈 -> 我的页查看  
5. 切换 `stu002` 再切回 `stu001`  
6. 管理端处理反馈为“已处理”  
7. 统计概览点击刷新，完成闭环演示
