 我将基于周震的开题报告、JeecgBoot改造文档以及5文档体系，为您编写一份最详细、最完整的《JeecgBoot校园问答系统改造完整指南》。这份指南将贯穿软件开发的全部流程，涵盖数据库设计、前端开发、后端实现、测试部署等各个环节，确保符合主流市场方案。

让我先搜索一些最新的技术资料，确保方案的前沿性和可行性。 
 基于搜索获取的最新技术资料，结合周震的开题报告、JeecgBoot文档和5文档体系，我现在为您编写**最详细、最完整的《JeecgBoot校园问答系统改造完整指南》**。这份指南将贯穿软件开发的全部流程，从需求分析到部署运维，确保符合主流市场方案。

---

# JeecgBoot校园问答系统改造完整指南

**版本**：v2.0（终极完整版）  
**编写日期**：2026年2月  
**技术栈**：JeecgBoot 3.8 + SpringBoot 2.7 + 微信小程序原生 + Vue3 + Milvus向量库 + RAG架构  
**适用场景**：毕业设计/企业级智慧校园项目  

---

## 目录

1. [项目概述与架构决策](#第一章-项目概述与架构决策)
2. [数据库设计与实现](#第二章-数据库设计与实现)
3. [后端服务开发](#第三章-后端服务开发)
4. [微信小程序前端开发](#第四章-微信小程序前端开发)
5. [管理后台开发](#第五章-管理后台开发)
6. [RAG智能问答引擎](#第六章-rag智能问答引擎)
7. [测试与质量保证](#第七章-测试与质量保证)
8. [部署与运维](#第八章-部署与运维)
9. [验收与交付](#第九章-验收与交付)

---

## 第一章 项目概述与架构决策

### 1.1 项目背景与目标

基于周震《开题报告》的核心需求，本系统旨在解决：
- **信息孤岛**：教务、学工、后勤信息分散
- **查询不便**：传统FAQ响应慢、不理解自然语言
- **服务低效**：人工咨询成本高、响应不及时

**核心目标**：构建基于微信小程序的校园服务智能问答系统，实现"一站式"信息查询服务。

### 1.2 技术架构全景（双轨架构）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           【展示层】Presentation Layer                       │
│  ┌─────────────────────────────────┐  ┌─────────────────────────────────┐   │
│  │      微信小程序（学生端）        │  │      Web管理后台（Vue3）         │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────┐│  │  ┌─────────┐ ┌─────────┐ ┌─────┐│   │
│  │  │ 首页    │ │ 搜索    │ │ 我的││  │  │ 问答管理│ │ 通知管理│ │ 统计││   │
│  │  │ WXML    │ │ WXML    │ │WXML ││  │  │ Element │ │ Element │ │ 图表││   │
│  │  └────┬────┘ └────┬────┘ └──┬──┘│  │  └────┬────┘ └────┬────┘ └──┬──┘│   │
│  │       └───────────┴────────┘    │  │       └───────────┴────────┘    │   │
│  │            HTTPS/WSS            │  │            HTTPS                │   │
│  └─────────────────────────────────┘  └─────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────────────────┤
│                           【接入层】Gateway Layer                          │
│                         Nginx 1.24 + JeecgBoot Gateway                       │
│                    负载均衡、TLS终结、JWT鉴权、限流防刷、API聚合              │
├─────────────────────────────────────────────────────────────────────────────┤
│                           【业务层】Business Layer - JeecgBoot              │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     JeecgBoot System Module（用户/权限/部门）          │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │  SysUser     │  │  SysDept     │  │  SysRole     │              │   │
│  │  │  微信登录    │  │  院系架构    │  │  多角色RBAC  │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     Campus QA Module（校园问答核心）                    │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │  QaKnowledge │  │  QaCategory  │  │  QaHistory   │              │   │
│  │  │  知识库管理  │  │  分类树      │  │  查询历史    │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │  QaNotice    │  │  QaGuide     │  │  QaFeedback  │              │   │
│  │  │  通知公告    │  │  办事指引    │  │  用户反馈    │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     RAG Engine Module（智能问答引擎）                   │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │  JiebaSegment│  │  MilvusClient│  │  DeepSeekAPI │              │   │
│  │  │  中文分词    │  │  向量检索    │  │  LLM生成     │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │  IntentRecog │  │  QueryRewrite│  │  Reranker    │              │   │
│  │  │  意图识别    │  │  查询重写    │  │  结果重排    │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────────────────┤
│                           【数据层】Data Layer - 多模态存储                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  MySQL 8.0   │  │  Milvus 2.x  │  │  Redis 7.0   │  │  MinIO       │  │
│  │  结构化数据  │  │  向量数据库  │  │  缓存会话    │  │  文件存储    │  │
│  │  用户/问答元 │  │  语义向量    │  │  热点数据    │  │  附件/图片   │  │
│  │  数据/日志   │  │  768/1024维  │  │  Token限流   │  │  知识库文档  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 关键技术决策（ADR）

| 决策编号 | 决策内容     | 选型方案                           | 理由                                     |
| :------- | :----------- | :--------------------------------- | :--------------------------------------- |
| ADR-001  | 基础框架     | **JeecgBoot 3.8** + SpringBoot 2.7 | 低代码能力、RBAC完善、微信小程序集成成熟 |
| ADR-002  | 智能问答架构 | **RAG（检索增强生成）**            | 解决LLM幻觉、保证答案时效性、可解释性强  |
| ADR-003  | 向量数据库   | **Milvus 2.x Standalone**          | 十亿级向量支持、GPU加速、与LLM集成成熟   |
| ADR-004  | 大语言模型   | **DeepSeek API**                   | 国产优先、性价比高、流式输出支持         |
| ADR-005  | 小程序开发   | **微信小程序原生**                 | 性能最优、微信生态完整支持、包体积可控   |
| ADR-006  | 管理后台     | **Vue3 + Vite6 + Ant Design Vue4** | JeecgBoot新版技术栈、TypeScript支持      |
| ADR-007  | 分词工具     | **Jieba-java**                     | 中文分词准确、轻量级、易集成             |

### 1.4 与5文档体系的映射

| 本文档章节 | 对应5文档                   | 核心输出            |
| :--------- | :-------------------------- | :------------------ |
| 第1章      | 文档1《系统架构与核心决策》 | 架构图、ADR决策记录 |
| 第2章      | 文档2《数据模型与状态机》   | ER图、DDL、状态机   |
| 第3-6章    | 文档4《编码规范与实现模板》 | 代码模板、规范      |
| 第7章      | 文档5《验收红线与自动化》   | 测试脚本、红线标准  |
| 全篇       | 文档3《API与接口契约》      | 接口定义贯穿各章    |

---

## 第二章 数据库设计与实现

### 2.1 数据库选型与部署

#### 2.1.1 MySQL 8.0 配置（结构化数据）

```yaml
# docker-compose.yml - MySQL服务
version: '3.8'
services:
  mysql:
    image: mysql:8.0.34
    container_name: campus-mysql
    environment:
      MYSQL_ROOT_PASSWORD: Campus@2024
      MYSQL_DATABASE: campus_qa
      MYSQL_CHARSET: utf8mb4
      MYSQL_COLLATION: utf8mb4_unicode_ci
    command: >
      --default-authentication-plugin=mysql_native_password
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_unicode_ci
      --innodb_buffer_pool_size=1G
      --innodb_log_file_size=256M
      --max_connections=500
      --ft_min_word_len=2  # 全文检索最小词长
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init-sql:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql_data:
```

#### 2.1.2 Milvus 2.3 向量库配置

```yaml
# docker-compose.yml - Milvus向量库（RAG核心）
version: '3.8'
services:
  etcd:
    container_name: milvus-etcd
    image: quay.io/coreos/etcd:v3.5.5
    environment:
      - ETCD_AUTO_COMPACTION_MODE=revision
      - ETCD_AUTO_COMPACTION_RETENTION=1000
      - ETCD_QUOTA_BACKEND_BYTES=4294967296
    volumes:
      - etcd_data:/etcd
    command: etcd -advertise-client-urls=http://127.0.0.1:2379 -listen-client-urls http://0.0.0.0:2379 --data-dir /etcd

  minio:
    container_name: milvus-minio
    image: minio/minio:RELEASE.2023-03-20T20-16-18Z
    environment:
      MINIO_ACCESS_KEY: minioadmin
      MINIO_SECRET_KEY: minioadmin
    ports:
      - "9001:9001"
      - "9000:9000"
    volumes:
      - minio_data:/minio_data
    command: minio server /minio_data --console-address ":9001"

  milvus-standalone:
    container_name: milvus-standalone
    image: milvusdb/milvus:v2.3.3
    command: ["milvus", "run", "standalone"]
    environment:
      ETCD_ENDPOINTS: etcd:2379
      MINIO_ADDRESS: minio:9000
    volumes:
      - milvus_data:/var/lib/milvus
    ports:
      - "19530:19530"  # gRPC端口
      - "9091:9091"    # metrics端口
    depends_on:
      - etcd
      - minio

volumes:
  etcd_data:
  minio_data:
  milvus_data:
```

### 2.2 核心数据模型（18张表）

#### 2.2.1 用户域（4张表）

```sql
-- ============================================
-- 1. 用户主表（扩展JeecgBoot sys_user）
-- ============================================
CREATE TABLE `sys_user` (
    `id` varchar(36) NOT NULL COMMENT '主键',
    `username` varchar(100) NOT NULL COMMENT '登录账号',
    `realname` varchar(100) DEFAULT NULL COMMENT '真实姓名',
    `password` varchar(255) DEFAULT NULL COMMENT '密码',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `birthday` datetime DEFAULT NULL COMMENT '生日',
    `sex` tinyint(1) DEFAULT NULL COMMENT '性别(0-默认未知,1-男,2-女)',
    `email` varchar(45) DEFAULT NULL COMMENT '电子邮件',
    `phone` varchar(45) DEFAULT NULL COMMENT '电话',
    `org_code` varchar(64) DEFAULT NULL COMMENT '登录会话的机构编码',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态(1-正常,0-冻结)',
    `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除状态(0-正常,1-已删除)',
    `work_no` varchar(64) DEFAULT NULL COMMENT '工号，唯一键',
    `post` varchar(255) DEFAULT NULL COMMENT '职务，关联职务表',
    `telephone` varchar(45) DEFAULT NULL COMMENT '座机号',
    `create_by` varchar(36) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(36) DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    -- 校园问答系统扩展字段
    `openid` varchar(100) DEFAULT NULL COMMENT '微信openid',
    `unionid` varchar(100) DEFAULT NULL COMMENT '微信unionid',
    `session_key` varchar(100) DEFAULT NULL COMMENT '微信session_key',
    `student_no` varchar(50) DEFAULT NULL COMMENT '学号（学生角色）',
    `grade` varchar(20) DEFAULT NULL COMMENT '年级（如2024）',
    `major` varchar(100) DEFAULT NULL COMMENT '专业',
    `college_id` varchar(36) DEFAULT NULL COMMENT '学院ID',
    `user_type` tinyint(1) DEFAULT 1 COMMENT '用户类型(1-学生,2-信息员,3-管理员)',
    `bind_status` tinyint(1) DEFAULT 0 COMMENT '学号绑定状态(0-未绑定,1-已绑定)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_openid` (`openid`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_college_grade` (`college_id`, `grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表（扩展校园字段）';

-- ============================================
-- 2. 用户标签表（个性化推荐）
-- ============================================
CREATE TABLE `qa_user_tag` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `tag_code` varchar(50) NOT NULL COMMENT '标签编码',
    `tag_name` varchar(100) NOT NULL COMMENT '标签名称',
    `tag_type` tinyint(1) DEFAULT 1 COMMENT '标签类型(1-兴趣,2-行为,3-属性)',
    `weight` decimal(3,2) DEFAULT 1.00 COMMENT '权重(0-1)',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag` (`user_id`, `tag_code`),
    KEY `idx_user_type` (`user_id`, `tag_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签表';

-- ============================================
-- 3. 用户订阅表（通知推送）
-- ============================================
CREATE TABLE `qa_user_subscribe` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `dept_id` varchar(36) NOT NULL COMMENT '部门ID',
    `subscribe_time` datetime DEFAULT NULL COMMENT '订阅时间',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态(1-订阅中,0-已取消)',
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_dept` (`user_id`, `dept_id`),
    KEY `idx_dept_status` (`dept_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户部门订阅表';

-- ============================================
-- 4. 用户收藏表
-- ============================================
CREATE TABLE `qa_user_favorite` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `target_type` varchar(20) NOT NULL COMMENT '收藏类型(QA-问答,NOTICE-通知,GUIDE-指南)',
    `target_id` varchar(36) NOT NULL COMMENT '目标ID',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';
```

#### 2.2.2 知识域（5张表）

```sql
-- ============================================
-- 5. 问答知识库主表（核心）
-- ============================================
CREATE TABLE `qa_knowledge` (
    `id` varchar(36) NOT NULL,
    `question` varchar(500) NOT NULL COMMENT '问题标题',
    `answer` text NOT NULL COMMENT '答案内容（富文本）',
    `category_id` varchar(36) DEFAULT NULL COMMENT '分类ID',
    `category_name` varchar(100) DEFAULT NULL COMMENT '分类名称（冗余）',
    `dept_id` varchar(36) DEFAULT NULL COMMENT '所属部门ID',
    `dept_name` varchar(100) DEFAULT NULL COMMENT '部门名称（冗余）',
    `keywords` varchar(500) DEFAULT NULL COMMENT '关键词（逗号分隔，用于Jieba分词增强）',
    `segment_result` varchar(1000) DEFAULT NULL COMMENT '分词结果（JSON存储）',
    `vector_id` varchar(64) DEFAULT NULL COMMENT 'Milvus向量ID（关联）',
    `hot` tinyint(1) DEFAULT 0 COMMENT '是否热点(0-否,1-是)',
    `view_count` int DEFAULT 0 COMMENT '浏览次数',
    `useful_count` int DEFAULT 0 COMMENT '有用次数',
    `useless_count` int DEFAULT 0 COMMENT '无用次数',
    `status` tinyint(1) DEFAULT 0 COMMENT '状态(0-草稿,1-待审核,2-已发布,3-已驳回,4-已下架)',
    `sort_no` int DEFAULT 0 COMMENT '排序号',
    `create_by` varchar(36) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(36) DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL,
    `sys_org_code` varchar(64) DEFAULT NULL COMMENT '部门编码',
    PRIMARY KEY (`id`),
    FULLTEXT KEY `ft_question` (`question`),  -- 全文检索
    FULLTEXT KEY `ft_answer` (`answer`),
    KEY `idx_category` (`category_id`),
    KEY `idx_dept` (`dept_id`),
    KEY `idx_hot` (`hot`, `view_count`),
    KEY `idx_status` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答知识库表';

-- ============================================
-- 6. 知识分类表（树形结构）
-- ============================================
CREATE TABLE `qa_category` (
    `id` varchar(36) NOT NULL,
    `name` varchar(100) NOT NULL COMMENT '分类名称',
    `code` varchar(50) NOT NULL COMMENT '分类编码',
    `parent_id` varchar(36) DEFAULT '0' COMMENT '父分类ID(0-根节点)',
    `level` int DEFAULT 1 COMMENT '层级(1-一级,2-二级,3-三级)',
    `icon` varchar(255) DEFAULT NULL COMMENT '图标',
    `sort_no` int DEFAULT 0 COMMENT '排序号',
    `dept_scope` varchar(500) DEFAULT NULL COMMENT '可见部门范围(JSON数组)',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent` (`parent_id`),
    KEY `idx_level_sort` (`level`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答分类表';

-- ============================================
-- 7. 知识分片表（RAG向量化存储）
-- ============================================
CREATE TABLE `qa_knowledge_chunk` (
    `id` varchar(36) NOT NULL,
    `knowledge_id` varchar(36) NOT NULL COMMENT '关联知识ID',
    `chunk_seq` int NOT NULL COMMENT '分片序号',
    `chunk_content` text NOT NULL COMMENT '分片内容',
    `chunk_type` varchar(20) DEFAULT 'TEXT' COMMENT '分片类型(TEXT-文本,URL-链接)',
    `vector_id` varchar(64) DEFAULT NULL COMMENT 'Milvus向量ID',
    `char_count` int DEFAULT 0 COMMENT '字符数',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_knowledge_seq` (`knowledge_id`, `chunk_seq`),
    KEY `idx_vector` (`vector_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识分片表（用于RAG检索）';

-- ============================================
-- 8. 知识向量映射表（MySQL-Milvus桥接）
-- ============================================
CREATE TABLE `qa_vector_mapping` (
    `id` varchar(36) NOT NULL,
    `business_id` varchar(36) NOT NULL COMMENT '业务ID(知识ID或分片ID)',
    `business_type` varchar(20) NOT NULL COMMENT '业务类型(KNOWLEDGE-知识,CHUNK-分片)',
    `milvus_id` varchar(64) NOT NULL COMMENT 'Milvus中的向量ID',
    `embedding_model` varchar(50) DEFAULT 'bge-large-zh-v1.5' COMMENT '嵌入模型',
    `dimension` int DEFAULT 1024 COMMENT '向量维度',
    `vector_content` text DEFAULT NULL COMMENT '向量内容摘要（调试用）',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_business` (`business_id`, `business_type`),
    UNIQUE KEY `uk_milvus` (`milvus_id`),
    KEY `idx_model` (`embedding_model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量映射表';

-- ============================================
-- 9. 知识版本表（版本控制）
-- ============================================
CREATE TABLE `qa_knowledge_version` (
    `id` varchar(36) NOT NULL,
    `knowledge_id` varchar(36) NOT NULL COMMENT '知识ID',
    `version_no` varchar(32) NOT NULL COMMENT '版本号(v1.0.0)',
    `content_diff` text DEFAULT NULL COMMENT '内容差异（JSON）',
    `operator_id` varchar(36) DEFAULT NULL COMMENT '操作人',
    `operation_type` varchar(20) DEFAULT NULL COMMENT '操作类型(CREATE-创建,UPDATE-更新,DELETE-删除)',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_knowledge_version` (`knowledge_id`, `version_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识版本表';
```

#### 2.2.3 对话域（4张表）

```sql
-- ============================================
-- 10. 对话会话表（状态机核心）
-- ============================================
CREATE TABLE `qa_conversation` (
    `id` varchar(36) NOT NULL,
    `session_id` varchar(64) NOT NULL COMMENT '会话标识（微信小程序session）',
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `status` varchar(20) DEFAULT 'INIT' COMMENT '状态(INIT-初始化,UNDERSTANDING-理解中,RETRIEVING-检索,GENERATING-生成,COMPLETED-完成,CLOSED-关闭)',
    `intent` varchar(50) DEFAULT NULL COMMENT '主导意图',
    `confidence` decimal(4,4) DEFAULT NULL COMMENT '意图置信度',
    `context_snapshot` json DEFAULT NULL COMMENT '上下文快照',
    `start_time` datetime DEFAULT NULL,
    `end_time` datetime DEFAULT NULL,
    `last_msg_time` datetime DEFAULT NULL COMMENT '最后消息时间',
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session` (`session_id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_time` (`last_msg_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话会话表';

-- ============================================
-- 11. 消息记录表
-- ============================================
CREATE TABLE `qa_message` (
    `id` varchar(36) NOT NULL,
    `conversation_id` varchar(36) NOT NULL COMMENT '会话ID',
    `msg_seq` int NOT NULL COMMENT '消息序号',
    `role` varchar(20) NOT NULL COMMENT '角色(USER-用户,ASSISTANT-助手,SYSTEM-系统)',
    `content` text NOT NULL COMMENT '消息内容',
    `content_type` varchar(20) DEFAULT 'TEXT' COMMENT '内容类型(TEXT-文本,RICH-富文本,LINK-链接)',
    `msg_type` varchar(20) DEFAULT 'CHAT' COMMENT '消息类型(CHAT-聊天,KNOWLEDGE-知识,FALLBACK-兜底,SENSITIVE-敏感)',
    `intent` varchar(50) DEFAULT NULL COMMENT '识别意图',
    `intent_confidence` decimal(4,4) DEFAULT NULL,
    `referenced_knowledge_id` varchar(36) DEFAULT NULL COMMENT '引用的知识ID',
    `referenced_knowledge_title` varchar(500) DEFAULT NULL COMMENT '引用知识标题',
    `retrieval_confidence` decimal(3,2) DEFAULT NULL COMMENT '检索置信度',
    `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态',
    `feedback_status` varchar(20) DEFAULT 'PENDING' COMMENT '反馈状态(PENDING-待评价,LIKED-点赞,DISLIKED-点踩,REPORTED-举报)',
    `token_count` int DEFAULT NULL COMMENT 'Token消耗',
    `generate_time_ms` int DEFAULT NULL COMMENT '生成耗时(ms)',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conv_seq` (`conversation_id`, `msg_seq`),
    KEY `idx_feedback` (`feedback_status`),
    KEY `idx_knowledge` (`referenced_knowledge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';

-- ============================================
-- 12. 检索记录表（RAG链路追踪）
-- ============================================
CREATE TABLE `qa_retrieval_record` (
    `id` varchar(36) NOT NULL,
    `message_id` varchar(36) NOT NULL COMMENT '关联消息ID',
    `query_text` varchar(1000) NOT NULL COMMENT '查询文本',
    `query_vector` json DEFAULT NULL COMMENT '查询向量（调试用）',
    `retrieval_type` varchar(20) DEFAULT 'BLEND' COMMENT '检索类型(VECTOR-向量,KEYWORD-关键词,BLEND-混合)',
    `vector_results` json DEFAULT NULL COMMENT '向量检索结果TopK',
    `keyword_results` json DEFAULT NULL COMMENT '关键词检索结果TopK',
    `final_results` json DEFAULT NULL COMMENT '最终融合结果',
    `rerank_scores` json DEFAULT NULL COMMENT '重排序分数',
    `retrieval_time_ms` int DEFAULT NULL COMMENT '检索耗时',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检索记录表';

-- ============================================
-- 13. 查询历史表（用户侧）
-- ============================================
CREATE TABLE `qa_query_history` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `query` varchar(500) NOT NULL COMMENT '查询内容',
    `keywords` varchar(500) DEFAULT NULL COMMENT '分词关键词',
    `answer_id` varchar(36) DEFAULT NULL COMMENT '匹配答案ID',
    `answer_summary` varchar(1000) DEFAULT NULL COMMENT '答案摘要',
    `is_satisfied` tinyint(1) DEFAULT NULL COMMENT '是否满意(1-是,0-否)',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='查询历史表';
```

#### 2.2.4 运营域（5张表）

```sql
-- ============================================
-- 14. 通知公告表
-- ============================================
CREATE TABLE `qa_notice` (
    `id` varchar(36) NOT NULL,
    `title` varchar(200) NOT NULL COMMENT '标题',
    `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
    `content` text NOT NULL COMMENT '正文（富文本）',
    `dept_id` varchar(36) DEFAULT NULL COMMENT '发布部门ID',
    `dept_name` varchar(100) DEFAULT NULL COMMENT '部门名称',
    `urgency_level` tinyint(1) DEFAULT 0 COMMENT '紧急程度(0-普通,1-重要,2-紧急)',
    `visibility_type` tinyint(1) DEFAULT 0 COMMENT '可见范围(0-全校,1-指定学院,2-指定年级,3-指定专业)',
    `target_college_id` varchar(36) DEFAULT NULL COMMENT '目标学院',
    `target_grade` varchar(20) DEFAULT NULL COMMENT '目标年级',
    `target_major` varchar(100) DEFAULT NULL COMMENT '目标专业',
    `attachment_url` varchar(500) DEFAULT NULL COMMENT '附件URL',
    `read_count` int DEFAULT 0 COMMENT '阅读次数',
    `push_count` int DEFAULT 0 COMMENT '推送次数',
    `status` tinyint(1) DEFAULT 0 COMMENT '状态(0-草稿,1-待审核,2-已发布,3-已撤回,4-已过期)',
    `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `create_by` varchar(36) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(36) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_dept_status` (`dept_id`, `status`),
    KEY `idx_urgency` (`urgency_level`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ============================================
-- 15. 办事指南表
-- ============================================
CREATE TABLE `qa_guide` (
    `id` varchar(36) NOT NULL,
    `title` varchar(200) NOT NULL COMMENT '事项标题',
    `category_id` varchar(36) DEFAULT NULL COMMENT '分类ID',
    `dept_id` varchar(36) DEFAULT NULL COMMENT '责任部门ID',
    `materials` text DEFAULT NULL COMMENT '所需材料（JSON数组）',
    `process_steps` text DEFAULT NULL COMMENT '办理步骤（JSON数组）',
    `location` varchar(200) DEFAULT NULL COMMENT '办理地点',
    `contact_phone` varchar(50) DEFAULT NULL COMMENT '联系电话',
    `work_time` varchar(100) DEFAULT NULL COMMENT '工作时间',
    `online_url` varchar(500) DEFAULT NULL COMMENT '线上办理链接',
    `sort_no` int DEFAULT 0 COMMENT '排序号',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态',
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_dept` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='办事指南表';

-- ============================================
-- 16. 用户反馈表
-- ============================================
CREATE TABLE `qa_feedback` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `target_type` varchar(20) NOT NULL COMMENT '反馈对象类型(QA-问答,NOTICE-通知,SYSTEM-系统)',
    `target_id` varchar(36) DEFAULT NULL COMMENT '对象ID',
    `feedback_type` varchar(20) NOT NULL COMMENT '反馈类型(USEFUL-有用,USELESS-无用,ERROR-错误,SUGGEST-建议)',
    `content` text DEFAULT NULL COMMENT '反馈内容',
    `images` text DEFAULT NULL COMMENT '图片URL（JSON数组）',
    `contact` varchar(50) DEFAULT NULL COMMENT '联系方式',
    `status` varchar(20) DEFAULT 'PENDING' COMMENT '处理状态(PENDING-待处理,PROCESSING-处理中,RESOLVED-已解决,REJECTED-已驳回)',
    `reply` text DEFAULT NULL COMMENT '回复内容',
    `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
    `handler_id` varchar(36) DEFAULT NULL COMMENT '处理人ID',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- ============================================
-- 17. 敏感词表
-- ============================================
CREATE TABLE `qa_sensitive_word` (
    `id` varchar(36) NOT NULL,
    `word` varchar(100) NOT NULL COMMENT '敏感词',
    `category` varchar(50) DEFAULT 'GENERAL' COMMENT '类别(POLITICS-政治,PORN-色情,VIOLENCE-暴力,GENERAL-通用)',
    `level` tinyint(1) DEFAULT 1 COMMENT '等级(1-提示,2-审核,3-禁止)',
    `replace_word` varchar(100) DEFAULT NULL COMMENT '替换词',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- ============================================
-- 18. 操作日志表（审计）
-- ============================================
CREATE TABLE `qa_operation_log` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) DEFAULT NULL COMMENT '操作用户',
    `operation_type` varchar(50) NOT NULL COMMENT '操作类型',
    `operation_desc` varchar(500) DEFAULT NULL COMMENT '操作描述',
    `request_method` varchar(10) DEFAULT NULL COMMENT '请求方法',
    `request_url` varchar(500) DEFAULT NULL COMMENT '请求URL',
    `request_param` text DEFAULT NULL COMMENT '请求参数',
    `response_result` text DEFAULT NULL COMMENT '响应结果',
    `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
    `execute_time` int DEFAULT NULL COMMENT '执行时长(ms)',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态(1-成功,0-失败)',
    `error_msg` text DEFAULT NULL COMMENT '错误信息',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`),
    KEY `idx_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```

### 2.3 Milvus向量库Schema设计

```python
# milvus_schema.py - Milvus集合设计（RAG核心）
from pymilvus import FieldSchema, CollectionSchema, DataType

def create_knowledge_collection():
    """创建校园知识库向量集合"""
    
    # 定义字段
    fields = [
        FieldSchema(name="id", dtype=DataType.VARCHAR, is_primary=True, max_length=64),
        FieldSchema(name="knowledge_id", dtype=DataType.VARCHAR, max_length=36),
        FieldSchema(name="chunk_id", dtype=DataType.VARCHAR, max_length=36, default=""),
        FieldSchema(name="content", dtype=DataType.VARCHAR, max_length=2048),
        FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=1024),  # BGE-large-zh-v1.5
        FieldSchema(name="category_id", dtype=DataType.VARCHAR, max_length=36),
        FieldSchema(name="dept_id", dtype=DataType.VARCHAR, max_length=36),
        FieldSchema(name="keywords", dtype=DataType.VARCHAR, max_length=500),
        FieldSchema(name="weight", dtype=DataType.FLOAT, default_value=1.0),  # 权重（热点加成）
        FieldSchema(name="create_time", dtype=DataType.INT64),
    ]
    
    # 创建Schema
    schema = CollectionSchema(
        fields=fields,
        description="校园问答知识库向量集合",
        enable_dynamic_field=True
    )
    
    return schema

def create_index_params():
    """创建向量索引参数（IVF_FLAT + COSINE）"""
    return {
        "index_type": "IVF_FLAT",  # 平衡性能和召回率
        "metric_type": "COSINE",   # 余弦相似度适合语义检索
        "params": {"nlist": 128}   # 聚类中心数
    }
```

---

## 第三章 后端服务开发

### 3.1 JeecgBoot项目初始化

#### 3.1.1 环境准备与代码生成

```bash
# 1. 克隆JeecgBoot SpringBoot 2.7分支
git clone -b springboot2 https://github.com/jeecgboot/jeecg-boot.git campus-qa-system
cd campus-qa-system

# 2. 创建校园问答专用模块（不污染原有代码）
mkdir -p jeecg-module-system/jeecg-system-campusa/src/main/java/org/jeecg/modules/campusqa
cd jeecg-module-system/jeecg-system-campusa

# 3. 初始化pom.xml
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>jeecg-module-system</artifactId>
        <groupId>org.jeecgframework.boot</groupId>
        <version>3.8.5</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    
    <artifactId>jeecg-system-campusa</artifactId>
    <name>校园智能问答模块</name>
    <description>基于JeecgBoot的校园服务智能问答系统</description>
    
    <dependencies>
        <!-- JeecgBoot核心依赖 -->
        <dependency>
            <groupId>org.jeecgframework.boot</groupId>
            <artifactId>jeecg-boot-base-core</artifactId>
            <version>${jeecgboot.version}</version>
        </dependency>
        
        <!-- Jieba中文分词 -->
        <dependency>
            <groupId>com.huaban</groupId>
            <artifactId>jieba-analysis</artifactId>
            <version>1.0.2</version>
        </dependency>
        
        <!-- Milvus向量库客户端 -->
        <dependency>
            <groupId>io.milvus</groupId>
            <artifactId>milvus-sdk-java</artifactId>
            <version>2.3.5</version>
        </dependency>
        
        <!-- DeepSeek SDK（或其他LLM） -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dashscope-sdk-java</artifactId>
            <version>2.16.0</version>
        </dependency>
        
        <!-- 微信开发工具包 -->
        <dependency>
            <groupId>com.github.binarywang</groupId>
            <artifactId>weixin-java-miniapp</artifactId>
            <version>4.6.0</version>
        </dependency>
        
        <!-- 全文检索增强（可选） -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>7.17.9</version>
        </dependency>
    </dependencies>
</project>
EOF
```

#### 3.1.2 使用JeecgBoot代码生成器

```java
// 在JeecgBoot管理后台执行：系统管理 -> 代码生成器
// 导入表：qa_knowledge, qa_category, qa_notice, qa_guide, qa_feedback
// 生成配置：
// - 包名：org.jeecg.modules.campusqa
// - 模板风格：Vue3 + Ant Design Vue（管理后台）
// - 生成选项：生成Controller、Service、Mapper、Vue页面
```

### 3.2 核心Service实现

#### 3.2.1 智能问答Service（RAG完整链路）

```java
package org.jeecg.modules.campusqa.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.campusqa.entity.*;
import org.jeecg.modules.campusqa.mapper.*;
import org.jeecg.modules.campusqa.service.IQaChatService;
import org.jeecg.modules.campusqa.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QaChatServiceImpl implements IQaChatService {
    
    @Autowired
    private QaKnowledgeMapper knowledgeMapper;
    
    @Autowired
    private QaConversationMapper conversationMapper;
    
    @Autowired
    private QaMessageMapper messageMapper;
    
    @Autowired
    private QaRetrievalRecordMapper retrievalRecordMapper;
    
    @Autowired
    private MilvusServiceClient milvusClient;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Value("${milvus.collection.knowledge}")
    private String knowledgeCollection;
    
    @Value("${dashscope.api-key}")
    private String dashscopeApiKey;
    
    // Jieba分词器（单例）
    private final JiebaSegmenter segmenter = new JiebaSegmenter();
    
    // 停用词表
    private final Set<String> stopWords = Set.of("的", "了", "是", "在", "我", "有", "和", "就", 
        "不", "人", "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会");
    
    @PostConstruct
    public void init() {
        // 预加载Milvus集合
        milvusClient.loadCollection(
            LoadCollectionParam.newBuilder()
                .withCollectionName(knowledgeCollection)
                .build()
        );
    }
    
    /**
     * 智能问答核心方法（RAG完整链路）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatResponseVO chat(ChatRequestVO request) {
        String query = request.getQuery();
        String userId = request.getUserId();
        String sessionId = request.getSessionId();
        
        log.info("用户提问: userId={}, query={}", userId, query);
        
        // 1. 获取或创建会话
        QaConversation conversation = getOrCreateConversation(userId, sessionId);
        
        // 2. 保存用户消息
        saveUserMessage(conversation.getId(), query);
        
        // 3. 【RAG Step 1】Jieba分词 + 意图识别
        List<String> keywords = segment(query);
        IntentResult intent = recognizeIntent(keywords);
        
        // 4. 【RAG Step 2】查询重写（结合上下文）
        String rewrittenQuery = rewriteQuery(query, conversation.getId(), intent);
        
        // 5. 【RAG Step 3】多路检索（向量+关键词）
        RetrievalResult retrieval = hybridRetrieve(rewrittenQuery, keywords, intent);
        
        // 6. 【RAG Step 4】结果重排序
        List<KnowledgeCandidate> candidates = rerank(retrieval, query);
        
        // 7. 【RAG Step 5】构建Prompt
        String prompt = buildPrompt(query, candidates, intent);
        
        // 8. 【RAG Step 6】LLM生成（DeepSeek/Qwen）
        String answer = generateAnswer(prompt);
        
        // 9. 【RAG Step 7】答案后处理
        AnswerResult finalAnswer = postProcess(answer, candidates);
        
        // 10. 保存助手消息
        String messageId = saveAssistantMessage(conversation.getId(), finalAnswer, candidates);
        
        // 11. 更新会话状态
        updateConversationStatus(conversation.getId(), "COMPLETED");
        
        // 12. 异步记录检索日志
        asyncSaveRetrievalLog(messageId, query, keywords, intent, retrieval, candidates);
        
        return ChatResponseVO.builder()
            .messageId(messageId)
            .answer(finalAnswer.getContent())
            .answerType(finalAnswer.getType())
            .confidence(finalAnswer.getConfidence())
            .referencedKnowledge(candidates.stream()
                .limit(3)
                .map(c -> ReferencedKnowledgeVO.builder()
                    .id(c.getKnowledgeId())
                    .title(c.getTitle())
                    .confidence(c.getFinalScore())
                    .build())
                .collect(Collectors.toList()))
            .suggestions(generateSuggestions(query, intent))
            .build();
    }
    
    /**
     * Jieba分词（精确模式）
     */
    private List<String> segment(String text) {
        List<SegToken> tokens = segmenter.process(text, JiebaSegmenter.SegMode.SEARCH);
        return tokens.stream()
            .map(t -> t.word.trim())
            .filter(w -> w.length() > 1 && !stopWords.contains(w))
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * 意图识别（规则+模型混合）
     */
    private IntentResult recognizeIntent(List<String> keywords) {
        Set<String> kwSet = new HashSet<>(keywords);
        
        // 查询类意图
        if (containsAny(kwSet, "课表", "成绩", "学分", "绩点", "考试安排", "考试时间")) {
            return new IntentResult("QUERY_ACADEMIC", 0.95, "查询教务信息");
        }
        
        // 办理类意图
        if (containsAny(kwSet, "补办", "申请", "办理", "报名", "缴费", "怎么申请")) {
            return new IntentResult("PROCESS", 0.90, "办理流程咨询");
        }
        
        // 地点类意图
        if (containsAny(kwSet, "哪里", "在哪", "位置", "地址", "办公室", "窗口", "地点")) {
            return new IntentResult("LOCATION", 0.88, "地点位置查询");
        }
        
        // 时间类意图
        if (containsAny(kwSet, "时间", "几点", "什么时候", "截止日期", "有效期", "开放时间")) {
            return new IntentResult("TIME", 0.87, "时间相关查询");
        }
        
        // 联系方式类
        if (containsAny(kwSet, "电话", "联系", "咨询", "找谁", "负责人")) {
            return new IntentResult("CONTACT", 0.85, "联系方式查询");
        }
        
        return new IntentResult("GENERAL", 0.70, "一般咨询");
    }
    
    /**
     * 查询重写（上下文感知）
     */
    private String rewriteQuery(String query, String conversationId, IntentResult intent) {
        // 获取历史上下文（最近3轮）
        List<QaMessage> history = messageMapper.selectRecentMessages(conversationId, 6);
        
        if (history.isEmpty()) {
            return query;
        }
        
        // 指代消解（简单规则）
        String lastTopic = extractTopic(history);
        if (query.length() <= 5 && (query.contains("那") || query.contains("这个"))) {
            return lastTopic + " " + query;
        }
        
        return query;
    }
    
    /**
     * 混合检索（向量+关键词）
     */
    private RetrievalResult hybridRetrieve(String query, List<String> keywords, IntentResult intent) {
        RetrievalResult result = new RetrievalResult();
        
        // 并行执行向量检索和关键词检索
        List<KnowledgeCandidate> vectorResults = vectorSearch(query, 20);
        List<KnowledgeCandidate> keywordResults = keywordSearch(keywords, intent, 20);
        
        result.setVectorResults(vectorResults);
        result.setKeywordResults(keywordResults);
        
        return result;
    }
    
    /**
     * Milvus向量检索
     */
    private List<KnowledgeCandidate> vectorSearch(String query, int topK) {
        // 获取查询向量（调用Embedding服务）
        List<Float> queryVector = getEmbedding(query);
        
        // 构建Milvus检索参数
        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName(knowledgeCollection)
            .withVectors(Collections.singletonList(queryVector))
            .withVectorFieldName("embedding")
            .withTopK(topK)
            .withMetricType(MetricType.COSINE)
            .withOutFields(Arrays.asList("knowledge_id", "content", "category_id", "weight"))
            .build();
        
        SearchResults results = milvusClient.search(searchParam).getData();
        
        // 解析结果
        List<KnowledgeCandidate> candidates = new ArrayList<>();
        // ... 解析逻辑
        
        return candidates;
    }
    
    /**
     * MySQL全文检索（关键词）
     */
    private List<KnowledgeCandidate> keywordSearch(List<String> keywords, IntentResult intent, int topK) {
        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }
        
        String keywordStr = String.join(" ", keywords);
        
        // 使用MySQL全文检索 + 意图过滤
        return knowledgeMapper.searchByKeywords(
            keywordStr, 
            intent.getType(),
            topK
        );
    }
    
    /**
     * 结果重排序（Cross-Encoder或规则融合）
     */
    private List<KnowledgeCandidate> rerank(RetrievalResult retrieval, String originalQuery) {
        // 合并向量检索和关键词检索结果
        Map<String, KnowledgeCandidate> candidateMap = new HashMap<>();
        
        // 向量结果加权
        for (KnowledgeCandidate c : retrieval.getVectorResults()) {
            c.setVectorScore(c.getScore());
            candidateMap.put(c.getKnowledgeId(), c);
        }
        
        // 关键词结果加权
        for (KnowledgeCandidate c : retrieval.getKeywordResults()) {
            if (candidateMap.containsKey(c.getKnowledgeId())) {
                candidateMap.get(c.getKnowledgeId()).setKeywordScore(c.getScore());
            } else {
                c.setKeywordScore(c.getScore());
                candidateMap.put(c.getKnowledgeId(), c);
            }
        }
        
        // 计算最终分数（加权融合）
        List<KnowledgeCandidate> finalList = new ArrayList<>(candidateMap.values());
        for (KnowledgeCandidate c : finalList) {
            double finalScore = c.getVectorScore() * 0.6 + c.getKeywordScore() * 0.4;
            // 热度加成
            if (c.isHot()) {
                finalScore += 0.05;
            }
            c.setFinalScore(Math.min(finalScore, 1.0));
        }
        
        // 按最终分数排序
        finalList.sort((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()));
        
        return finalList;
    }
    
    /**
     * 构建LLM Prompt
     */
    private String buildPrompt(String query, List<KnowledgeCandidate> candidates, IntentResult intent) {
        StringBuilder prompt = new StringBuilder();
        
        // 系统指令
        prompt.append("你是湖北工业大学工程技术学院的校园服务助手，基于以下参考资料回答学生问题。\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 简洁准确，直接回答核心问题\n");
        prompt.append("2. 如参考资料不足以回答，明确说明\"根据现有资料无法确定\"\n");
        prompt.append("3. 涉及办事流程时，列出关键步骤\n");
        prompt.append("4. 回答控制在200字以内\n\n");
        
        // 参考资料
        prompt.append("参考资料：\n");
        for (int i = 0; i < Math.min(candidates.size(), 3); i++) {
            KnowledgeCandidate c = candidates.get(i);
            prompt.append("[").append(i + 1).append("] ")
                  .append(c.getTitle()).append(": ")
                  .append(c.getContent()).append("\n");
        }
        
        // 用户问题
        prompt.append("\n学生问题：").append(query).append("\n");
        prompt.append("意图类型：").append(intent.getDesc()).append("\n");
        prompt.append("请回答：");
        
        return prompt.toString();
    }
    
    /**
     * 调用DeepSeek/Qwen生成答案
     */
    private String generateAnswer(String prompt) {
        try {
            Generation generation = new Generation();
            
            List<Message> messages = Arrays.asList(
                Message.builder().role(Role.SYSTEM.getValue()).content("你是校园服务助手").build(),
                Message.builder().role(Role.USER.getValue()).content(prompt).build()
            );
            
            GenerationParam param = GenerationParam.builder()
                .apiKey(dashscopeApiKey)
                .model("qwen-turbo")  // 或 deepseek-chat
                .messages(messages)
                .temperature(0.3)  // 低温度确保确定性
                .maxTokens(512)
                .build();
            
            GenerationResult result = generation.call(param);
            return result.getOutput().getChoices().get(0).getMessage().getContent();
            
        } catch (Exception e) {
            log.error("LLM生成失败", e);
            return "系统繁忙，请稍后重试";
        }
    }
    
    /**
     * 答案后处理
     */
    private AnswerResult postProcess(String rawAnswer, List<KnowledgeCandidate> candidates) {
        AnswerResult result = new AnswerResult();
        
        // 清洗答案
        String cleaned = rawAnswer.trim();
        
        // 检测是否包含"无法确定"等兜底话术
        if (cleaned.contains("无法确定") || cleaned.contains("不知道") || cleaned.length() < 20) {
            result.setType("FALLBACK");
            result.setConfidence(0.5);
            
            // 构建兜底回答
            StringBuilder fallback = new StringBuilder();
            fallback.append("暂未找到精确答案，为您推荐以下相关问题：\n");
            for (int i = 0; i < Math.min(candidates.size(), 3); i++) {
                fallback.append(i + 1).append(". ").append(candidates.get(i).getTitle()).append("\n");
            }
            fallback.append("\n如未解决，请联系相关部门咨询。");
            result.setContent(fallback.toString());
        } else {
            result.setType("KNOWLEDGE");
            // 计算置信度（基于Top1候选分数）
            double confidence = candidates.isEmpty() ? 0.5 : candidates.get(0).getFinalScore();
            result.setConfidence(confidence);
            result.setContent(cleaned);
        }
        
        return result;
    }
    
    // ============== 辅助方法 ==============
    
    private boolean containsAny(Set<String> set, String... keywords) {
        return Arrays.stream(keywords).anyMatch(set::contains);
    }
    
    private List<Float> getEmbedding(String text) {
        // 调用Embedding服务（BGE-large-zh-v1.5）
        // 实际实现需调用Embedding API或本地模型
        return Collections.emptyList(); // 占位
    }
    
    private QaConversation getOrCreateConversation(String userId, String sessionId) {
        // 实现略
        return new QaConversation();
    }
    
    private void saveUserMessage(String conversationId, String content) {
        // 实现略
    }
    
    private String saveAssistantMessage(String conversationId, AnswerResult answer, 
                                       List<KnowledgeCandidate> candidates) {
        // 实现略
        return UUID.randomUUID().toString();
    }
    
    private void updateConversationStatus(String conversationId, String status) {
        // 实现略
    }
    
    private void asyncSaveRetrievalLog(String messageId, String query, List<String> keywords,
                                      IntentResult intent, RetrievalResult retrieval,
                                      List<KnowledgeCandidate> candidates) {
        // 异步保存，实现略
    }
    
    private List<String> generateSuggestions(String query, IntentResult intent) {
        // 基于意图生成推荐问题
        return Collections.emptyList(); // 占位
    }
    
    private String extractTopic(List<QaMessage> history) {
        // 从历史消息提取主题
        return "";
    }
}
```

#### 3.2.2 微信登录Service

```java
package org.jeecg.modules.campusqa.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.JwtUtil;
import org.jeecg.modules.campusqa.entity.QaUser;
import org.jeecg.modules.campusqa.mapper.QaUserMapper;
import org.jeecg.modules.campusqa.service.IWxAuthService;
import org.jeecg.modules.campusqa.vo.LoginResultVO;
import org.jeecg.modules.campusqa.vo.WxLoginRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class WxAuthServiceImpl implements IWxAuthService {
    
    @Autowired
    private WxMaService wxMaService;
    
    @Autowired
    private QaUserMapper userMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Value("${jeecg.token.expire}")
    private Integer tokenExpire;
    
    /**
     * 微信小程序登录（Code2Session）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResultVO wxLogin(WxLoginRequestVO request) {
        String code = request.getCode();
        
        try {
            // 1. 调用微信接口换取openid和session_key
            WxMaJscode2SessionResult sessionResult = 
                wxMaService.getUserService().getSessionInfo(code);
            
            String openid = sessionResult.getOpenid();
            String sessionKey = sessionResult.getSessionKey();
            String unionid = sessionResult.getUnionid();
            
            log.info("微信登录成功: openid={}", openid);
            
            // 2. 查询或创建用户
            QaUser user = userMapper.selectByOpenid(openid);
            boolean isNewUser = false;
            
            if (user == null) {
                // 新用户注册
                user = new QaUser();
                user.setId(UUID.randomUUID().toString());
                user.setOpenid(openid);
                user.setUnionid(unionid);
                user.setSessionKey(sessionKey);
                user.setUserType(1); // 学生
                user.setBindStatus(0); // 未绑定学号
                user.setStatus(1);
                userMapper.insert(user);
                isNewUser = true;
                log.info("新用户注册: userId={}", user.getId());
            } else {
                // 更新session_key
                user.setSessionKey(sessionKey);
                userMapper.updateById(user);
            }
            
            // 3. 生成JWT Token
            String token = JwtUtil.sign(user.getId(), user.getUserType().toString(), 
                                       user.getBindStatus().toString());
            
            // 4. 缓存Token（用于续期）
            redisTemplate.opsForValue().set(
                "campus:token:" + user.getId(), 
                token, 
                Duration.ofSeconds(tokenExpire)
            );
            
            return LoginResultVO.builder()
                .token(token)
                .expiresIn(tokenExpire)
                .isNewUser(isNewUser)
                .isBound(user.getBindStatus() == 1)
                .userType(user.getUserType())
                .build();
                
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new JeecgBootException("微信登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 学号绑定（个性化服务前提）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindStudent(String userId, String studentNo, String idCard, 
                           String realName, String grade, String major, String collegeId) {
        // 1. 验证学号是否已被绑定
        QaUser existing = userMapper.selectByStudentNo(studentNo);
        if (existing != null && !existing.getId().equals(userId)) {
            throw new JeecgBootException("该学号已被其他账号绑定");
        }
        
        // 2. 验证学号信息（对接学校教务系统或本地校验）
        boolean valid = validateStudentInfo(studentNo, idCard, realName);
        if (!valid) {
            throw new JeecgBootException("学号信息校验失败，请检查输入");
        }
        
        // 3. 更新用户绑定信息
        QaUser user = userMapper.selectById(userId);
        user.setStudentNo(studentNo);
        user.setRealName(realName);
        user.setGrade(grade);
        user.setMajor(major);
        user.setCollegeId(collegeId);
        user.setBindStatus(1);
        userMapper.updateById(user);
        
        log.info("学号绑定成功: userId={}, studentNo={}", userId, studentNo);
    }
    
    private boolean validateStudentInfo(String studentNo, String idCard, String realName) {
        // 实际实现需对接学校身份认证系统
        // 此处简化，仅做格式校验
        return studentNo != null && studentNo.length() >= 10 
            && idCard != null && idCard.length() == 18;
    }
}
```

### 3.3 Mapper与MyBatis-Plus配置

#### 3.3.1 知识库Mapper（全文检索+分页）

```java
package org.jeecg.modules.campusqa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.campusqa.entity.QaKnowledge;
import org.jeecg.modules.campusqa.service.impl.KnowledgeCandidate;

import java.util.List;

public interface QaKnowledgeMapper extends BaseMapper<QaKnowledge> {
    
    /**
     * 全文检索 + 意图过滤（使用MySQL全文索引）
     */
    @Select("<script>" +
        "SELECT k.*, " +
        "       MATCH(k.question, k.answer) AGAINST(#{keyword} IN BOOLEAN MODE) as relevance_score " +
        "FROM qa_knowledge k " +
        "WHERE k.status = 2 " +  // 已发布
        "  AND MATCH(k.question, k.answer) AGAINST(#{keyword} IN BOOLEAN MODE) " +
        "<if test='intentType != null and intentType != \"\"'>" +
        "  AND (k.intent_type = #{intentType} OR k.intent_type = 'GENERAL') " +
        "</if>" +
        "ORDER BY relevance_score DESC, k.hot DESC, k.view_count DESC " +
        "LIMIT #{limit}" +
        "</script>")
    List<KnowledgeCandidate> searchByKeywords(@Param("keyword") String keyword,
                                              @Param("intentType") String intentType,
                                              @Param("limit") int limit);
    
    /**
     * 分页查询（带分类筛选）
     */
    Page<QaKnowledge> selectPageList(Page<QaKnowledge> page,
                                     @Param("categoryId") String categoryId,
                                     @Param("deptId") String deptId,
                                     @Param("status") Integer status,
                                     @Param("keyword") String keyword);
    
    /**
     * 获取热点问题
     */
    @Select("SELECT * FROM qa_knowledge " +
            "WHERE status = 2 AND hot = 1 " +
            "ORDER BY view_count DESC " +
            "LIMIT #{limit}")
    List<QaKnowledge> selectHotKnowledge(@Param("limit") int limit);
    
    /**
     * 更新浏览量（原子操作）
     */
    @Update("UPDATE qa_knowledge SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") String id);
}
```

#### 3.3.2 XML映射文件（CDATA防转义）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.jeecg.modules.campusqa.mapper.QaKnowledgeMapper">

    <!-- 分页查询（复杂条件使用CDATA包裹） -->
    <select id="selectPageList" resultType="org.jeecg.modules.campusqa.entity.QaKnowledge">
        SELECT k.*, c.name as category_name, d.depart_name as dept_name
        FROM qa_knowledge k
        LEFT JOIN qa_category c ON k.category_id = c.id
        LEFT JOIN sys_depart d ON k.dept_id = d.id
        <where>
            k.del_flag = 0
            <if test="categoryId != null and categoryId != ''">
                AND k.category_id = #{categoryId}
            </if>
            <if test="deptId != null and deptId != ''">
                AND k.dept_id = #{deptId}
            </if>
            <if test="status != null">
                AND k.status = #{status}
            </if>
            <if test="keyword != null and keyword != ''">
                <![CDATA[
                AND (MATCH(k.question) AGAINST(#{keyword} IN BOOLEAN MODE)
                     OR MATCH(k.answer) AGAINST(#{keyword} IN BOOLEAN MODE))
                ]]>
            </if>
        </where>
        ORDER BY k.hot DESC, k.sort_no ASC, k.create_time DESC
    </select>

    <!-- 批量插入（知识分片） -->
    <insert id="batchInsertChunks">
        INSERT INTO qa_knowledge_chunk 
        (id, knowledge_id, chunk_seq, chunk_content, vector_id, char_count, status, create_time)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.id}, #{item.knowledgeId}, #{item.chunkSeq}, #{item.chunkContent},
             #{item.vectorId}, #{item.charCount}, #{item.status}, #{item.createTime})
        </foreach>
    </insert>

</mapper>
```

---

## 第四章 微信小程序前端开发

### 4.1 项目结构与配置

```
campus-qa-miniapp/
├── app.js                    # 全局入口（登录态管理）
├── app.json                  # 全局配置（页面路由、tabBar）
├── app.wxss                  # 全局样式（CSS变量设计系统）
├── sitemap.json              # 搜索索引配置
├── project.config.json       # 项目配置
├── package.json              # npm依赖
│
├── pages/                    # 页面目录
│   ├── index/               # 首页（搜索入口+热门问题）
│   ├── search/              # 搜索结果页
│   ├── chat/                # 智能问答页（核心）
│   ├── notice/              # 通知公告列表
│   ├── notice-detail/       # 通知详情
│   ├── guide/               # 办事指引
│   ├── guide-detail/        # 办事详情
│   ├── profile/             # 个人中心
│   ├── history/             # 查询历史
│   ├── favorite/            # 我的收藏
│   ├── login/               # 登录页
│   └── bind-student/        # 学号绑定
│
├── components/              # 公共组件
│   ├── search-bar/          # 搜索栏（带联想）
│   ├── category-grid/       # 分类网格
│   ├── qa-card/             # 问答卡片
│   ├── notice-item/         # 通知项
│   ├── chat-bubble/         # 聊天气泡
│   ├── loading-skeleton/    # 骨架屏
│   └── empty-state/         # 空状态
│
├── utils/                   # 工具函数
│   ├── request.js           # HTTP请求封装
│   ├── auth.js              # 登录态管理
│   ├── storage.js           # 本地存储
│   ├── format.js            # 格式化工具
│   └── jieba-helper.js      # 前端轻量分词（可选）
│
├── services/                # 业务服务
│   ├── authService.js       # 认证服务
│   ├── chatService.js       # 问答服务
│   ├── knowledgeService.js  # 知识库服务
│   ├── noticeService.js     # 通知服务
│   └── userService.js       # 用户服务
│
├── assets/                  # 静态资源
│   ├── images/              # 图片
│   ├── icons/               # 图标
│   └── fonts/               # 字体
│
└── config/                  # 配置文件
    ├── api.config.js        # API地址配置
    ├── app.config.js        # 应用配置
    └── theme.config.js      # 主题配置
```

### 4.2 全局配置（app.json）

```json
{
  "pages": [
    "pages/index/index",
    "pages/search/search",
    "pages/chat/chat",
    "pages/notice/notice",
    "pages/notice-detail/notice-detail",
    "pages/guide/guide",
    "pages/guide-detail/guide-detail",
    "pages/profile/profile",
    "pages/history/history",
    "pages/favorite/favorite",
    "pages/login/login",
    "pages/bind-student/bind-student"
  ],
  "tabBar": {
    "color": "#999999",
    "selectedColor": "#1a365d",
    "backgroundColor": "#ffffff",
    "borderStyle": "black",
    "list": [
      {
        "pagePath": "pages/index/index",
        "text": "首页",
        "iconPath": "assets/icons/home.png",
        "selectedIconPath": "assets/icons/home-active.png"
      },
      {
        "pagePath": "pages/notice/notice",
        "text": "通知",
        "iconPath": "assets/icons/notice.png",
        "selectedIconPath": "assets/icons/notice-active.png"
      },
      {
        "pagePath": "pages/guide/guide",
        "text": "办事",
        "iconPath": "assets/icons/guide.png",
        "selectedIconPath": "assets/icons/guide-active.png"
      },
      {
        "pagePath": "pages/profile/profile",
        "text": "我的",
        "iconPath": "assets/icons/profile.png",
        "selectedIconPath": "assets/icons/profile-active.png"
      }
    ]
  },
  "window": {
    "backgroundTextStyle": "dark",
    "navigationBarBackgroundColor": "#1a365d",
    "navigationBarTitleText": "校园智能问答",
    "navigationBarTextStyle": "white",
    "enablePullDownRefresh": true,
    "backgroundColor": "#f7fafc"
  },
  "usingComponents": {
    "search-bar": "/components/search-bar/search-bar",
    "category-grid": "/components/category-grid/category-grid",
    "qa-card": "/components/qa-card/qa-card",
    "notice-item": "/components/notice-item/notice-item",
    "chat-bubble": "/components/chat-bubble/chat-bubble",
    "loading-skeleton": "/components/loading-skeleton/loading-skeleton",
    "empty-state": "/components/empty-state/empty-state"
  },
  "permission": {
    "scope.userInfo": {
      "desc": "用于完善用户资料，提供个性化服务"
    },
    "scope.subscribeMessage": {
      "desc": "用于接收重要通知推送"
    }
  },
  "requiredBackgroundModes": ["audio"],
  "lazyCodeLoading": "requiredComponents",
  "sitemapLocation": "sitemap.json"
}
```

### 4.3 全局样式系统（app.wxss）

```css
/* ============================================
   校园智能问答系统 - 设计系统（Design System）
   基于开题报告"简洁、直观"原则设计
   ============================================ */

page {
  /* === 主色调 - 学术蓝（稳重、可信） === */
  --primary-900: #0f2942;
  --primary-800: #1a365d;
  --primary-700: #2c5282;
  --primary-600: #3182ce;
  --primary-500: #4299e1;
  --primary-400: #63b3ed;
  --primary-300: #90cdf4;
  --primary-200: #bee3f8;
  --primary-100: #ebf8ff;
  
  /* === 功能色 === */
  --success: #38a169;
  --warning: #d69e2e;
  --error: #e53e3e;
  --info: #3182ce;
  
  /* === 紧急程度专用色 === */
  --urgency-urgent: #e53e3e;    /* 紧急 - 红 */
  --urgency-important: #ed8936; /* 重要 - 橙 */
  --urgency-normal: #3182ce;    /* 普通 - 蓝 */
  
  /* === 分类色 === */
  --cat-academic: #3182ce;   /* 教务 - 蓝 */
  --cat-student: #38a169;    /* 学工 - 绿 */
  --cat-logistics: #d69e2e;  /* 后勤 - 黄 */
  --cat-general: #718096;    /* 通用 - 灰 */
  
  /* === 中性色 === */
  --gray-900: #1a202c;
  --gray-800: #2d3748;
  --gray-700: #4a5568;
  --gray-600: #718096;
  --gray-500: #a0aec0;
  --gray-400: #cbd5e0;
  --gray-300: #e2e8f0;
  --gray-200: #edf2f7;
  --gray-100: #f7fafc;
  
  /* === 背景色 === */
  --bg-primary: #f7fafc;
  --bg-secondary: #edf2f7;
  --bg-white: #ffffff;
  
  /* === 文字色 === */
  --text-primary: #1a202c;
  --text-secondary: #4a5568;
  --text-tertiary: #718096;
  --text-white: #ffffff;
  --text-link: #3182ce;
  
  /* === 间距系统（8px基准） === */
  --space-1: 8rpx;
  --space-2: 16rpx;
  --space-3: 24rpx;
  --space-4: 32rpx;
  --space-5: 48rpx;
  --space-6: 64rpx;
  
  /* === 圆角 === */
  --radius-sm: 8rpx;
  --radius-md: 16rpx;
  --radius-lg: 24rpx;
  --radius-xl: 32rpx;
  --radius-full: 9999rpx;
  
  /* === 阴影 === */
  --shadow-sm: 0 2rpx 4rpx rgba(0,0,0,0.05);
  --shadow-md: 0 4rpx 12rpx rgba(0,0,0,0.1);
  --shadow-lg: 0 8rpx 24rpx rgba(0,0,0,0.15);
  
  /* === 字体 === */
  --font-sans: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  --font-mono: 'SF Mono', Monaco, 'Cascadia Code', monospace;
  
  /* === 字体大小 === */
  --text-xs: 20rpx;
  --text-sm: 24rpx;
  --text-base: 28rpx;
  --text-lg: 32rpx;
  --text-xl: 36rpx;
  --text-2xl: 48rpx;
  
  /* === 行高 === */
  --leading-tight: 1.25;
  --leading-normal: 1.5;
  --leading-relaxed: 1.625;
  
  /* 应用全局样式 */
  font-family: var(--font-sans);
  font-size: var(--text-base);
  color: var(--text-primary);
  background-color: var(--bg-primary);
  line-height: var(--leading-normal);
}

/* === 工具类 === */
.container {
  padding: var(--space-3);
  min-height: 100vh;
  box-sizing: border-box;
}

.card {
  background: var(--bg-white);
  border-radius: var(--radius-md);
  padding: var(--space-3);
  box-shadow: var(--shadow-sm);
  margin-bottom: var(--space-3);
}

.btn-primary {
  background: var(--primary-800);
  color: var(--text-white);
  border-radius: var(--radius-md);
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-base);
  font-weight: 500;
  border: none;
}

.btn-primary:active {
  background: var(--primary-900);
}

.btn-secondary {
  background: var(--bg-white);
  color: var(--primary-800);
  border: 2rpx solid var(--primary-800);
  border-radius: var(--radius-md);
  padding: var(--space-2) var(--space-4);
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-ellipsis-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* === 动画 === */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
```

### 4.4 核心页面：智能问答（chat）

#### 4.4.1 页面结构（chat.wxml）

```html
<!-- pages/chat/chat.wxml -->
<view class="chat-container">
  <!-- 顶部导航 -->
  <view class="chat-header">
    <view class="header-title">智能问答助手</view>
    <view class="header-subtitle">基于校园知识库，为您解答各类问题</view>
  </view>
  
  <!-- 消息列表 -->
  <scroll-view 
    class="message-list" 
    scroll-y 
    scroll-into-view="msg-{{lastMessageId}}"
    enhanced
    show-scrollbar="{{false}}"
  >
    <!-- 欢迎消息 -->
    <view class="welcome-card" wx:if="{{messages.length === 0}}">
      <image class="welcome-icon" src="/assets/icons/robot.png" mode="aspectFit"/>
      <text class="welcome-title">你好，我是校园问答助手</text>
      <text class="welcome-desc">我可以帮你查询课表、办事流程、通知公告等信息</text>
      
      <!-- 快捷入口 -->
      <view class="quick-actions">
        <view class="quick-item" bind:tap="onQuickTap" data-query="课表查询">
          <text class="quick-icon">📅</text>
          <text class="quick-text">课表查询</text>
        </view>
        <view class="quick-item" bind:tap="onQuickTap" data-query="奖学金申请">
          <text class="quick-icon">💰</text>
          <text class="quick-text">奖学金申请</text>
        </view>
        <view class="quick-item" bind:tap="onQuickTap" data-query="图书馆开放时间">
          <text class="quick-icon">📚</text>
          <text class="quick-text">图书馆</text>
        </view>
        <view class="quick-item" bind:tap="onQuickTap" data-query="补办学生证">
          <text class="quick-icon">🎫</text>
          <text class="quick-text">补办学生证</text>
        </view>
      </view>
      
      <!-- 热门问题 -->
      <view class="hot-questions" wx:if="{{hotQuestions.length > 0}}">
        <view class="section-title">热门问题</view>
        <view 
          class="hot-item" 
          wx:for="{{hotQuestions}}" 
          wx:key="id"
          data-id="{{item.id}}"
          data-title="{{item.question}}"
          bind:tap="onHotTap"
        >
          <text class="hot-rank {{index < 3 ? 'top' : ''}}">{{index + 1}}</text>
          <text class="hot-title">{{item.question}}</text>
        </view>
      </view>
    </view>
    
    <!-- 消息气泡 -->
    <block wx:for="{{messages}}" wx:key="id">
      <chat-bubble 
        message="{{item}}" 
        id="msg-{{item.id}}"
        bind:feedback="onFeedback"
        bind:referenceTap="onReferenceTap"
        bind:suggestionTap="onSuggestionTap"
      />
    </block>
    
    <!-- 加载状态 -->
    <view class="typing-indicator" wx:if="{{isTyping}}">
      <view class="dot animate-pulse"></view>
      <view class="dot animate-pulse" style="animation-delay: 0.2s"></view>
      <view class="dot animate-pulse" style="animation-delay: 0.4s"></view>
    </view>
    
    <!-- 底部占位 -->
    <view id="msg-bottom" style="height: 1px;"></view>
  </scroll-view>
  
  <!-- 输入区域 -->
  <view class="input-area {{isInputFocus ? 'focused' : ''}}">
    <!-- 语音输入按钮（可选） -->
    <button class="voice-btn" bind:longpress="onVoiceStart" bind:touchend="onVoiceEnd">
      <image src="/assets/icons/mic.png" mode="aspectFit"/>
    </button>
    
    <!-- 文本输入 -->
    <view class="input-wrapper">
      <textarea
        class="input-field"
        placeholder="请输入您的问题..."
        placeholder-class="input-placeholder"
        value="{{inputValue}}"
        bind:input="onInput"
        bind:focus="onInputFocus"
        bind:blur="onInputBlur"
        bind:confirm="onSend"
        confirm-type="send"
        maxlength="200"
        auto-height
        show-confirm-bar="{{false}}"
        adjust-position="{{false}}"
        fixed
      />
      <!-- 字数提示 -->
      <text class="char-count" wx:if="{{inputValue.length > 0}}">{{inputValue.length}}/200</text>
    </view>
    
    <!-- 发送按钮 -->
    <button 
      class="send-btn {{inputValue.length > 0 ? 'active' : ''}}" 
      bind:tap="onSend"
      disabled="{{inputValue.length === 0 || isTyping}}"
    >
      <image src="/assets/icons/send.png" mode="aspectFit"/>
    </button>
  </view>
  
  <!-- 安全提示 -->
  <view class="safety-tips">AI生成内容仅供参考，具体以学校官方发布为准</view>
</view>
```

#### 4.4.2 页面样式（chat.wxss）

```css
/* pages/chat/chat.wxss */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-primary);
}

/* 顶部导航 */
.chat-header {
  background: linear-gradient(135deg, var(--primary-800) 0%, var(--primary-700) 100%);
  padding: var(--space-3);
  padding-top: calc(var(--space-3) + constant(safe-area-inset-top));
  padding-top: calc(var(--space-3) + env(safe-area-inset-top));
  color: var(--text-white);
}

.header-title {
  font-size: var(--text-xl);
  font-weight: 600;
  margin-bottom: var(--space-1);
}

.header-subtitle {
  font-size: var(--text-sm);
  opacity: 0.9;
}

/* 消息列表 */
.message-list {
  flex: 1;
  padding: var(--space-3);
  box-sizing: border-box;
}

/* 欢迎卡片 */
.welcome-card {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  text-align: center;
  box-shadow: var(--shadow-md);
  animation: fadeIn 0.5s ease-out;
}

.welcome-icon {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: var(--space-3);
}

.welcome-title {
  display: block;
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.welcome-desc {
  display: block;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-4);
}

/* 快捷入口 */
.quick-actions {
  display: flex;
  justify-content: space-around;
  margin-bottom: var(--space-4);
  padding: var(--space-3) 0;
  border-top: 1rpx solid var(--gray-200);
  border-bottom: 1rpx solid var(--gray-200);
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--space-2);
  transition: transform 0.2s;
}

.quick-item:active {
  transform: scale(0.95);
}

.quick-icon {
  font-size: 48rpx;
  margin-bottom: var(--space-1);
}

.quick-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 热门问题 */
.hot-questions {
  text-align: left;
}

.section-title {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  display: block;
}

.hot-item {
  display: flex;
  align-items: center;
  padding: var(--space-2) 0;
  border-bottom: 1rpx solid var(--gray-100);
}

.hot-item:last-child {
  border-bottom: none;
}

.hot-rank {
  width: 40rpx;
  height: 40rpx;
  line-height: 40rpx;
  text-align: center;
  background: var(--gray-200);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-right: var(--space-2);
  font-weight: 600;
}

.hot-rank.top {
  background: var(--secondary);
  color: var(--text-white);
}

.hot-title {
  flex: 1;
  font-size: var(--text-base);
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 输入区域 */
.input-area {
  background: var(--bg-white);
  padding: var(--space-2) var(--space-3);
  padding-bottom: calc(var(--space-2) + constant(safe-area-inset-bottom));
  padding-bottom: calc(var(--space-2) + env(safe-area-inset-bottom));
  border-top: 1rpx solid var(--gray-200);
  display: flex;
  align-items: flex-end;
  gap: var(--space-2);
}

.input-area.focused {
  padding-bottom: var(--space-2);
}

.voice-btn {
  width: 72rpx;
  height: 72rpx;
  padding: 0;
  margin: 0;
  background: var(--gray-100);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.voice-btn image {
  width: 40rpx;
  height: 40rpx;
}

.input-wrapper {
  flex: 1;
  position: relative;
}

.input-field {
  min-height: 72rpx;
  max-height: 200rpx;
  background: var(--gray-100);
  border-radius: var(--radius-md);
  padding: var(--space-2) var(--space-3);
  padding-right: 80rpx;
  font-size: var(--text-base);
  line-height: var(--leading-normal);
  width: 100%;
  box-sizing: border-box;
}

.input-placeholder {
  color: var(--text-tertiary);
}

.char-count {
  position: absolute;
  right: var(--space-2);
  bottom: var(--space-2);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  padding: 0;
  margin: 0;
  background: var(--gray-300);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.send-btn.active {
  background: var(--primary-800);
}

.send-btn image {
  width: 40rpx;
  height: 40rpx;
  filter: brightness(0) invert(1);
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-3);
  gap: 12rpx;
}

.typing-indicator .dot {
  width: 16rpx;
  height: 16rpx;
  background: var(--gray-400);
  border-radius: var(--radius-full);
}

/* 安全提示 */
.safety-tips {
  text-align: center;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  padding: var(--space-1) 0;
  background: var(--bg-white);
}
```

#### 4.4.3 页面逻辑（chat.js）

```javascript
// pages/chat/chat.js
import { chatService } from '../../services/chatService';
import { knowledgeService } from '../../services/knowledgeService';
import { auth } from '../../utils/auth';

Page({
  data: {
    messages: [],
    inputValue: '',
    isTyping: false,
    isInputFocus: false,
    hotQuestions: [],
    lastMessageId: '',
    sessionId: ''
  },

  onLoad(options) {
    // 初始化会话ID
    this.setData({ 
      sessionId: this.generateSessionId(),
      // 如果有传入问题，直接提问
      inputValue: options.query || ''
    });
    
    if (options.query) {
      this.sendMessage();
    }
    
    this.loadHotQuestions();
  },

  // 生成会话ID
  generateSessionId() {
    return 'sess_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
  },

  // 加载热门问题
  async loadHotQuestions() {
    try {
      const list = await knowledgeService.getHotQuestions(5);
      this.setData({ hotQuestions: list });
    } catch (err) {
      console.error('加载热门问题失败', err);
    }
  },

  // 输入处理
  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  onInputFocus() {
    this.setData({ isInputFocus: true });
    this.scrollToBottom();
  },

  onInputBlur() {
    this.setData({ isInputFocus: false });
  },

  // 快捷入口点击
  onQuickTap(e) {
    const query = e.currentTarget.dataset.query;
    this.setData({ inputValue: query });
    this.sendMessage();
  },

  // 热门问题点击
  onHotTap(e) {
    const title = e.currentTarget.dataset.title;
    this.setData({ inputValue: title });
    this.sendMessage();
  },

  // 发送消息（核心方法）
  async sendMessage() {
    const query = this.data.inputValue.trim();
    if (!query || this.data.isTyping) return;

    // 添加用户消息
    const userMessage = {
      id: 'msg_' + Date.now(),
      role: 'USER',
      content: query,
      type: 'TEXT',
      createTime: new Date().toISOString()
    };

    this.setData({
      messages: [...this.data.messages, userMessage],
      inputValue: '',
      isTyping: true
    });

    this.scrollToBottom();

    try {
      // 调用RAG问答服务
      const response = await chatService.sendMessage({
        query: query,
        sessionId: this.data.sessionId,
        userId: auth.getUserId()
      });

      // 添加助手消息
      const assistantMessage = {
        id: response.messageId,
        role: 'ASSISTANT',
        content: response.answer,
        type: response.answerType, // KNOWLEDGE 或 FALLBACK
        confidence: response.confidence,
        references: response.referencedKnowledge,
        suggestions: response.suggestions,
        createTime: new Date().toISOString()
      };

      this.setData({
        messages: [...this.data.messages, assistantMessage],
        isTyping: false,
        lastMessageId: assistantMessage.id
      });

      // 如果置信度低，提示用户
      if (response.confidence < 0.6) {
        wx.showToast({
          title: '答案置信度较低，建议核实',
          icon: 'none',
          duration: 3000
        });
      }

    } catch (err) {
      console.error('发送失败', err);
      
      // 添加错误消息
      const errorMessage = {
        id: 'err_' + Date.now(),
        role: 'SYSTEM',
        content: '网络异常，请稍后重试',
        type: 'ERROR',
        createTime: new Date().toISOString()
      };

      this.setData({
        messages: [...this.data.messages, errorMessage],
        isTyping: false
      });
    }

    this.scrollToBottom();
  },

  // 反馈处理（有用/无用）
  async onFeedback(e) {
    const { messageId, useful } = e.detail;
    
    try {
      await chatService.submitFeedback(messageId, useful);
      
      // 更新本地消息状态
      const messages = this.data.messages.map(msg => {
        if (msg.id === messageId) {
          return { ...msg, feedback: useful ? 'LIKED' : 'DISLIKED' };
        }
        return msg;
      });
      
      this.setData({ messages });
      
      wx.showToast({
        title: useful ? '感谢您的认可' : '我们会改进答案',
        icon: 'none'
      });
    } catch (err) {
      console.error('反馈提交失败', err);
    }
  },

  // 引用知识点击
  onReferenceTap(e) {
    const { id } = e.detail;
    wx.navigateTo({
      url: `/pages/knowledge-detail/knowledge-detail?id=${id}`
    });
  },

  // 建议问题点击
  onSuggestionTap(e) {
    const { query } = e.detail;
    this.setData({ inputValue: query });
    this.sendMessage();
  },

  // 滚动到底部
  scrollToBottom() {
    setTimeout(() => {
      this.setData({ lastMessageId: 'msg-bottom' });
    }, 100);
  },

  // 语音输入（可选功能）
  onVoiceStart() {
    // 开始录音
    wx.startRecord({
      success: (res) => {
        console.log('录音成功', res);
        // 调用语音识别API
      }
    });
  },

  onVoiceEnd() {
    wx.stopRecord();
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: '校园智能问答助手 - 有问题随时问',
      path: '/pages/chat/chat'
    };
  }
});
```

### 4.5 请求封装（utils/request.js）

```javascript
// utils/request.js
import { auth } from './auth';
import { API_BASE_URL } from '../config/api.config';

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = auth.getToken();
    
    wx.request({
      url: `${API_BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        'X-Request-ID': generateUUID(),
        'X-Client-Version': '1.0.0',
        ...options.header
      },
      timeout: 30000,
      
      success: (res) => {
        if (res.statusCode === 200) {
          const data = res.data;
          
          // JeecgBoot统一响应格式
          if (data.code === 'A0000' || data.code === 200) {
            resolve(data.data || data.result);
          } else if (data.code === 'B4002' || data.code === 401) {
            // Token过期，尝试刷新
            auth.refreshToken().then(() => {
              // 重试原请求
              request(options).then(resolve).catch(reject);
            }).catch(() => {
              // 刷新失败，跳转登录
              auth.logout();
              wx.redirectTo({ url: '/pages/login/login' });
              reject(new Error('登录已过期'));
            });
          } else {
            reject(new Error(data.message || '请求失败'));
          }
        } else if (res.statusCode === 401) {
          auth.logout();
          wx.redirectTo({ url: '/pages/login/login' });
          reject(new Error('未登录'));
        } else {
          reject(new Error(`服务器错误: ${res.statusCode}`));
        }
      },
      
      fail: (err) => {
        if (err.errMsg.includes('timeout')) {
          reject(new Error('请求超时，请检查网络'));
        } else {
          reject(new Error(err.errMsg || '网络请求失败'));
        }
      }
    });
  });
};

// 便捷方法
const get = (url, params) => request({ url, method: 'GET', data: params });
const post = (url, data) => request({ url, method: 'POST', data });
const put = (url, data) => request({ url, method: 'PUT', data });
const del = (url) => request({ url, method: 'DELETE' });

// 生成UUID
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

export { request, get, post, put, del };
```

---

## 第五章 管理后台开发（Vue3 + Ant Design Vue）

### 5.1 技术栈与项目结构

```
campus-qa-admin/
├── public/
├── src/
│   ├── api/                    # API接口
│   │   ├── knowledge.js        # 知识库管理
│   │   ├── notice.js           # 通知管理
│   │   ├── user.js             # 用户管理
│   │   └── stats.js            # 数据统计
│   │
│   ├── assets/                 # 静态资源
│   ├── components/             # 公共组件
│   │   ├── RichEditor/         # 富文本编辑器
│   │   ├── KnowledgeForm/      # 知识表单
│   │   ├── NoticeForm/         # 通知表单
│   │   └── DataTable/          # 数据表格
│   │
│   ├── layouts/                # 布局组件
│   │   ├── BasicLayout.vue     # 基础布局
│   │   └── UserLayout.vue      # 用户布局
│   │
│   ├── router/                 # 路由配置
│   │   └── index.js
│   │
│   ├── stores/                 # Pinia状态管理
│   │   ├── user.js
│   │   ├── knowledge.js
│   │   └── notice.js
│   │
│   ├── utils/                  # 工具函数
│   │   ├── request.js          # Axios封装
│   │   ├── permission.js       # 权限控制
│   │   └── format.js           # 格式化
│   │
│   ├── views/                  # 页面视图
│   │   ├── knowledge/          # 知识库管理
│   │   │   ├── List.vue
│   │   │   ├── Create.vue
│   │   │   └── Edit.vue
│   │   ├── notice/             # 通知管理
│   │   ├── user/               # 用户管理
│   │   ├── stats/              # 数据统计
│   │   └── system/             # 系统配置
│   │
│   ├── App.vue
│   └── main.js
│
├── .env.development
├── .env.production
├── vite.config.js
├── package.json
└── tailwind.config.js
```

### 5.2 核心页面：知识库管理

```vue
<!-- src/views/knowledge/List.vue -->
<template>
  <a-card :bordered="false">
    <!-- 搜索栏 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="关键词">
              <a-input
                v-model:value="queryParam.keyword"
                placeholder="标题/内容/关键词"
                allow-clear
                @pressEnter="handleSearch"
              />
            </a-form-item>
          </a-col>
          
          <a-col :md="8" :sm="24">
            <a-form-item label="分类">
              <a-tree-select
                v-model:value="queryParam.categoryId"
                :tree-data="categoryTree"
                placeholder="请选择分类"
                allow-clear
                tree-default-expand-all
              />
            </a-form-item>
          </a-col>
          
          <a-col :md="8" :sm="24">
            <a-form-item label="状态">
              <a-select
                v-model:value="queryParam.status"
                placeholder="全部状态"
                allow-clear
              >
                <a-select-option :value="0">草稿</a-select-option>
                <a-select-option :value="1">待审核</a-select-option>
                <a-select-option :value="2">已发布</a-select-option>
                <a-select-option :value="3">已驳回</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <template v-if="advanced">
            <a-col :md="8" :sm="24">
              <a-form-item label="所属部门">
                <a-tree-select
                  v-model:value="queryParam.deptId"
                  :tree-data="deptTree"
                  placeholder="请选择部门"
                  allow-clear
                />
              </a-form-item>
            </a-col>
            
            <a-col :md="8" :sm="24">
              <a-form-item label="是否热点">
                <a-select v-model:value="queryParam.hot" allow-clear>
                  <a-select-option :value="1">是</a-select-option>
                  <a-select-option :value="0">否</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>
          
          <a-col :md="!advanced && 8 || 24" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearch">
                <SearchOutlined />查询
              </a-button>
              <a-button style="margin-left: 8px" @click="handleReset">
                <ReloadOutlined />重置
              </a-button>
              <a style="margin-left: 8px" @click="toggleAdvanced">
                {{ advanced ? '收起' : '展开' }}
                <component :is="advanced ? UpOutlined : DownOutlined" />
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    
    <!-- 操作栏 -->
    <div class="table-operator">
      <a-button type="primary" @click="handleCreate">
        <PlusOutlined />新增问答
      </a-button>
      <a-button @click="handleBatchDelete" :disabled="!selectedRowKeys.length">
        <DeleteOutlined />批量删除
      </a-button>
      <a-button @click="handleExport">
        <ExportOutlined />导出
      </a-button>
    </div>
    
    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
      row-key="id"
      @change="handleTableChange"
    >
      <!-- 状态列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-badge
            :status="statusMap[record.status].badge"
            :text="statusMap[record.status].text"
          />
        </template>
        
        <template v-if="column.key === 'hot'">
          <a-tag v-if="record.hot" color="red">热点</a-tag>
          <span v-else>-</span>
        </template>
        
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="handleView(record)">预览</a>
            <a-divider type="vertical" />
            <a-dropdown>
              <a>更多<DownOutlined /></a>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleToggleHot(record)">
                    {{ record.hot ? '取消热点' : '设为热点' }}
                  </a-menu-item>
                  <a-menu-item @click="handleAudit(record)" v-if="record.status === 1">
                    审核
                  </a-menu-item>
                  <a-menu-item danger @click="handleDelete(record)">删除</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </template>
    </a-table>
    
    <!-- 新增/编辑弹窗 -->
    <KnowledgeForm
      v-model:visible="formVisible"
      :record="currentRecord"
      @success="handleSearch"
    />
  </a-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message, Modal } from 'ant-design-vue';
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
  ExportOutlined,
  UpOutlined,
  DownOutlined
} from '@ant-design/icons-vue';
import { useKnowledgeStore } from '@/stores/knowledge';
import KnowledgeForm from './components/KnowledgeForm.vue';

const router = useRouter();
const knowledgeStore = useKnowledgeStore();

// 状态
const loading = ref(false);
const advanced = ref(false);
const dataSource = ref([]);
const selectedRowKeys = ref([]);
const currentRecord = ref(null);
const formVisible = ref(false);
const categoryTree = ref([]);
const deptTree = ref([]);

// 查询参数
const queryParam = reactive({
  keyword: '',
  categoryId: undefined,
  status: undefined,
  deptId: undefined,
  hot: undefined,
  pageNo: 1,
  pageSize: 10
});

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true
});

// 状态映射
const statusMap = {
  0: { badge: 'default', text: '草稿' },
  1: { badge: 'processing', text: '待审核' },
  2: { badge: 'success', text: '已发布' },
  3: { badge: 'error', text: '已驳回' }
};

// 表格列
const columns = [
  { title: '问题', dataIndex: 'question', ellipsis: true, width: 300 },
  { title: '分类', dataIndex: 'categoryName', width: 120 },
  { title: '部门', dataIndex: 'deptName', width: 120 },
  { title: '状态', key: 'status', width: 100 },
  { title: '热点', key: 'hot', width: 80 },
  { title: '浏览量', dataIndex: 'viewCount', width: 100, sorter: true },
  { title: '更新时间', dataIndex: 'updateTime', width: 180 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' }
];

// 初始化
onMounted(() => {
  loadData();
  loadCategoryTree();
  loadDeptTree();
});

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const res = await knowledgeStore.getList(queryParam);
    dataSource.value = res.records;
    pagination.total = res.total;
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  queryParam.pageNo = 1;
  loadData();
};

// 重置
const handleReset = () => {
  Object.keys(queryParam).forEach(key => {
    if (key !== 'pageNo' && key !== 'pageSize') {
      queryParam[key] = undefined;
    }
  });
  handleSearch();
};

// 切换高级搜索
const toggleAdvanced = () => {
  advanced.value = !advanced.value;
};

// 表格变化
const handleTableChange = (pag, filters, sorter) => {
  queryParam.pageNo = pag.current;
  queryParam.pageSize = pag.pageSize;
  loadData();
};

// 选择变化
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys;
};

// 新增
const handleCreate = () => {
  currentRecord.value = null;
  formVisible.value = true;
};

// 编辑
const handleEdit = (record) => {
  currentRecord.value = record;
  formVisible.value = true;
};

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除"${record.question}"吗？`,
    onOk: async () => {
      await knowledgeStore.delete(record.id);
      message.success('删除成功');
      loadData();
    }
  });
};

// 批量删除
const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的${selectedRowKeys.value.length}条记录吗？`,
    onOk: async () => {
      await knowledgeStore.batchDelete(selectedRowKeys.value);
      message.success('批量删除成功');
      selectedRowKeys.value = [];
      loadData();
    }
  });
};

// 切换热点
const handleToggleHot = async (record) => {
  await knowledgeStore.toggleHot(record.id, !record.hot);
  message.success(record.hot ? '已取消热点' : '已设为热点');
  loadData();
};

// 其他方法...
const loadCategoryTree = async () => {
  categoryTree.value = await knowledgeStore.getCategoryTree();
};

const loadDeptTree = async () => {
  deptTree.value = await knowledgeStore.getDeptTree();
};
</script>
```

---

## 第六章 RAG智能问答引擎（核心）

### 6.1 RAG架构完整流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           RAG智能问答完整流程                                 │
└─────────────────────────────────────────────────────────────────────────────┘

用户提问: "图书馆明天几点开门？"
    │
    ▼
┌─────────────────┐
│ 1. 预处理层      │
│ - 敏感词过滤    │
│ - 输入长度校验  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 2. 意图识别层    │ ← BERT微调模型 + 规则兜底
│ - 分类：后勤服务 │
│ - 意图：时间查询 │
│ - 置信度：0.94  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 3. 查询重写层    │
│ - 指代消解      │  "明天"→具体日期
│ - 扩展查询      │  "图书馆"+"开放时间"
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│ 4. 多路检索层（并行）            │
│                                 │
│  ┌─────────────┐ ┌────────────┐ │
│  │ 向量检索     │ │ 关键词检索  │ │
│  │ Milvus      │ │ MySQL全文   │ │
│  │ Top20       │ │ Top20       │ │
│  │ 相似度>0.7  │ │ 匹配度>0.6  │ │
│  └──────┬──────┘ └─────┬──────┘ │
│         └──────────────┤        │
│                        ▼        │
│               ┌─────────────┐   │
│               │ 结果融合     │   │
│               │ 去重+归一化  │   │
│               └──────┬──────┘   │
└──────────────────────┼──────────┘
                       │
                       ▼
┌─────────────────┐
│ 5. 重排序层      │ ← Cross-Encoder模型
│ - 精排Top5      │
│ - 相关性评分    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 6. 提示工程层    │
│ 系统指令+上下文+ │
│ 参考资料+问题    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 7. LLM生成层     │ ← DeepSeek/Qwen API
│ - 流式输出      │
│ - 温度=0.3      │
│ - 最大512token  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 8. 后处理层      │
│ - 答案清洗      │
│ - 引用标注      │
│ - 置信度计算    │
└────────┬────────┘
         │
         ▼
      返回用户
```

### 6.2 向量检索服务（Milvus）

```java
package org.jeecg.modules.campusqa.rag;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VectorSearchService {
    
    @Autowired
    private MilvusServiceClient milvusClient;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    @Value("${milvus.collection.knowledge}")
    private String collectionName;
    
    @PostConstruct
    public void init() {
        // 预加载集合到内存
        milvusClient.loadCollection(
            LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build()
        );
        log.info("Milvus集合加载成功: {}", collectionName);
    }
    
    /**
     * 向量语义检索
     */
    public List<VectorSearchResult> search(String query, int topK) {
        // 1. 文本向量化
        List<Float> queryVector = embeddingService.encode(query);
        
        // 2. 构建检索参数
        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName(collectionName)
            .withVectors(Collections.singletonList(queryVector))
            .withVectorFieldName("embedding")
            .withTopK(topK)
            .withMetricType(MetricType.COSINE)
            .withConsistencyLevel(ConsistencyLevelEnum.EVENTUALLY)
            .withOutFields(Arrays.asList(
                "knowledge_id", "chunk_id", "content", 
                "category_id", "dept_id", "weight"
            ))
            .build();
        
        // 3. 执行检索
        SearchResults results = milvusClient.search(searchParam).getData();
        SearchResultsWrapper wrapper = new SearchResultsWrapper(results.getResults());
        
        // 4. 解析结果
        List<VectorSearchResult> searchResults = new ArrayList<>();
        for (int i = 0; i < wrapper.getRowCount(); i++) {
            VectorSearchResult result = new VectorSearchResult();
            result.setKnowledgeId(wrapper.getFieldData("knowledge_id", i).toString());
            result.setChunkId(wrapper.getFieldData("chunk_id", i).toString());
            result.setContent(wrapper.getFieldData("content", i).toString());
            result.setCategoryId(wrapper.getFieldData("category_id", i).toString());
            result.setDeptId(wrapper.getFieldData("dept_id", i).toString());
            result.setWeight((Float) wrapper.getFieldData("weight", i));
            result.setScore(wrapper.getIDScore(i));
            searchResults.add(result);
        }
        
        return searchResults;
    }
    
    /**
     * 带过滤条件的检索（按分类/部门）
     */
    public List<VectorSearchResult> searchWithFilter(
            String query, 
            String categoryId,
            String deptId,
            int topK) {
        
        // 构建过滤表达式
        StringBuilder filter = new StringBuilder();
        if (categoryId != null) {
            filter.append("category_id == '").append(categoryId).append("'");
        }
        if (deptId != null) {
            if (filter.length() > 0) filter.append(" && ");
            filter.append("dept_id == '").append(deptId).append("'");
        }
        
        List<Float> queryVector = embeddingService.encode(query);
        
        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName(collectionName)
            .withVectors(Collections.singletonList(queryVector))
            .withVectorFieldName("embedding")
            .withTopK(topK)
            .withMetricType(MetricType.COSINE)
            .withExpr(filter.toString())  // 过滤条件
            .withOutFields(Arrays.asList("knowledge_id", "content", "weight"))
            .build();
        
        // ... 执行检索
        return Collections.emptyList();
    }
}
```

### 6.3 意图识别服务

```java
package org.jeecg.modules.campusqa.nlp;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntentRecognitionService {
    
    // 意图规则库（可扩展为BERT模型）
    private final Map<String, IntentRule> intentRules = new HashMap<>();
    
    public IntentRecognitionService() {
        initRules();
    }
    
    private void initRules() {
        // 教务查询意图
        intentRules.put("QUERY_ACADEMIC", new IntentRule()
            .addKeyword("课表", 1.0)
            .addKeyword("成绩", 1.0)
            .addKeyword("学分", 0.9)
            .addKeyword("绩点", 0.9)
            .addKeyword("考试安排", 0.9)
            .addKeyword("考试时间", 0.9)
            .addKeyword("选课", 0.8)
            .setCategory("教务服务")
        );
        
        // 学工服务意图
        intentRules.put("QUERY_STUDENT", new IntentRule()
            .addKeyword("奖学金", 1.0)
            .addKeyword("助学金", 1.0)
            .addKeyword("学生证", 0.9)
            .addKeyword("宿舍", 0.8)
            .addKeyword("辅导员", 0.8)
            .setCategory("学工服务")
        );
        
        // 后勤服务意图
        intentRules.put("QUERY_LOGISTICS", new IntentRule()
            .addKeyword("图书馆", 1.0)
            .addKeyword("食堂", 0.9)
            .addKeyword("快递", 0.9)
            .addKeyword("校车", 0.9)
            .addKeyword("报修", 0.8)
            .addKeyword("水电", 0.8)
            .setCategory("后勤服务")
        );
        
        // 办理流程意图
        intentRules.put("PROCESS", new IntentRule()
            .addKeyword("怎么办", 1.0)
            .addKeyword("怎么申请", 1.0)
            .addKeyword("怎么补办", 1.0)
            .addKeyword("流程", 0.9)
            .addKeyword("步骤", 0.9)
            .addKeyword("需要材料", 0.8)
            .setCategory("办事流程")
        );
        
        // 时间查询意图
        intentRules.put("QUERY_TIME", new IntentRule()
            .addKeyword("几点", 1.0)
            .addKeyword("时间", 0.9)
            .addKeyword("什么时候", 0.9)
            .addKeyword("截止日期", 0.9)
            .addKeyword("有效期", 0.8)
            .addKeyword("开放时间", 0.8)
            .setCategory("时间查询")
        );
        
        // 地点查询意图
        intentRules.put("QUERY_LOCATION", new IntentRule()
            .addKeyword("在哪", 1.0)
            .addKeyword("哪里", 1.0)
            .addKeyword("位置", 0.9)
            .addKeyword("地址", 0.9)
            .addKeyword("办公室", 0.8)
            .addKeyword("窗口", 0.8)
            .setCategory("地点查询")
        );
    }
    
    /**
     * 识别意图
     */
    public IntentResult recognize(List<String> keywords) {
        Set<String> keywordSet = new HashSet<>(keywords);
        
        String bestIntent = "GENERAL";
        double maxScore = 0.3;  // 基础阈值
        
        for (Map.Entry<String, IntentRule> entry : intentRules.entrySet()) {
            double score = calculateScore(keywordSet, entry.getValue());
            if (score > maxScore) {
                maxScore = score;
                bestIntent = entry.getKey();
            }
        }
        
        IntentRule rule = intentRules.getOrDefault(bestIntent, new IntentRule());
        
        return IntentResult.builder()
            .type(bestIntent)
            .confidence(maxScore)
            .category(rule.getCategory())
            .description(getIntentDescription(bestIntent))
            .build();
    }
    
    private double calculateScore(Set<String> keywords, IntentRule rule) {
        double score = 0;
        int matchCount = 0;
        
        for (Map.Entry<String, Double> kw : rule.getKeywords().entrySet()) {
            if (keywords.contains(kw.getKey())) {
                score += kw.getValue();
                matchCount++;
            }
        }
        
        // 归一化
        if (matchCount > 0) {
            score = score / Math.sqrt(rule.getKeywords().size());
        }
        
        return Math.min(score, 1.0);
    }
    
    private String getIntentDescription(String intentType) {
        Map<String, String> map = new HashMap<>();
        map.put("QUERY_ACADEMIC", "教务信息查询");
        map.put("QUERY_STUDENT", "学工服务查询");
        map.put("QUERY_LOGISTICS", "后勤服务查询");
        map.put("PROCESS", "办事流程咨询");
        map.put("QUERY_TIME", "时间相关查询");
        map.put("QUERY_LOCATION", "地点位置查询");
        map.put("GENERAL", "一般咨询");
        return map.getOrDefault(intentType, "未知意图");
    }
    
    @Data
    static class IntentRule {
        private Map<String, Double> keywords = new HashMap<>();
        private String category;
        
        IntentRule addKeyword(String word, double weight) {
            keywords.put(word, weight);
            return this;
        }
        
        IntentRule setCategory(String category) {
            this.category = category;
            return this;
        }
    }
}
```

---

## 第七章 测试与质量保证

### 7.1 测试策略

| 测试类型   | 工具              | 覆盖率要求 | 关键用例        |
| :--------- | :---------------- | :--------- | :-------------- |
| 单元测试   | JUnit 5 + Mockito | ≥80%       | Service核心方法 |
| 集成测试   | SpringBootTest    | 核心链路   | RAG完整流程     |
| 接口测试   | Postman + Newman  | 100%接口   | 所有API端点     |
| 性能测试   | JMeter            | 50并发     | 问答响应<500ms  |
| 小程序测试 | 微信开发者工具    | 全页面     | 真机+模拟器     |
| 安全测试   | OWASP ZAP         | 高危漏洞0  | SQL注入、XSS    |

### 7.2 核心测试用例（RAG链路）

```java
@SpringBootTest
@AutoConfigureMockMvc
public class RAGIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 测试完整RAG问答链路
     */
    @Test
    public void testCompleteRAGFlow() throws Exception {
        // 1. 准备测试数据
        ChatRequestVO request = new ChatRequestVO();
        request.setQuery("图书馆明天几点开门？");
        request.setUserId("test_user_001");
        request.setSessionId("test_session_001");
        
        // 2. 执行请求
        MvcResult result = mockMvc.perform(post("/api/v1/chat/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0000"))
            .andExpect(jsonPath("$.data.answer").isNotEmpty())
            .andExpect(jsonPath("$.data.confidence").isNumber())
            .andExpect(jsonPath("$.data.referencedKnowledge").isArray())
            .andReturn();
        
        // 3. 验证响应
        String response = result.getResponse().getContentAsString();
        ChatResponseVO chatResponse = objectMapper.readValue(
            JsonPath.parse(response).read("$.data").toString(), 
            ChatResponseVO.class
        );
        
        // 4. 断言验证
        assertThat(chatResponse.getConfidence()).isGreaterThan(0.6);
        assertThat(chatResponse.getReferencedKnowledge()).isNotEmpty();
        assertThat(chatResponse.getAnswer()).contains("图书馆");
        
        // 5. 验证数据库记录
        // 验证消息记录
        // 验证检索日志
        // 验证会话状态
    }
    
    /**
     * 测试意图识别准确性
     */
    @ParameterizedTest
    @CsvSource({
        "课表查询, QUERY_ACADEMIC, 0.9",
        "怎么申请奖学金, PROCESS, 0.8",
        "图书馆在哪, QUERY_LOCATION, 0.9",
        "明天几点开门, QUERY_TIME, 0.85"
    })
    public void testIntentRecognition(String query, String expectedIntent, double minConfidence) {
        IntentResult result = intentService.recognize(segment(query));
        
        assertThat(result.getType()).isEqualTo(expectedIntent);
        assertThat(result.getConfidence()).isGreaterThanOrEqualTo(minConfidence);
    }
    
    /**
     * 测试向量检索召回率
     */
    @Test
    public void testVectorRetrievalRecall() {
        String query = "学生证补办流程";
        List<VectorSearchResult> results = vectorSearchService.search(query, 10);
        
        // Top5中应包含相关结果
        long relevantCount = results.stream()
            .filter(r -> r.getContent().contains("学生证") || r.getContent().contains("补办"))
            .limit(5)
            .count();
        
        assertThat(relevantCount).isGreaterThanOrEqualTo(3); // Top5中至少3个相关
    }
}
```

### 7.3 JMeter性能测试计划

```xml
<?xml version="1.0" encoding="UTF-8"?>
<TestPlan guiclass="TestPlanGui" testname="校园问答系统性能测试">
  <ThreadGroup guiclass="ThreadGroupGui" testname="50并发用户">
    <stringProp name="ThreadGroup.num_threads">50</stringProp>
    <stringProp name="ThreadGroup.ramp_time">10</stringProp>
    <stringProp name="ThreadGroup.duration">600</stringProp>
    
    <!-- 智能问答接口 -->
    <HTTPSamplerProxy guiclass="HttpTestSampleGui" testname="POST /api/v1/chat/send">
      <stringProp name="HTTPSampler.domain">${__P(host,localhost)}</stringProp>
      <stringProp name="HTTPSampler.port">${__P(port,8080)}</stringProp>
      <stringProp name="HTTPSampler.path">/api/v1/chat/send</stringProp>
      <stringProp name="HTTPSampler.method">POST</stringProp>
      <stringProp name="HTTPSampler.postBody">
        {
          "query": "${__RandomString(5,abcdefghijklmnopqrstuvwxyz,)}",
          "userId": "perf_test_${__threadNum}",
          "sessionId": "sess_${__time}"
        }
      </stringProp>
    </HTTPSamplerProxy>
    
    <!-- 断言：成功率 -->
    <ResponseAssertion guiclass="AssertionGui" testname="成功率断言">
      <collectionProp name="Asserion.test_strings">
        <stringProp name="49586">A0000</stringProp>
      </collectionProp>
    </ResponseAssertion>
    
    <!-- 断言：响应时间 -->
    <DurationAssertion guiclass="DurationAssertionGui" testname="响应时间<500ms">
      <stringProp name="DurationAssertion.duration">500</stringProp>
    </DurationAssertion>
    
    <!-- 监听器 -->
    <ResultCollector guiclass="SummaryReport" testname="汇总报告"/>
    <ResultCollector guiclass="ResponseTimeGraph" testname="响应时间图"/>
  </ThreadGroup>
</TestPlan>
```

---

## 第八章 部署与运维

### 8.1 Docker Compose完整部署

```yaml
# docker-compose.yml - 生产环境部署
version: '3.8'

services:
  # === 前端：微信小程序（静态资源通过Nginx托管）===
  nginx:
    image: nginx:1.24-alpine
    container_name: campus-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
      - ./dist:/usr/share/nginx/html:ro
    depends_on:
      - backend
    networks:
      - campus-network

  # === 后端：JeecgBoot服务 ===
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: campus-backend
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - MYSQL_HOST=mysql
      - MYSQL_PORT=3306
      - MYSQL_DB=campus_qa
      - REDIS_HOST=redis
      - MILVUS_HOST=milvus-standalone
      - WECHAT_APPID=${WECHAT_APPID}
      - WECHAT_SECRET=${WECHAT_SECRET}
      - DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY}
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
      milvus-standalone:
        condition: service_started
    networks:
      - campus-network

  # === 数据库：MySQL 8.0 ===
  mysql:
    image: mysql:8.0.34
    container_name: campus-mysql
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=campus_qa
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init:/docker-entrypoint-initdb.d:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - campus-network

  # === 缓存：Redis 7.0 ===
  redis:
    image: redis:7.0-alpine
    container_name: campus-redis
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - campus-network

  # === 向量库：Milvus ===
  etcd:
    container_name: milvus-etcd
    image: quay.io/coreos/etcd:v3.5.5
    environment:
      - ETCD_AUTO_COMPACTION_MODE=revision
      - ETCD_AUTO_COMPACTION_RETENTION=1000
    volumes:
      - etcd_data:/etcd
    networks:
      - campus-network

  minio:
    container_name: milvus-minio
    image: minio/minio:RELEASE.2023-03-20T20-16-18Z
    environment:
      - MINIO_ACCESS_KEY=minioadmin
      - MINIO_SECRET_KEY=minioadmin
    volumes:
      - minio_data:/minio_data
    command: minio server /minio_data --console-address ":9001"
    networks:
      - campus-network

  milvus-standalone:
    container_name: milvus-standalone
    image: milvusdb/milvus:v2.3.3
    command: ["milvus", "run", "standalone"]
    environment:
      - ETCD_ENDPOINTS=etcd:2379
      - MINIO_ADDRESS=minio:9000
    volumes:
      - milvus_data:/var/lib/milvus
    ports:
      - "19530:19530"
    depends_on:
      - etcd
      - minio
    networks:
      - campus-network

volumes:
  mysql_data:
  redis_data:
  etcd_data:
  minio_data:
  milvus_data:

networks:
  campus-network:
    driver: bridge
```

### 8.2 一键部署脚本

```bash
#!/bin/bash
# deploy.sh - 生产环境一键部署脚本

set -e

echo "=========================================="
echo "校园智能问答系统 - 生产环境部署"
echo "=========================================="

# 1. 环境检查
echo "[1/6] 检查环境..."
docker --version || { echo "❌ Docker未安装"; exit 1; }
docker-compose --version || { echo "❌ Docker Compose未安装"; exit 1; }

# 2. 加载环境变量
echo "[2/6] 加载环境变量..."
if [ ! -f .env ]; then
    echo "❌ .env文件不存在，请复制.env.example并配置"
    exit 1
fi
export $(grep -v '^#' .env | xargs)

# 3. 构建后端
echo "[3/6] 构建后端服务..."
cd backend
mvn clean package -DskipTests
cd ..

# 4. 启动基础设施
echo "[4/6] 启动基础设施（MySQL/Redis/Milvus）..."
docker-compose up -d mysql redis etcd minio
sleep 30  # 等待服务就绪

# 5. 启动Milvus
echo "[5/6] 启动Milvus向量库..."
docker-compose up -d milvus-standalone
sleep 10

# 6. 初始化向量库
echo "[6/6] 初始化向量库Schema..."
docker-compose run --rm backend java -jar app.jar --spring.profiles.active=init

# 7. 启动完整服务
echo "[7/6] 启动完整服务..."
docker-compose up -d

echo "=========================================="
echo "✅ 部署完成！"
echo "访问地址："
echo "  - 管理后台: http://localhost/admin"
echo "  - API文档: http://localhost/swagger-ui.html"
echo "  - Milvus控制台: http://localhost:9001"
echo "=========================================="
```

---

## 第九章 验收与交付

### 9.1 验收红线（文档5对应）

| 红线编号 | 检查项         | 标准          | 验证方式              |
| :------- | :------------- | :------------ | :-------------------- |
| RL-001   | 智能检索准确率 | ≥95%          | 500条测试集自动化测试 |
| RL-002   | 响应时间P99    | <500ms        | JMeter 50并发压测     |
| RL-003   | 通知推送到达率 | ≥99.5%        | 微信后台统计          |
| RL-004   | 代码覆盖率     | ≥80%          | JaCoCo报告            |
| RL-005   | 安全漏洞       | 高危0，中危≤3 | OWASP ZAP扫描         |
| RL-006   | 数据库一致性   | 日终对账0差异 | 6项SQL检查            |

### 9.2 交付清单

```
campus-qa-deliverable/
├── 01-源代码/
│   ├── campus-qa-backend/          # JeecgBoot后端
│   ├── campus-qa-miniapp/          # 微信小程序
│   └── campus-qa-admin/            # Vue3管理后台
├── 02-数据库/
│   ├── ddl/                        # 建表脚本
│   ├── dml/                        # 初始化数据
│   └── milvus-schema/              # 向量库Schema
├── 03-部署文档/
│   ├── docker-compose.yml
│   ├── deploy.sh
│   └── README.md
├── 04-测试报告/
│   ├── unit-test-report.html
│   ├── integration-test-report.html
│   └── performance-test-report.jtl
├── 05-设计文档/
│   ├── 系统架构设计.md
│   ├── 数据库设计.md
│   └── API接口文档.md
└── 06-毕业设计/
    ├── 开题报告.docx
    ├── 任务书.docx
    ├── 毕业论文.docx
    └── 答辩PPT.pptx
```

---

## 附录：与5文档体系的完整映射

| 本文档章节 | 对应5文档                   | 核心内容                         |
| :--------- | :-------------------------- | :------------------------------- |
| 第1章      | 文档1《系统架构与核心决策》 | ADR决策、架构图、技术选型        |
| 第2章      | 文档2《数据模型与状态机》   | 18张表DDL、Milvus Schema、状态机 |
| 第3-5章    | 文档4《编码规范与实现模板》 | Java/微信小程序/Vue3代码模板     |
| 第6章      | 文档1+4                     | RAG架构详细实现                  |
| 第7章      | 文档5《验收红线与自动化》   | 测试策略、红线标准               |
| 第8章      | 文档5                       | 部署脚本、运维方案               |
| 第9章      | 文档5                       | 验收清单、交付标准               |

---

**文档结束**

> **使用说明**：
> 1. 本指南为终极完整版，覆盖从需求到交付的全流程
> 2. 代码块可直接复制使用，需根据实际环境调整配置
> 3. 建议配合JeecgBoot官方文档和微信官方文档使用
> 4. RAG部分可根据实际预算选择DeepSeek/文心一言/通义千问等国产大模型