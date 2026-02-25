package org.jeecg.modules.campusqa.rag.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.campusqa.knowledge.entity.QaKnowledge;
import org.jeecg.modules.campusqa.knowledge.service.IQaKnowledgeService;
import org.jeecg.modules.campusqa.rag.config.CampusQaRagProperties;
import org.jeecg.modules.campusqa.rag.dto.RagAskRequest;
import org.jeecg.modules.campusqa.rag.dto.RagAnswerResponse;
import org.jeecg.modules.campusqa.rag.service.IRagService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagServiceImpl implements IRagService {

    private final IQaKnowledgeService knowledgeService;
    private final CampusQaRagProperties ragProperties;

    public RagServiceImpl(IQaKnowledgeService knowledgeService, CampusQaRagProperties ragProperties) {
        this.knowledgeService = knowledgeService;
        this.ragProperties = ragProperties;
    }

    @Override
    public RagAnswerResponse ask(RagAskRequest request) {
        RagAnswerResponse resp = new RagAnswerResponse();
        if (request == null || oConvertUtils.isEmpty(request.getQuestion())) {
            resp.setAnswer("问题不能为空");
            resp.setMode("error");
            resp.setConfidence(0.0);
            resp.setIntentType("GENERAL");
            resp.setIntentLabel("综合咨询");
            resp.setIntentScore(0.0);
            resp.setMatchedKeywords("");
            return resp;
        }

        IntentResult intent = recognizeIntent(request.getQuestion());
        resp.setIntentType(intent.getIntentType());
        resp.setIntentLabel(intent.getIntentLabel());
        resp.setIntentScore(intent.getScore());
        resp.setMatchedKeywords(String.join(", ", intent.getMatchedKeywords()));

        int topK = request.getTopK() != null ? request.getTopK() : ragProperties.getTopK();
        IPage<QaKnowledge> page = knowledgeService.search(request.getQuestion(), null, 1, Math.max(topK, 1));
        if (page.getRecords().isEmpty()) {
            resp.setAnswer("未命中知识库，建议尝试更具体关键词或查看相关分类");
            resp.setMode(ragProperties.isEnabled() ? ragProperties.getMode() : "keyword+intent");
            resp.setConfidence(round(Math.max(0.05, intent.getScore() * 0.2)));
            return resp;
        }

        QaKnowledge top = page.getRecords().get(0);
        resp.setAnswer(top.getAnswer());
        resp.setSourceId(top.getId());
        resp.setSourceQuestion(top.getQuestion());
        resp.setMode(ragProperties.isEnabled() ? ragProperties.getMode() : "keyword+intent");
        resp.setConfidence(buildConfidence(request.getQuestion(), top, intent));
        return resp;
    }

    private double buildConfidence(String question, QaKnowledge knowledge, IntentResult intent) {
        double lexicalScore = calcLexicalScore(question, knowledge, intent.getMatchedKeywords());
        double hotScore = (knowledge.getHotFlag() != null && knowledge.getHotFlag() == 1) ? 0.05 : 0.0;
        double hitsScore = Math.min(0.10, Math.max(0, safeInt(knowledge.getHits())) / 200.0);
        double confidence = 0.35 + lexicalScore * 0.35 + intent.getScore() * 0.20 + hotScore + hitsScore;
        return round(Math.min(0.95, confidence));
    }

    private double calcLexicalScore(String question, QaKnowledge knowledge, List<String> matchedIntentKeywords) {
        String combined = normalize(knowledge.getQuestion()) + " " + normalize(knowledge.getKeywords()) + " " + normalize(knowledge.getTags());
        int total = 1;
        int matched = combined.contains(normalize(question)) ? 1 : 0;

        for (String keyword : matchedIntentKeywords) {
            total++;
            if (combined.contains(normalize(keyword))) {
                matched++;
            }
        }
        return Math.min(1.0, Math.max(0.0, (double) matched / total));
    }

    private IntentResult recognizeIntent(String question) {
        String normalizedQuestion = normalize(question);
        IntentResult best = new IntentResult("GENERAL", "综合咨询", 0.2, Collections.emptyList());

        for (IntentRule rule : intentRules()) {
            List<String> matched = rule.getKeywords().stream()
                    .filter(normalizedQuestion::contains)
                    .collect(Collectors.toList());
            if (matched.isEmpty()) {
                continue;
            }
            double score = round((double) matched.size() / rule.getKeywords().size());
            if (score > best.getScore()) {
                best = new IntentResult(rule.getIntentType(), rule.getIntentLabel(), score, matched);
            }
        }
        return best;
    }

    private List<IntentRule> intentRules() {
        return Arrays.asList(
                new IntentRule("QUERY_ACADEMIC", "教务咨询", Arrays.asList("课表", "选课", "考试", "成绩", "学分", "补考", "重修")),
                new IntentRule("QUERY_STUDENT", "学工咨询", Arrays.asList("奖学金", "助学金", "辅导员", "学生证", "请假", "评优", "资助")),
                new IntentRule("QUERY_LOGISTICS", "后勤咨询", Arrays.asList("食堂", "宿舍", "水电", "报修", "图书馆", "快递", "校车")),
                new IntentRule("PROCESS", "办事流程", Arrays.asList("流程", "步骤", "怎么办", "如何申请", "材料", "办理")),
                new IntentRule("QUERY_TIME", "时间查询", Arrays.asList("几点", "时间", "何时", "截止", "开始", "结束")),
                new IntentRule("QUERY_LOCATION", "地点查询", Arrays.asList("在哪", "哪里", "地点", "地址", "办公室", "窗口"))
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class IntentRule {
        private final String intentType;
        private final String intentLabel;
        private final List<String> keywords;

        IntentRule(String intentType, String intentLabel, List<String> keywords) {
            this.intentType = intentType;
            this.intentLabel = intentLabel;
            this.keywords = keywords;
        }

        String getIntentType() {
            return intentType;
        }

        String getIntentLabel() {
            return intentLabel;
        }

        List<String> getKeywords() {
            return keywords;
        }
    }

    private static class IntentResult {
        private final String intentType;
        private final String intentLabel;
        private final double score;
        private final List<String> matchedKeywords;

        IntentResult(String intentType, String intentLabel, double score, List<String> matchedKeywords) {
            this.intentType = intentType;
            this.intentLabel = intentLabel;
            this.score = score;
            this.matchedKeywords = matchedKeywords;
        }

        String getIntentType() {
            return intentType;
        }

        String getIntentLabel() {
            return intentLabel;
        }

        double getScore() {
            return score;
        }

        List<String> getMatchedKeywords() {
            return matchedKeywords;
        }
    }
}
