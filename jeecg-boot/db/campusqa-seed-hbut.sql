SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

START TRANSACTION;

-- Ensure core categories exist and are enabled.
INSERT INTO campus_qa_category
  (id, name, parent_id, type, sort, status, remark, del_flag, create_by, update_by)
VALUES
  ('CQA_CAT_EDU', '教务', '0', 'qa', 1, 'enable', '教务与考试类', '0', 'admin', 'admin'),
  ('CQA_CAT_AFF', '学工', '0', 'qa', 2, 'enable', '学工与资助类', '0', 'admin', 'admin'),
  ('CQA_CAT_LOG', '后勤', '0', 'qa', 3, 'enable', '后勤与生活类', '0', 'admin', 'admin')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = VALUES(parent_id),
  type = VALUES(type),
  sort = VALUES(sort),
  status = VALUES(status),
  remark = VALUES(remark),
  del_flag = '0',
  update_by = 'admin',
  update_time = NOW();

-- Knowledge base: derived from real high-frequency QQ messages.
INSERT INTO campus_qa_knowledge
  (id, category_id, question, answer, keywords, tags, hot_flag, hits, status, source, del_flag, create_by, update_by)
VALUES
  ('CQA_KNOW_001', 'CQA_CAT_EDU', '如何查看课表？',
   '登录教务系统后，在“教学安排/个人课表”中查看本学期课表。遇到调停课时，以教务通知和班级通知为准。',
   '课表,教务,上课地点,教学安排', '教务,课表', 1, 128, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_002', 'CQA_CAT_LOG', '校园卡如何挂失？',
   '校园卡丢失后可先在一卡通自助终端或相关服务入口进行挂失，再到一站式服务中心办理补卡。',
   '校园卡,挂失,补办,一卡通', '后勤,一卡通', 1, 96, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_003', 'CQA_CAT_EDU', '英语四六级什么时候报名和缴费？',
   '按教务处发布的通知时间在报名网站完成报名和缴费。报名截止后通常不再补报，考试前需打印本次准考证并核对考试日期。',
   '四六级,报名,缴费,准考证', '教务,考试', 1, 150, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_004', 'CQA_CAT_EDU', '补考和重修怎么安排？',
   '补考按教务部考试安排执行，须携带身份证参加考试；重修按每学期通知报名，按要求完成学习通课程任务并参加重修考试。',
   '补考,重修,考试安排,身份证', '教务,考试', 1, 142, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_005', 'CQA_CAT_AFF', '学费怎么交，助学贷款怎么抵扣？',
   '学费通过学校缴费平台缴纳；已办理国家助学贷款的同学按要求提交受理证明或回执码，系统到账后自动冲抵相应学费。',
   '学费,缴费,贷款抵扣,缴费平台', '学工,资助', 1, 168, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_006', 'CQA_CAT_AFF', '助学贷款回执码交到哪里？',
   '按辅导员通知时间将国家助学贷款回执材料提交至指定办公室（常见为工2-C511），避免影响贷款发放和学费冲抵。',
   '助学贷款,回执码,工2-C511,提交材料', '学工,资助', 1, 134, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_007', 'CQA_CAT_LOG', '学生证丢了怎么补办？',
   '准备学生证补办申请表、证件照，并在系部完成签字盖章后，按通知时间到一站式服务中心办理补办。',
   '学生证,补办,申请表,一站式服务中心', '后勤,证件', 1, 176, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_008', 'CQA_CAT_AFF', '大学生医保怎么缴费和报销？',
   '在学校通知的集中缴费期内完成医保缴费；就医报销按校医院及校外就诊流程执行，保留票据并按规定时间提交材料。',
   '医保,缴费,报销,校医院', '学工,医保', 1, 161, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_009', 'CQA_CAT_AFF', '返校登记和晚点名有什么要求？',
   '假期返校通常需在学习通完成返校登记，按班级通知时间返校并参加晚点名；因特殊情况不能按时返校需提前报备。',
   '返校登记,晚点名,学习通,报备', '学工,返校', 1, 139, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_010', 'CQA_CAT_EDU', '考试需要带哪些证件？',
   '参加课程考试、补考、重修考试通常需携带本人身份证和学生证。证件遗失请提前办理临时证明，避免无法入场。',
   '考试,身份证,学生证,补考', '教务,考试', 1, 146, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_011', 'CQA_CAT_AFF', '一次性求职补贴怎么申请？',
   '符合条件的毕业生在规定截止日前提交申请表、花名册及对应证明材料。已在前学历阶段领取过补贴者通常不能重复申请。',
   '求职补贴,毕业生,申请材料,截止时间', '学工,就业', 1, 118, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_012', 'CQA_CAT_AFF', '就业推荐表和成绩单去哪里办理盖章？',
   '成绩单：填写申请表后可到教务办公室（工2-C303）办理打印；就业推荐表：先在系部办公室（工2-C511）盖章，再到就业办盖章。',
   '就业推荐表,成绩单,盖章,工2-C303,工2-C511', '学工,就业', 1, 122, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_013', 'CQA_CAT_LOG', '宿舍停电或设施损坏怎么报修？',
   '发现宿舍停电、照明损坏或设施故障时，先向宿舍管理或班级负责人报备，再按后勤报修流程登记处理。',
   '宿舍,停电,报修,后勤', '后勤,宿舍', 1, 101, 'enable', 'QQ群消息整理', '0', 'admin', 'admin'),
  ('CQA_KNOW_014', 'CQA_CAT_AFF', '收到助学金或贷款相关“转账”电话怎么办？',
   '涉及助学金、贷款、退款等事项，以学校官方通知为准，不要共享屏幕、不要转账、不要泄露验证码，发现异常立即联系老师。',
   '防诈骗,助学金,贷款,转账,验证码', '学工,安全', 1, 153, 'enable', 'QQ群消息整理', '0', 'admin', 'admin')
ON DUPLICATE KEY UPDATE
  category_id = VALUES(category_id),
  question = VALUES(question),
  answer = VALUES(answer),
  keywords = VALUES(keywords),
  tags = VALUES(tags),
  hot_flag = VALUES(hot_flag),
  hits = VALUES(hits),
  status = VALUES(status),
  source = VALUES(source),
  del_flag = '0',
  update_by = 'admin',
  update_time = NOW();

-- Service guides for mini app "办事指南" page.
INSERT INTO campus_qa_guide
  (id, category_id, title, content, steps, contact, location, status, del_flag, create_by, update_by)
VALUES
  ('CQA_GUIDE_001', 'CQA_CAT_AFF', '学费缴纳与助学贷款抵扣办理指南',
   '适用于普通缴费和助学贷款抵扣场景。请先确认本人缴费状态，再按通知提交回执材料。',
   '1. 登录学校缴费平台核对应缴金额。\\n2. 已办贷款同学提交受理证明/回执码。\\n3. 关注到账与冲抵结果。\\n4. 有异常及时联系班导师或财务老师。',
   '班导师/财务老师', '线上缴费平台 + 工2-C511', 'enable', '0', 'admin', 'admin'),
  ('CQA_GUIDE_002', 'CQA_CAT_AFF', '大学生医保缴费与报销指南',
   '适用于在校大学生医保缴费、校医院就诊和校外就诊报销咨询。',
   '1. 在规定时限内完成医保缴费。\\n2. 保留缴费凭证和就医票据。\\n3. 按校医院/校外流程提交报销材料。\\n4. 关注学院与校医院后续通知。',
   '校医院窗口/班导师', '校医院 + 缴费平台', 'enable', '0', 'admin', 'admin'),
  ('CQA_GUIDE_003', 'CQA_CAT_LOG', '学生证补办办理指南',
   '学生证遗失、信息填写错误或临近考试急需证件时可参考本指南。',
   '1. 下载并填写学生证补办申请表。\\n2. 准备证件照并完成院系签字盖章。\\n3. 按通知时间到一站式服务中心办理。\\n4. 按通知时间领取新证件。',
   '学工办老师', '西区工二一楼一站式服务中心', 'enable', '0', 'admin', 'admin'),
  ('CQA_GUIDE_004', 'CQA_CAT_EDU', '四六级报名与准考证打印指南',
   '适用于英语四六级报名、缴费、准考证打印和考前准备。',
   '1. 查看教务处通知确认报名时间。\\n2. 在报名系统完成报名与缴费。\\n3. 考前打印本次准考证并核对考试时间。\\n4. 考试当天携带身份证、学生证、准考证。',
   '教务部运行办', '教务网站 + 考试教室', 'enable', '0', 'admin', 'admin'),
  ('CQA_GUIDE_005', 'CQA_CAT_EDU', '补考与重修办理指南',
   '适用于课程不及格后的补考与重修流程咨询。',
   '1. 关注教务发布的补考/重修通知。\\n2. 在规定时间完成报名或课程加入。\\n3. 按要求完成学习任务。\\n4. 携带身份证参加考试。',
   '教务部学籍（考试）管理办公室', '工2-C303', 'enable', '0', 'admin', 'admin'),
  ('CQA_GUIDE_006', 'CQA_CAT_AFF', '毕业生一次性求职补贴申请指南',
   '适用于符合条件的毕业生申领一次性求职补贴。',
   '1. 核验本人是否符合申请条件。\\n2. 准备申请表、花名册与佐证材料。\\n3. 按班级通知在截止日前提交。\\n4. 关注公示结果与退回原因。',
   '就业办/班导师', '工2-C511 + 就业系统', 'enable', '0', 'admin', 'admin'),
  ('CQA_GUIDE_007', 'CQA_CAT_AFF', '就业材料盖章办理指南',
   '适用于就业推荐表、成绩单、学籍类证明的办理与盖章。',
   '1. 先确认材料模板与命名格式。\\n2. 成绩单到教务办公室办理。\\n3. 就业推荐表先系部后就业办盖章。\\n4. 预留办理时间，避免临近截止拥堵。',
   '教务老师/就业办老师', '工2-C303、工2-C511、行政楼206', 'enable', '0', 'admin', 'admin'),
  ('CQA_GUIDE_008', 'CQA_CAT_AFF', '假期返校登记与请假报备指南',
   '适用于节假日前后返校统计、晚点名和请假报备。',
   '1. 在学习通完成返校登记。\\n2. 按班级通知时间返校并参加晚点名。\\n3. 因特殊情况不能返校须提前报备。\\n4. 离返校途中注意安全并保持通讯畅通。',
   '班导师/辅导员', '学习通 + 班级群通知地点', 'enable', '0', 'admin', 'admin')
ON DUPLICATE KEY UPDATE
  category_id = VALUES(category_id),
  title = VALUES(title),
  content = VALUES(content),
  steps = VALUES(steps),
  contact = VALUES(contact),
  location = VALUES(location),
  status = VALUES(status),
  del_flag = '0',
  update_by = 'admin',
  update_time = NOW();

-- Notices for mini app "通知公告" page and backend notice list.
INSERT INTO campus_qa_notice
  (id, title, content, dept_code, `level`, status, publish_time, expire_time, attachments, del_flag, create_by, update_by)
VALUES
  ('CQA_NOTICE_001', '【重要】2026年度大学生医保集中缴费提醒',
   '学校已发布2026年度大学生医保集中缴费通知，请在规定时间内完成缴费；已在家乡参保的同学按班级要求提交对应证明。',
   'AFF', 'H', 'published', '2026-02-16 09:00:00', '2026-03-01 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_002', '【教务】全国大学英语四六级报名与缴费截止提醒',
   '请各位同学在教务通知规定时间内完成报名和缴费，逾期不再补报。考试前务必打印本次准考证并核对考试日期。',
   'EDU', 'H', 'published', '2026-02-14 10:00:00', '2026-03-10 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_003', '【考试】补考/重修考试证件要求通知',
   '参加补考、重修考试的学生须携带本人身份证（建议同时携带学生证）。证件遗失需提前办理相关手续，否则可能无法参加考试。',
   'EDU', 'H', 'published', '2026-02-13 08:30:00', '2026-03-31 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_004', '【学工】学生证补办窗口开放通知',
   '近期开放学生证补办服务，请准备申请表、证件照并完成院系盖章后，在指定时间到一站式服务中心办理。',
   'AFF', 'M', 'published', '2026-02-12 14:00:00', '2026-04-01 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_005', '【就业】一次性求职补贴材料提交截止提醒',
   '符合条件的2026届毕业生请按通知在截止日前提交申请材料。材料不齐、类别不符或逾期提交将影响审核结果。',
   'AFF', 'H', 'published', '2026-02-11 11:30:00', '2026-03-20 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_006', '【学工】开学第一课学习通课程学习通知',
   '本学期“开学第一课”已在学习通发布，请按要求完成学习并提交学习截图，逾期未完成将纳入班级统计。',
   'AFF', 'M', 'published', '2026-02-10 09:30:00', '2026-03-15 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_007', '【返校】假期返校登记与晚点名提醒',
   '请同学们按班级要求在学习通完成返校登记，并于规定时间前返校参加晚点名，确有特殊情况请提前报备。',
   'AFF', 'H', 'published', '2026-02-09 16:20:00', '2026-03-05 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_008', '【资助】助学贷款回执材料提交通知',
   '办理生源地助学贷款的同学，请按通知在规定时间将受理证明/回执材料提交到指定办公室，避免影响放款和学费冲抵。',
   'AFF', 'M', 'published', '2026-02-08 10:00:00', '2026-03-18 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_009', '【安全】开学季防诈骗提醒',
   '近期出现冒充老师和贷款工作人员的诈骗行为，请勿轻信转账、共享屏幕、验证码索取等行为，涉及资金事项以官方通知为准。',
   'AFF', 'H', 'published', '2026-02-07 18:00:00', '2026-12-31 23:59:59', NULL, '0', 'admin', 'admin'),
  ('CQA_NOTICE_010', '【就业】就业指导课程与简历提交提醒',
   '就业指导课程作业请按任课老师要求提交PDF简历和相关材料，注意命名格式与截止时间，逾期将影响课程成绩统计。',
   'AFF', 'M', 'published', '2026-02-06 15:00:00', '2026-03-30 23:59:59', NULL, '0', 'admin', 'admin')
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  content = VALUES(content),
  dept_code = VALUES(dept_code),
  `level` = VALUES(`level`),
  status = VALUES(status),
  publish_time = VALUES(publish_time),
  expire_time = VALUES(expire_time),
  attachments = VALUES(attachments),
  del_flag = '0',
  update_by = 'admin',
  update_time = NOW();

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
