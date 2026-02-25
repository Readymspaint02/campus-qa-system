-- Campus QA menu and permissions
-- Note:
-- 1) root menu parent_id for first-level menu must be '' (or NULL), not '0'
-- 2) this script is idempotent and safe to run multiple times

-- menu ids
-- root: 1e7eef8bc41843e3a6d443c43d900e65
-- knowledge: baf2e1d4648840548e0e32c8c3c9b9ea
-- notice: 1d7006c7ae4d4464b6c2bce24e259fe7
-- guide: 43a6fba88b8f49c293ae86dba7c3415c
-- category: 42870cbc03c64cc28e27a43595237af3
-- feedback: 13b6314ce15b42e19f11eac8ff0609ce
-- stats: c2d9f8c7b5d142f2b71da7f05b1d9a44

-- repair historical wrong root parent_id value
UPDATE `sys_permission`
SET `parent_id` = ''
WHERE `id` = '1e7eef8bc41843e3a6d443c43d900e65' AND `parent_id` = '0';

INSERT IGNORE INTO `sys_permission` VALUES
('1e7eef8bc41843e3a6d443c43d900e65','','校园问答','/campusqa','layouts/RouteView',1,'campusqa','/campusqa/knowledge',0,NULL,'1',80.00,1,'ant-design:question-circle-outlined',0,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('baf2e1d4648840548e0e32c8c3c9b9ea','1e7eef8bc41843e3a6d443c43d900e65','问答库','/campusqa/knowledge','campusqa/knowledge/index',1,'campusqa-knowledge',NULL,1,'campusqa:knowledge:list','1',1.00,0,'ant-design:book-outlined',1,1,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('1d7006c7ae4d4464b6c2bce24e259fe7','1e7eef8bc41843e3a6d443c43d900e65','通知公告','/campusqa/notice','campusqa/notice/index',1,'campusqa-notice',NULL,1,'campusqa:notice:list','1',2.00,0,'ant-design:notification-outlined',1,1,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('43a6fba88b8f49c293ae86dba7c3415c','1e7eef8bc41843e3a6d443c43d900e65','办事指南','/campusqa/guide','campusqa/guide/index',1,'campusqa-guide',NULL,1,'campusqa:guide:list','1',3.00,0,'ant-design:profile-outlined',1,1,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('42870cbc03c64cc28e27a43595237af3','1e7eef8bc41843e3a6d443c43d900e65','分类管理','/campusqa/category','campusqa/category/index',1,'campusqa-category',NULL,1,'campusqa:category:list','1',4.00,0,'ant-design:apartment-outlined',1,1,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('13b6314ce15b42e19f11eac8ff0609ce','1e7eef8bc41843e3a6d443c43d900e65','反馈管理','/campusqa/feedback','campusqa/feedback/index',1,'campusqa-feedback',NULL,1,'campusqa:feedback:list','1',5.00,0,'ant-design:message-outlined',1,1,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('c2d9f8c7b5d142f2b71da7f05b1d9a44','1e7eef8bc41843e3a6d443c43d900e65','统计概览','/campusqa/stats','campusqa/stats/index',1,'campusqa-stats',NULL,1,'campusqa:stats:view','1',6.00,0,'ant-design:bar-chart-outlined',1,1,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('8295c5a7fa014dc2aec21ef91d82fb85','baf2e1d4648840548e0e32c8c3c9b9ea','编辑','',NULL,0,NULL,NULL,2,'campusqa:knowledge:edit','1',1.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('aab89dd20af447b292d6075b183948d5','baf2e1d4648840548e0e32c8c3c9b9ea','删除','',NULL,0,NULL,NULL,2,'campusqa:knowledge:delete','1',2.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('f3213de197fe47008f65a422c8b44bbb','1d7006c7ae4d4464b6c2bce24e259fe7','编辑','',NULL,0,NULL,NULL,2,'campusqa:notice:edit','1',1.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('8a5a5901e0054f3893fada56f68ad5dd','1d7006c7ae4d4464b6c2bce24e259fe7','删除','',NULL,0,NULL,NULL,2,'campusqa:notice:delete','1',2.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('40d7c33fdeb3498f9d3ebcc2872a5fd3','43a6fba88b8f49c293ae86dba7c3415c','编辑','',NULL,0,NULL,NULL,2,'campusqa:guide:edit','1',1.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('cd2076f9c8f049f2b3931f28448731f7','43a6fba88b8f49c293ae86dba7c3415c','删除','',NULL,0,NULL,NULL,2,'campusqa:guide:delete','1',2.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('de757901c3774c77a895ad5d285e07c3','42870cbc03c64cc28e27a43595237af3','编辑','',NULL,0,NULL,NULL,2,'campusqa:category:edit','1',1.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('cf2ff62ab1e940f8b67d672991bab51d','42870cbc03c64cc28e27a43595237af3','删除','',NULL,0,NULL,NULL,2,'campusqa:category:delete','1',2.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('8153db37efb541bead91a119e1fe2eb1','13b6314ce15b42e19f11eac8ff0609ce','回复','',NULL,0,NULL,NULL,2,'campusqa:feedback:reply','1',1.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('6e8e343726984b4abcbe4d0579655a4f','13b6314ce15b42e19f11eac8ff0609ce','删除','',NULL,0,NULL,NULL,2,'campusqa:feedback:delete','1',2.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0),
('4f91f4f9f72248ea9086f627f8b4f74c','c2d9f8c7b5d142f2b71da7f05b1d9a44','查看','',NULL,0,NULL,NULL,2,'campusqa:stats:view','1',1.00,0,NULL,1,0,0,0,NULL,'admin',NOW(),'admin',NOW(),0,0,'1',0);

-- grant to built-in roles: admin + vue3 (if they exist)
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), r.id, p.id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
JOIN `sys_permission` p ON p.id IN (
  '1e7eef8bc41843e3a6d443c43d900e65',
  'baf2e1d4648840548e0e32c8c3c9b9ea',
  '1d7006c7ae4d4464b6c2bce24e259fe7',
  '43a6fba88b8f49c293ae86dba7c3415c',
  '42870cbc03c64cc28e27a43595237af3',
  '13b6314ce15b42e19f11eac8ff0609ce',
  '8295c5a7fa014dc2aec21ef91d82fb85',
  'aab89dd20af447b292d6075b183948d5',
  'f3213de197fe47008f65a422c8b44bbb',
  '8a5a5901e0054f3893fada56f68ad5dd',
  '40d7c33fdeb3498f9d3ebcc2872a5fd3',
  'cd2076f9c8f049f2b3931f28448731f7',
  'de757901c3774c77a895ad5d285e07c3',
  'cf2ff62ab1e940f8b67d672991bab51d',
  '8153db37efb541bead91a119e1fe2eb1',
  '6e8e343726984b4abcbe4d0579655a4f',
  'c2d9f8c7b5d142f2b71da7f05b1d9a44',
  '4f91f4f9f72248ea9086f627f8b4f74c'
)
LEFT JOIN `sys_role_permission` rp ON rp.role_id = r.id AND rp.permission_id = p.id
WHERE r.role_code IN ('admin', 'vue3') AND rp.id IS NULL;
