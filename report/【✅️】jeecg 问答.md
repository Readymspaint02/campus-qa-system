 我来为你提供**JeecgBoot改造为校园问答系统的完整方案**，包含后端改造步骤和微信小程序对接代码模板。

---

# JeecgBoot校园问答系统改造完整指南

## 一、技术架构选型

| 层级           | 技术选择                         | 版本   |
| :------------- | :------------------------------- | :----- |
| **后端**       | JeecgBoot SpringBoot 2.7分支     | 3.8.5+ |
| **管理后台**   | JeecgBoot自带Vue3                | -      |
| **微信小程序** | 原生微信小程序 + JeecgUniapp参考 | -      |
| **智能分词**   | jieba-java                       | 1.0.2  |
| **数据库**     | MySQL                            | 8.0    |

---

## 二、后端改造详细步骤

### 步骤1：环境准备与项目初始化

```bash
# 1. 克隆JeecgBoot SpringBoot 2.7分支（匹配开题报告技术栈）
git clone -b springboot2 https://github.com/jeecgboot/JeecgBoot.git campus-qa-system

# 2. 导入IDEA
# File -> Open -> 选择 campus-qa-system/pom.xml

# 3. 创建校园问答专用模块（推荐方式，不污染原有代码）
# 在 jeecg-module-system 下新建子模块
```

### 步骤2：创建校园问答模块（campus-qa）

```
campus-qa-system/
├── jeecg-module-system/
│   ├── jeecg-system-biz/          # 原有系统业务
│   └── campus-qa-biz/             # 【新建】校园问答业务模块
│       ├── src/main/java/org/jeecg/modules/qa/
│       │   ├── controller/          # 控制器
│       │   ├── service/             # 服务层
│       │   ├── mapper/              # MyBatis-Plus
│       │   ├── entity/              # 实体类
│       │   └── vo/                  # 视图对象
│       └── pom.xml
```

**campus-qa-biz/pom.xml**：
```xml
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
    
    <artifactId>campus-qa-biz</artifactId>
    <name>campus-qa-biz</name>
    <description>校园智能问答系统业务模块</description>
    
    <dependencies>
        <!-- 依赖jeecg-system-common -->
        <dependency>
            <groupId>org.jeecgframework.boot</groupId>
            <artifactId>jeecg-system-common</artifactId>
            <version>${jeecgboot.version}</version>
        </dependency>
        
        <!-- jieba分词 -->
        <dependency>
            <groupId>com.huaban</groupId>
            <artifactId>jieba-analysis</artifactId>
            <version>1.0.2</version>
        </dependency>
        
        <!-- HanLP备选（如需语义分析） -->
        <dependency>
            <groupId>com.hankcs</groupId>
            <artifactId>hanlp</artifactId>
            <version>portable-1.8.4</version>
        </dependency>
    </dependencies>
</project>
```

### 步骤3：数据库表设计（JeecgBoot风格）

```sql
-- 问答知识库表
CREATE TABLE `qa_knowledge` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `question` varchar(500) NOT NULL COMMENT '问题',
    `answer` text NOT NULL COMMENT '答案',
    `category_id` varchar(36) DEFAULT NULL COMMENT '分类ID',
    `category_name` varchar(100) DEFAULT NULL COMMENT '分类名称',
    `keywords` varchar(500) DEFAULT NULL COMMENT '关键词（逗号分隔）',
    `segment_result` varchar(1000) DEFAULT NULL COMMENT '分词结果（JSON）',
    `hot` tinyint(1) DEFAULT 0 COMMENT '是否热点：0否 1是',
    `view_count` int DEFAULT 0 COMMENT '浏览次数',
    `useful_count` int DEFAULT 0 COMMENT '有用次数',
    `useless_count` int DEFAULT 0 COMMENT '无用次数',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `department_id` varchar(36) DEFAULT NULL COMMENT '所属部门ID',
    `department_name` varchar(100) DEFAULT NULL COMMENT '所属部门名称',
    `create_by` varchar(36) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(36) DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `sys_org_code` varchar(64) DEFAULT NULL COMMENT '部门编码',
    PRIMARY KEY (`id`),
    KEY `idx_question` (`question`),
    KEY `idx_category` (`category_id`),
    KEY `idx_hot` (`hot`),
    KEY `idx_status` (`status`),
    FULLTEXT KEY `ft_question` (`question`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答知识库';

-- 问答分类表
CREATE TABLE `qa_category` (
    `id` varchar(36) NOT NULL,
    `name` varchar(100) NOT NULL COMMENT '分类名称',
    `code` varchar(50) NOT NULL COMMENT '分类编码',
    `parent_id` varchar(36) DEFAULT '0' COMMENT '父分类ID',
    `icon` varchar(255) DEFAULT NULL COMMENT '图标',
    `sort_no` int DEFAULT 0 COMMENT '排序',
    `description` varchar(500) DEFAULT NULL,
    `status` tinyint(1) DEFAULT 1,
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答分类';

-- 用户查询历史表
CREATE TABLE `qa_query_history` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `query` varchar(500) NOT NULL COMMENT '查询内容',
    `segment_result` varchar(1000) DEFAULT NULL COMMENT '分词结果',
    `answer_id` varchar(36) DEFAULT NULL COMMENT '匹配答案ID',
    `answer` text DEFAULT NULL COMMENT '答案内容',
    `confidence` decimal(3,2) DEFAULT NULL COMMENT '匹配置信度',
    `useful` tinyint(1) DEFAULT NULL COMMENT '是否有用：0无用 1有用',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='查询历史';

-- 用户反馈表
CREATE TABLE `qa_feedback` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL,
    `type` varchar(20) NOT NULL COMMENT '类型：ANSWER_ISSUE/FEATURE_REQUEST/BUG_REPORT',
    `target_id` varchar(36) DEFAULT NULL COMMENT '关联问答ID',
    `content` text NOT NULL,
    `contact` varchar(50) DEFAULT NULL,
    `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/RESOLVED/REJECTED',
    `reply` text DEFAULT NULL COMMENT '回复内容',
    `reply_time` datetime DEFAULT NULL,
    `handler_id` varchar(36) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈';

-- 通知公告表（扩展JeecgBoot原有表，或独立创建）
CREATE TABLE `qa_notice` (
    `id` varchar(36) NOT NULL,
    `title` varchar(200) NOT NULL,
    `content` text NOT NULL,
    `summary` varchar(500) DEFAULT NULL,
    `department_id` varchar(36) DEFAULT NULL,
    `department_name` varchar(100) DEFAULT NULL,
    `type` varchar(20) DEFAULT 'NORMAL' COMMENT '类型：IMPORTANT/NORMAL',
    `target_range` varchar(20) DEFAULT 'ALL' COMMENT '范围：ALL/SPECIFIC',
    `attachment_url` varchar(500) DEFAULT NULL,
    `read_count` int DEFAULT 0,
    `publish_time` datetime DEFAULT NULL,
    `expire_time` datetime DEFAULT NULL,
    `status` varchar(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED/EXPIRED',
    `create_by` varchar(36) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(36) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_department` (`department_id`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园通知';

-- 用户订阅表
CREATE TABLE `qa_user_subscribe` (
    `id` varchar(36) NOT NULL,
    `user_id` varchar(36) NOT NULL,
    `department_id` varchar(36) NOT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_dept` (`user_id`, `department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户订阅';
```

### 步骤4：JeecgBoot代码生成（核心！）

```java
// 使用JeecgBoot的代码生成器，生成基础CRUD
// 路径：系统管理 -> 代码生成器 -> 导入数据库表 -> 生成代码

// 生成配置（以qa_knowledge为例）：
// 1. 表名：qa_knowledge
// 2. 实体名：QaKnowledge
// 3. 包名：org.jeecg.modules.qa
// 4. 模板：单表（默认）

// 生成后文件结构：
campus-qa-biz/src/main/java/org/jeecg/modules/qa/
├── controller/
│   └── QaKnowledgeController.java    # 生成后改造
├── entity/
│   └── QaKnowledge.java              # 生成后微调
├── mapper/
│   └── QaKnowledgeMapper.java
│   └── xml/
│       └── QaKnowledgeMapper.xml
├── service/
│   └── IQaKnowledgeService.java
│   └── impl/
│       └── QaKnowledgeServiceImpl.java # 核心改造：添加智能匹配逻辑
└── vo/
    └── QaKnowledgeVO.java
```

### 步骤5：核心Service实现（智能问答引擎）

**IQaKnowledgeService.java**（扩展生成的方法）：
```java
package org.jeecg.modules.qa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.qa.entity.QaKnowledge;
import org.jeecg.modules.qa.vo.QaAskVO;
import org.jeecg.modules.qa.vo.QaAnswerVO;

import java.util.List;

/**
 * 问答知识库Service
 */
public interface IQaKnowledgeService extends IService<QaKnowledge> {
    
    /**
     * 智能提问（核心方法）
     * @param askVO 提问内容
     * @return 匹配答案
     */
    QaAnswerVO ask(QaAskVO askVO);
    
    /**
     * 分词测试
     * @param text 待分词文本
     * @return 分词结果
     */
    List<String> segment(String text);
    
    /**
     * 获取热门问题
     */
    List<QaKnowledge> getHotQuestions(String categoryId, Integer limit);
    
    /**
     * 获取推荐问题（基于用户历史）
     */
    List<String> getSuggestions(String userId, String keyword);
}
```

**QaKnowledgeServiceImpl.java**（核心实现）：
```java
package org.jeecg.modules.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.qa.entity.QaKnowledge;
import org.jeecg.modules.qa.mapper.QaKnowledgeMapper;
import org.jeecg.modules.qa.mapper.QaQueryHistoryMapper;
import org.jeecg.modules.qa.service.IQaKnowledgeService;
import org.jeecg.modules.qa.vo.QaAskVO;
import org.jeecg.modules.qa.vo.QaAnswerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QaKnowledgeServiceImpl extends ServiceImpl<QaKnowledgeMapper, QaKnowledge> 
        implements IQaKnowledgeService {
    
    @Autowired
    private QaKnowledgeMapper qaKnowledgeMapper;
    
    @Autowired
    private QaQueryHistoryMapper queryHistoryMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // Jieba分词器（单例）
    private final JiebaSegmenter segmenter = new JiebaSegmenter();
    
    // 匹配阈值
    private static final double MATCH_THRESHOLD = 0.6;
    private static final int MAX_SUGGESTIONS = 5;
    
    @Override
    public QaAnswerVO ask(QaAskVO askVO) {
        String query = askVO.getQuestion();
        String userId = askVO.getUserId();
        String sessionId = askVO.getSessionId();
        
        log.info("用户提问: {}, userId: {}", query, userId);
        
        // 1. Jieba分词
        List<String> queryTokens = segment(query);
        String segmentResult = String.join(",", queryTokens);
        
        // 2. 构建匹配逻辑（多策略）
        List<QaKnowledge> candidates = findCandidates(queryTokens);
        
        // 3. 计算相似度并排序
        QaKnowledge bestMatch = null;
        double maxScore = 0;
        
        for (QaKnowledge candidate : candidates) {
            double score = calculateSimilarity(queryTokens, candidate);
            if (score > maxScore) {
                maxScore = score;
                bestMatch = candidate;
            }
        }
        
        // 4. 保存查询历史
        saveQueryHistory(userId, query, segmentResult, bestMatch, maxScore);
        
        // 5. 构建返回结果
        QaAnswerVO answerVO = new QaAnswerVO();
        
        if (bestMatch != null && maxScore >= MATCH_THRESHOLD) {
            // 匹配成功
            answerVO.setSuccess(true);
            answerVO.setAnswerId(bestMatch.getId());
            answerVO.setQuestion(bestMatch.getQuestion());
            answerVO.setAnswer(bestMatch.getAnswer());
            answerVO.setConfidence(round(maxScore, 2));
            answerVO.setSource("KNOWLEDGE_BASE");
            answerVO.setDepartment(bestMatch.getDepartmentName());
            
            // 更新浏览次数
            redisTemplate.opsForHash().increment("qa:view_count", bestMatch.getId(), 1);
            
            // 获取相关问题
            answerVO.setRelatedQuestions(getRelatedQuestions(bestMatch.getCategoryId(), bestMatch.getId()));
            
        } else {
            // 匹配失败，返回建议
            answerVO.setSuccess(false);
            answerVO.setMessage("未找到精确答案，为您推荐以下相关问题：");
            answerVO.setSuggestions(getHotQuestions(null, MAX_SUGGESTIONS).stream()
                    .map(QaKnowledge::getQuestion)
                    .collect(Collectors.toList()));
            answerVO.setContact(getDefaultContact());
        }
        
        return answerVO;
    }
    
    @Override
    public List<String> segment(String text) {
        // 使用搜索引擎模式，适合问答场景
        List<SegToken> tokens = segmenter.process(text, JiebaSegmenter.SegMode.SEARCH);
        // 过滤停用词（可扩展）
        return tokens.stream()
                .map(token -> token.word.trim())
                .filter(word -> word.length() > 1) // 过滤单字
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 查找候选答案（多策略）
     */
    private List<QaKnowledge> findCandidates(List<String> queryTokens) {
        Set<QaKnowledge> candidates = new HashSet<>();
        
        // 策略1：关键词匹配（MySQL LIKE）
        for (String token : queryTokens) {
            LambdaQueryWrapper<QaKnowledge> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(QaKnowledge::getQuestion, token)
                   .or()
                   .like(QaKnowledge::getKeywords, token)
                   .eq(QaKnowledge::getStatus, 1);
            candidates.addAll(qaKnowledgeMapper.selectList(wrapper));
        }
        
        // 策略2：如果结果太少，扩展搜索（全文检索，需开启MySQL全文索引）
        if (candidates.size() < 5) {
            String queryText = String.join(" ", queryTokens);
            List<QaKnowledge> fullTextResults = qaKnowledgeMapper.fullTextSearch(queryText);
            candidates.addAll(fullTextResults);
        }
        
        return new ArrayList<>(candidates);
    }
    
    /**
     * 计算相似度（Jaccard系数 + 权重优化）
     */
    private double calculateSimilarity(List<String> queryTokens, QaKnowledge candidate) {
        // 获取候选问题的分词
        List<String> candidateTokens;
        if (candidate.getSegmentResult() != null) {
            candidateTokens = Arrays.asList(candidate.getSegmentResult().split(","));
        } else {
            candidateTokens = segment(candidate.getQuestion());
        }
        
        // Jaccard相似度
        Set<String> querySet = new HashSet<>(queryTokens);
        Set<String> candidateSet = new HashSet<>(candidateTokens);
        
        Set<String> intersection = new HashSet<>(querySet);
        intersection.retainAll(candidateSet);
        
        Set<String> union = new HashSet<>(querySet);
        union.addAll(candidateSet);
        
        if (union.isEmpty()) return 0;
        
        double jaccard = (double) intersection.size() / union.size();
        
        // 权重优化：关键词匹配加分
        double keywordBonus = 0;
        if (candidate.getKeywords() != null) {
            String keywords = candidate.getKeywords().toLowerCase();
            long keywordMatch = queryTokens.stream()
                    .filter(t -> keywords.contains(t.toLowerCase()))
                    .count();
            keywordBonus = keywordMatch * 0.1;
        }
        
        // 热度加权（热门问题适当提升）
        double hotBonus = candidate.getHot() != null && candidate.getHot() == 1 ? 0.05 : 0;
        
        return Math.min(jaccard + keywordBonus + hotBonus, 1.0);
    }
    
    @Override
    public List<QaKnowledge> getHotQuestions(String categoryId, Integer limit) {
        LambdaQueryWrapper<QaKnowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QaKnowledge::getStatus, 1)
               .eq(QaKnowledge::getHot, 1)
               .orderByDesc(QaKnowledge::getViewCount);
        
        if (categoryId != null) {
            wrapper.eq(QaKnowledge::getCategoryId, categoryId);
        }
        
        return qaKnowledgeMapper.selectList(wrapper.last("LIMIT " + (limit != null ? limit : 10)));
    }
    
    @Override
    public List<String> getSuggestions(String userId, String keyword) {
        // 基于用户历史 + 热门问题推荐
        List<String> suggestions = new ArrayList<>();
        
        // 1. 用户历史相关问题
        List<String> historyQueries = queryHistoryMapper.selectRecentQueries(userId, 5);
        suggestions.addAll(historyQueries);
        
        // 2. 关键词匹配的热门问题
        if (keyword != null && !keyword.isEmpty()) {
            LambdaQueryWrapper<QaKnowledge> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(QaKnowledge::getQuestion, keyword)
                   .eq(QaKnowledge::getStatus, 1)
                   .orderByDesc(QaKnowledge::getViewCount)
                   .last("LIMIT " + (MAX_SUGGESTIONS - suggestions.size()));
            List<QaKnowledge> matches = qaKnowledgeMapper.selectList(wrapper);
            suggestions.addAll(matches.stream().map(QaKnowledge::getQuestion).collect(Collectors.toList()));
        }
        
        // 去重并限制数量
        return suggestions.stream().distinct().limit(MAX_SUGGESTIONS).collect(Collectors.toList());
    }
    
    /**
     * 保存查询历史
     */
    private void saveQueryHistory(String userId, String query, String segmentResult, 
                                  QaKnowledge bestMatch, double score) {
        // 异步保存（可改为MQ）
        try {
            // 实现略，参考JeecgBoot的Save类
        } catch (Exception e) {
            log.error("保存查询历史失败", e);
        }
    }
    
    private QaAnswerVO.ContactVO getDefaultContact() {
        QaAnswerVO.ContactVO contact = new QaAnswerVO.ContactVO();
        contact.setDepartment("信息中心");
        contact.setPhone("027-12345678");
        contact.setOnlineTime("工作日 8:30-17:30");
        return contact;
    }
    
    private double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
```

### 步骤6：Controller层（REST API）

**QaKnowledgeController.java**：
```java
package org.jeecg.modules.qa.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.qa.entity.QaKnowledge;
import org.jeecg.modules.qa.service.IQaKnowledgeService;
import org.jeecg.modules.qa.vo.QaAskVO;
import org.jeecg.modules.qa.vo.QaAnswerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "校园问答-智能问答")
@RestController
@RequestMapping("/qa/knowledge")
public class QaKnowledgeController extends JeecgController<QaKnowledge, IQaKnowledgeService> {
    
    @Autowired
    private IQaKnowledgeService qaKnowledgeService;
    
    /**
     * 【核心API】智能提问
     * 微信小程序调用此接口
     */
    @ApiOperation(value = "智能提问", notes = "Jieba分词+智能匹配")
    @PostMapping("/ask")
    public Result<QaAnswerVO> ask(@RequestBody QaAskVO askVO) {
        // 参数校验
        if (askVO.getQuestion() == null || askVO.getQuestion().trim().isEmpty()) {
            return Result.error("问题不能为空");
        }
        
        QaAnswerVO answer = qaKnowledgeService.ask(askVO);
        return Result.ok(answer);
    }
    
    /**
     * 分词测试（调试用）
     */
    @ApiOperation(value = "分词测试")
    @GetMapping("/segment")
    public Result<List<String>> segment(@RequestParam String text) {
        List<String> tokens = qaKnowledgeService.segment(text);
        return Result.ok(tokens);
    }
    
    /**
     * 获取热门问题
     */
    @ApiOperation(value = "热门问题")
    @GetMapping("/hot")
    public Result<List<QaKnowledge>> hotQuestions(
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<QaKnowledge> list = qaKnowledgeService.getHotQuestions(categoryId, limit);
        return Result.ok(list);
    }
    
    /**
     * 获取问题推荐
     */
    @ApiOperation(value = "问题推荐")
    @GetMapping("/suggestions")
    public Result<List<String>> suggestions(
            @RequestParam String userId,
            @RequestParam(required = false) String keyword) {
        List<String> list = qaKnowledgeService.getSuggestions(userId, keyword);
        return Result.ok(list);
    }
    
    // CRUD方法由JeecgBoot代码生成器自动生成，继承即可
}
```

### 步骤7：VO对象定义

**QaAskVO.java**：
```java
package org.jeecg.modules.qa.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("提问请求VO")
public class QaAskVO {
    
    @ApiModelProperty(value = "问题内容", required = true)
    private String question;
    
    @ApiModelProperty(value = "用户ID", required = true)
    private String userId;
    
    @ApiModelProperty(value = "会话ID（连续对话）")
    private String sessionId;
    
    @ApiModelProperty(value = "上下文（上次问题）")
    private String context;
}
```

**QaAnswerVO.java**：
```java
package org.jeecg.modules.qa.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("问答响应VO")
public class QaAnswerVO {
    
    @ApiModelProperty("是否匹配成功")
    private Boolean success;
    
    @ApiModelProperty("答案ID")
    private String answerId;
    
    @ApiModelProperty("匹配的问题")
    private String question;
    
    @ApiModelProperty("答案内容")
    private String answer;
    
    @ApiModelProperty("置信度0-1")
    private Double confidence;
    
    @ApiModelProperty("来源：KNOWLEDGE_BASE/FAQ/AI_GENERATED")
    private String source;
    
    @ApiModelProperty("所属部门")
    private String department;
    
    @ApiModelProperty("提示信息（匹配失败时）")
    private String message;
    
    @ApiModelProperty("相关问题推荐")
    private List<String> relatedQuestions;
    
    @ApiModelProperty("建议问题（匹配失败时）")
    private List<String> suggestions;
    
    @ApiModelProperty("联系方式（匹配失败时）")
    private ContactVO contact;
    
    @Data
    public static class ContactVO {
        private String department;
        private String phone;
        private String onlineTime;
    }
}
```

---

## 三、微信小程序对接代码模板

### 3.1 小程序项目结构

```
campus-qa-weapp/
├── app.js                    # 全局入口
├── app.json                  # 全局配置
├── app.wxss                  # 全局样式
├── config/
│   └── api.js                # API配置
├── utils/
│   ├── request.js            # 请求封装（核心）
│   ├── auth.js               # 登录鉴权
│   └── storage.js            # 本地存储
├── pages/
│   ├── index/                # 首页（搜索入口）
│   ├── ask/                  # 问答页面
│   ├── category/             # 分类浏览
│   ├── history/              # 查询历史
│   ├── notice/               # 通知公告
│   ├── guide/                # 办事指引
│   ├── feedback/             # 反馈提交
│   └── my/                   # 个人中心
└── components/
    ├── qa-card/              # 问答卡片
    ├── notice-item/          # 通知项
    └── loading/              # 加载动画
```

### 3.2 核心配置文件

**config/api.js**：
```javascript
/**
 * API配置
 * 开发环境/生产环境切换
 */
const ENV = 'dev'; // dev/prod

const CONFIG = {
  dev: {
    baseURL: 'http://localhost:8080/jeecg-boot',  // 本地开发
    // baseURL: 'http://192.168.1.100:8080/jeecg-boot', // 真机调试
  },
  prod: {
    baseURL: 'https://your-domain.com/jeecg-boot', // 生产环境
  }
};

const API_BASE = CONFIG[ENV].baseURL;

module.exports = {
  // 认证
  AUTH: {
    WECHAT_LOGIN: `${API_BASE}/sys/wechatLogin`,  // 需后端实现微信登录
  },
  
  // 问答模块
  QA: {
    ASK: `${API_BASE}/qa/knowledge/ask`,           // 智能提问 【核心】
    HOT: `${API_BASE}/qa/knowledge/hot`,           // 热门问题
    SUGGESTIONS: `${API_BASE}/qa/knowledge/suggestions`, // 问题推荐
    SEGMENT: `${API_BASE}/qa/knowledge/segment`,     // 分词测试
  },
  
  // 分类
  CATEGORY: {
    LIST: `${API_BASE}/qa/category/list`,
    DETAIL: `${API_BASE}/qa/category/detail`,
  },
  
  // 通知
  NOTICE: {
    LIST: `${API_BASE}/qa/notice/list`,
    DETAIL: `${API_BASE}/qa/notice/detail`,
    SUBSCRIBE: `${API_BASE}/qa/notice/subscribe`,
  },
  
  // 历史记录
  HISTORY: {
    LIST: `${API_BASE}/qa/history/list`,
    CLEAR: `${API_BASE}/qa/history/clear`,
  },
  
  // 反馈
  FEEDBACK: {
    SUBMIT: `${API_BASE}/qa/feedback/submit`,
    LIST: `${API_BASE}/qa/feedback/myList`,
  },
  
  // 用户
  USER: {
    INFO: `${API_BASE}/sys/user/info`,
    UPDATE: `${API_BASE}/sys/user/update`,
  }
};
```

### 3.3 请求封装（核心）

**utils/request.js**：
```javascript
/**
 * HTTP请求封装
 * 对接JeecgBoot后端，处理token、错误码
 */
const API = require('../config/api');
const Auth = require('./auth');

// 请求拦截器配置
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = Auth.getToken();
    
    wx.request({
      url: options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'X-Access-Token': token,           // JeecgBoot token字段
        'X-Request-ID': generateUUID(),     // 幂等性
        ...options.header
      },
      timeout: 30000,
      
      success: (res) => {
        const { statusCode, data } = res;
        
        if (statusCode === 200) {
          // JeecgBoot统一响应格式：{ success: true, result: {}, message: '' }
          // 或 { code: 200, data: {}, message: '' }
          
          if (data.success || data.code === 200 || data.code === 0) {
            resolve(data.result || data.data);
          } else if (data.code === 401) {
            // token过期，重新登录
            Auth.clearToken();
            wx.reLaunch({ url: '/pages/login/index' });
            reject(new Error('登录已过期'));
          } else {
            // 业务错误
            wx.showToast({
              title: data.message || '请求失败',
              icon: 'none'
            });
            reject(new Error(data.message));
          }
        } else {
          wx.showToast({
            title: `网络错误: ${statusCode}`,
            icon: 'none'
          });
          reject(new Error(`HTTP ${statusCode}`));
        }
      },
      
      fail: (err) => {
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
};

// 便捷方法
const get = (url, params = {}) => request({ url, method: 'GET', data: params });
const post = (url, data = {}) => request({ url, method: 'POST', data });

// 生成UUID（幂等性）
const generateUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
};

module.exports = {
  request,
  get,
  post
};
```

**utils/auth.js**：
```javascript
/**
 * 登录鉴权
 * 对接JeecgBoot的token机制
 */
const STORAGE_KEY = 'jeecg_access_token';
const USER_INFO_KEY = 'user_info';

module.exports = {
  // 获取token
  getToken() {
    return wx.getStorageSync(STORAGE_KEY);
  },
  
  // 设置token
  setToken(token) {
    wx.setStorageSync(STORAGE_KEY, token);
  },
  
  // 清除token
  clearToken() {
    wx.removeStorageSync(STORAGE_KEY);
    wx.removeStorageSync(USER_INFO_KEY);
  },
  
  // 检查登录状态
  checkLogin() {
    return !!this.getToken();
  },
  
  // 获取用户信息
  getUserInfo() {
    return wx.getStorageSync(USER_INFO_KEY);
  },
  
  // 设置用户信息
  setUserInfo(userInfo) {
    wx.setStorageSync(USER_INFO_KEY, userInfo);
  },
  
  // 微信登录（调用后端接口）
  async wechatLogin() {
    try {
      // 1. 获取微信code
      const { code } = await wx.login();
      
      // 2. 获取用户信息（需用户授权）
      const { userInfo: wxUserInfo } = await wx.getUserProfile({
        desc: '用于完善用户资料'
      });
      
      // 3. 调用JeecgBoot后端登录接口
      const API = require('../config/api');
      const { post } = require('./request');
      
      const res = await post(API.AUTH.WECHAT_LOGIN, {
        code: code,
        nickName: wxUserInfo.nickName,
        avatarUrl: wxUserInfo.avatarUrl
      });
      
      // 4. 保存token
      this.setToken(res.token);
      this.setUserInfo(res.userInfo);
      
      return res;
    } catch (err) {
      console.error('登录失败', err);
      throw err;
    }
  }
};
```

### 3.4 核心页面：智能问答（pages/ask/ask.js）

```javascript
// pages/ask/ask.js
const API = require('../../config/api');
const { post, get } = require('../../utils/request');
const Auth = require('../../utils/auth');

Page({
  data: {
    question: '',
    answer: null,
    loading: false,
    history: [],           // 对话历史
    suggestions: [],       // 输入推荐
    relatedQuestions: [],  // 相关问题
    showFeedback: false,   // 显示反馈按钮
    feedbackType: null,    // 反馈类型：useful/useless
  },

  onLoad(options) {
    // 如果有传入问题，直接提问
    if (options.question) {
      this.setData({ question: decodeURIComponent(options.question) });
      this.handleAsk();
    }
    
    // 加载热门问题作为初始推荐
    this.loadHotQuestions();
  },

  // 输入框变化
  onInputChange(e) {
    const question = e.detail.value;
    this.setData({ question });
    
    // 输入超过2字，触发推荐
    if (question.length >= 2) {
      this.loadSuggestions(question);
    }
  },

  // 加载问题推荐
  async loadSuggestions(keyword) {
    try {
      const userInfo = Auth.getUserInfo();
      const list = await get(API.QA.SUGGESTIONS, {
        userId: userInfo.id,
        keyword: keyword
      });
      this.setData({ suggestions: list.slice(0, 5) });
    } catch (err) {
      console.error('加载推荐失败', err);
    }
  },

  // 加载热门问题
  async loadHotQuestions() {
    try {
      const list = await get(API.QA.HOT, { limit: 5 });
      this.setData({ 
        suggestions: list.map(item => item.question)
      });
    } catch (err) {
      console.error('加载热门问题失败', err);
    }
  },

  // 点击推荐问题
  onTapSuggestion(e) {
    const question = e.currentTarget.dataset.item;
    this.setData({ question, suggestions: [] });
    this.handleAsk();
  },

  // 提交问题（核心方法）
  async handleAsk() {
    const { question, loading } = this.data;
    
    if (!question.trim()) {
      wx.showToast({ title: '请输入问题', icon: 'none' });
      return;
    }
    
    if (loading) return;
    
    this.setData({ loading: true, answer: null, showFeedback: false });
    
    // 显示加载动画
    wx.showLoading({ title: '思考中...' });
    
    try {
      const userInfo = Auth.getUserInfo();
      
      // 【核心】调用JeecgBoot后端智能问答接口
      const res = await post(API.QA.ASK, {
        question: question.trim(),
        userId: userInfo.id,
        sessionId: this.generateSessionId(),
        context: this.getLastQuestion()
      });
      
      // 更新对话历史
      const history = this.data.history;
      history.push({
        type: 'ask',
        content: question,
        time: new Date().toLocaleTimeString()
      });
      
      // 处理响应
      if (res.success) {
        // 匹配成功
        history.push({
          type: 'answer',
          content: res.answer,
          question: res.question,
          confidence: res.confidence,
          department: res.department,
          time: new Date().toLocaleTimeString()
        });
        
        this.setData({
          answer: res,
          history,
          relatedQuestions: res.relatedQuestions || [],
          showFeedback: true,
          suggestions: []
        });
        
        // 保存到本地历史
        this.saveLocalHistory(question, res);
        
      } else {
        // 匹配失败，显示建议
        history.push({
          type: 'answer',
          content: res.message || '未找到答案',
          isFallback: true,
          contact: res.contact,
          time: new Date().toLocaleTimeString()
        });
        
        this.setData({
          answer: res,
          history,
          suggestions: res.suggestions || [],
          showFeedback: false
        });
      }
      
      // 清空输入
      this.setData({ question: '' });
      
    } catch (err) {
      wx.showToast({ 
        title: err.message || '请求失败，请重试', 
        icon: 'none',
        duration: 3000
      });
    } finally {
      this.setData({ loading: false });
      wx.hideLoading();
    }
  },

  // 反馈答案是否有用
  async submitFeedback(e) {
    const { type } = e.currentTarget.dataset;
    const { answer, history } = this.data;
    
    try {
      await post(API.FEEDBACK.SUBMIT, {
        type: type === 'useful' ? 'ANSWER_USEFUL' : 'ANSWER_USELESS',
        targetId: answer.answerId,
        content: type === 'useful' ? '答案有用' : '答案无用'
      });
      
      wx.showToast({ 
        title: type === 'useful' ? '感谢您的认可！' : '我们会改进答案',
        icon: 'none'
      });
      
      this.setData({ showFeedback: false });
      
    } catch (err) {
      console.error('反馈失败', err);
    }
  },

  // 查看相关问题详情
  onTapRelated(e) {
    const question = e.currentTarget.dataset.item;
    this.setData({ question });
    this.handleAsk();
  },

  // 拨打电话
  onMakeCall(e) {
    const phone = e.currentTarget.dataset.phone;
    wx.makePhoneCall({ phoneNumber: phone });
  },

  // 生成会话ID
  generateSessionId() {
    return 'sess_' + Date.now();
  },

  // 获取上一个问题（上下文）
  getLastQuestion() {
    const { history } = this.data;
    for (let i = history.length - 1; i >= 0; i--) {
      if (history[i].type === 'ask') {
        return history[i].content;
      }
    }
    return null;
  },

  // 保存本地历史
  saveLocalHistory(question, answer) {
    let localHistory = wx.getStorageSync('qa_history') || [];
    localHistory.unshift({
      question,
      answer: answer.answer,
      answerId: answer.answerId,
      time: new Date().toISOString()
    });
    // 只保留最近20条
    localHistory = localHistory.slice(0, 20);
    wx.setStorageSync('qa_history', localHistory);
  },

  // 页面分享
  onShareAppMessage() {
    const { answer } = this.data;
    return {
      title: answer ? `校园问答：${answer.question}` : '校园智能问答助手',
      path: answer ? `/pages/ask/ask?question=${encodeURIComponent(answer.question)}` : '/pages/index/index'
    };
  }
});
```

**pages/ask/ask.wxml**（界面模板）：
```xml
<!-- 搜索框 -->
<view class="search-box">
  <input 
    class="search-input" 
    placeholder="请输入您的问题，如：图书馆几点开门？"
    value="{{question}}"
    bindinput="onInputChange"
    confirm-type="search"
    bindconfirm="handleAsk"
  />
  <button class="search-btn" bindtap="handleAsk" disabled="{{loading}}">
    {{loading ? '搜索中...' : '提问'}}
  </button>
</view>

<!-- 推荐问题 -->
<view class="suggestions" wx:if="{{suggestions.length > 0 && !answer}}">
  <view class="suggestion-title">推荐问题：</view>
  <view 
    class="suggestion-item" 
    wx:for="{{suggestions}}" 
    wx:key="index"
    data-item="{{item}}"
    bindtap="onTapSuggestion"
  >
    {{item}}
  </view>
</view>

<!-- 对话历史 -->
<scroll-view class="chat-container" scroll-y scroll-into-view="msg-{{history.length-1}}">
  <view 
    wx:for="{{history}}" 
    wx:key="index"
    id="msg-{{index}}"
    class="msg {{item.type}}"
  >
    <!-- 用户提问 -->
    <view wx:if="{{item.type === 'ask'}}" class="msg-ask">
      <view class="bubble ask-bubble">
        <text>{{item.content}}</text>
        <view class="time">{{item.time}}</view>
      </view>
      <image class="avatar" src="/assets/avatar-user.png" />
    </view>
    
    <!-- 系统回答 -->
    <view wx:if="{{item.type === 'answer'}}" class="msg-answer">
      <image class="avatar" src="/assets/avatar-bot.png" />
      <view class="bubble answer-bubble">
        <!-- 正常答案 -->
        <block wx:if="{{!item.isFallback}}">
          <view class="answer-source" wx:if="{{item.department}}">
            来源：{{item.department}}
          </view>
          <rich-text nodes="{{item.content}}"></rich-text>
          <view class="confidence" wx:if="{{item.confidence}}">
            匹配度：{{(item.confidence * 100).toFixed(0)}}%
          </view>
        </block>
        
        <!-- 兜底答案 -->
        <block wx:else>
          <text>{{item.content}}</text>
          <view class="contact-info" wx:if="{{item.contact}}">
            <text>请联系：{{item.contact.department}}</text>
            <text>电话：{{item.contact.phone}}</text>
            <text>时间：{{item.contact.onlineTime}}</text>
            <button size="mini" bindtap="onMakeCall" data-phone="{{item.contact.phone}}">拨打</button>
          </view>
        </block>
        
        <view class="time">{{item.time}}</view>
      </view>
    </view>
  </view>
</scroll-view>

<!-- 反馈按钮 -->
<view class="feedback-bar" wx:if="{{showFeedback && answer}}">
  <text>这个答案对您有帮助吗？</text>
  <button size="mini" type="primary" data-type="useful" bindtap="submitFeedback">👍 有用</button>
  <button size="mini" type="default" data-type="useless" bindtap="submitFeedback">👎 无用</button>
</view>

<!-- 相关问题 -->
<view class="related-section" wx:if="{{relatedQuestions.length > 0}}">
  <view class="section-title">相关问题</view>
  <view 
    class="related-item" 
    wx:for="{{relatedQuestions}}" 
    wx:key="index"
    data-item="{{item}}"
    bindtap="onTapRelated"
  >
    {{item}}
  </view>
</view>
```

### 3.5 其他关键页面代码片段

**pages/index/index.js**（首页）：
```javascript
const API = require('../../config/api');
const { get } = require('../../utils/request');

Page({
  data: {
    categories: [],
    hotQuestions: [],
    notices: [],
    userInfo: null
  },

  async onLoad() {
    // 并行加载数据
    await Promise.all([
      this.loadCategories(),
      this.loadHotQuestions(),
      this.loadNotices()
    ]);
  },

  // 加载分类
  async loadCategories() {
    try {
      const list = await get(API.CATEGORY.LIST);
      this.setData({ categories: list });
    } catch (err) {
      console.error('加载分类失败', err);
    }
  },

  // 加载热门问题
  async loadHotQuestions() {
    try {
      const list = await get(API.QA.HOT, { limit: 5 });
      this.setData({ hotQuestions: list });
    } catch (err) {
      console.error('加载热门问题失败', err);
    }
  },

  // 加载通知
  async loadNotices() {
    try {
      const list = await get(API.NOTICE.LIST, { limit: 3 });
      this.setData({ notices: list });
    } catch (err) {
      console.error('加载通知失败', err);
    }
  },

  // 跳转到问答页
  goToAsk(e) {
    const question = e.currentTarget.dataset.question || '';
    wx.navigateTo({
      url: `/pages/ask/ask?question=${encodeURIComponent(question)}`
    });
  },

  // 分类点击
  onCategoryTap(e) {
    const { id, name } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/category/category?id=${id}&name=${encodeURIComponent(name)}`
    });
  }
});
```

---

## 四、JeecgBoot后端微信登录扩展

由于JeecgBoot默认没有微信小程序登录，需要添加：

**WechatLoginController.java**：
```java
package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Api(tags = "微信登录")
@RestController
@RequestMapping("/sys")
public class WechatLoginController {
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // 微信小程序配置（需替换为实际值）
    private static final String APP_ID = "wx_your_app_id";
    private static final String APP_SECRET = "your_app_secret";
    
    @ApiOperation("微信小程序登录")
    @PostMapping("/wechatLogin")
    public Result<Map<String, Object>> wechatLogin(
            @RequestParam String code,
            @RequestParam String nickName,
            @RequestParam String avatarUrl) {
        
        try {
            // 1. 调用微信接口获取openid
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                APP_ID, APP_SECRET, code
            );
            
            Map<String, Object> wxRes = restTemplate.getForObject(url, Map.class);
            
            if (wxRes == null || wxRes.get("openid") == null) {
                return Result.error("微信登录失败：" + wxRes.get("errmsg"));
            }
            
            String openid = (String) wxRes.get("openid");
            String unionid = (String) wxRes.get("unionid");
            
            // 2. 查找或创建用户
            SysUser user = userService.getUserByOpenid(openid);
            
            if (user == null) {
                // 新用户，创建
                user = new SysUser();
                user.setId(UUID.randomUUID().toString());
                user.setUsername("wx_" + openid.substring(0, 8));
                user.setRealname(nickName);
                user.setAvatar(avatarUrl);
                user.setOpenid(openid);
                user.setUnionid(unionid);
                user.setStatus(1);
                user.setDelFlag(0);
                // 设置默认角色为学生
                userService.save(user);
            }
            
            // 3. 生成JeecgBoot token
            String token = JwtUtil.sign(user.getUsername(), user.getPassword());
            redisUtil.set("prefix_user_token_" + token, token);
            redisUtil.expire("prefix_user_token_" + token, JwtUtil.EXPIRE_TIME / 1000);
            
            // 4. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", convertToUserInfo(user));
            result.put("isNewUser", user.getCreateTime() == null);
            
            return Result.ok(result);
            
        } catch (Exception e) {
            log.error("微信登录异常", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }
    
    private Map<String, Object> convertToUserInfo(SysUser user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("realName", user.getRealname());
        info.put("avatar", user.getAvatar());
        info.put("role", "STUDENT"); // 默认学生角色
        return info;
    }
}
```

---

## 五、开发时间预估

| 阶段           | 工作内容                    | 时间       | 依赖           |
| :------------- | :-------------------------- | :--------- | :------------- |
| **环境搭建**   | JeecgBoot部署、数据库初始化 | 1天        | -              |
| **代码生成**   | 使用Online开发生成基础CRUD  | 1天        | 数据库设计完成 |
| **核心开发**   | 智能问答Service、Jieba集成  | 2-3天      | 代码生成完成   |
| **小程序开发** | 页面开发、API对接           | 3-4天      | 后端API完成    |
| **联调测试**   | 端到端测试、Bug修复         | 2天        | 前后端完成     |
| **总计**       |                             | **9-11天** |                |

---

## 六、关键检查点

| 检查项    | 验证方式                   | 通过标准                 |
| :-------- | :------------------------- | :----------------------- |
| Jieba分词 | 调用/qa/knowledge/segment  | 返回正确分词列表         |
| 智能问答  | 小程序提问"图书馆几点开门" | 返回答案+置信度+相关问题 |
| 微信登录  | 小程序授权登录             | 返回token+用户信息       |
| 权限控制  | 信息员登录后台             | 只能看到本部门问答/通知  |
| 数据权限  | 学生A查询历史              | 只能看到自己的历史记录   |

需要我提供**部署脚本**（Docker一键部署）或**答辩PPT技术架构图**的绘制指导吗？