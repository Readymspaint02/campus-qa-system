package org.jeecg.modules.campusqa.stats.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.campusqa.feedback.entity.QaFeedback;
import org.jeecg.modules.campusqa.feedback.service.IQaFeedbackService;
import org.jeecg.modules.campusqa.history.entity.QaHistory;
import org.jeecg.modules.campusqa.history.service.IQaHistoryService;
import org.jeecg.modules.campusqa.knowledge.entity.QaKnowledge;
import org.jeecg.modules.campusqa.knowledge.service.IQaKnowledgeService;
import org.jeecg.modules.campusqa.notice.entity.QaNotice;
import org.jeecg.modules.campusqa.notice.service.IQaNoticeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/campusqa/stats")
public class QaStatsController {

    private final IQaKnowledgeService knowledgeService;
    private final IQaNoticeService noticeService;
    private final IQaFeedbackService feedbackService;
    private final IQaHistoryService historyService;

    public QaStatsController(IQaKnowledgeService knowledgeService,
                             IQaNoticeService noticeService,
                             IQaFeedbackService feedbackService,
                             IQaHistoryService historyService) {
        this.knowledgeService = knowledgeService;
        this.noticeService = noticeService;
        this.feedbackService = feedbackService;
        this.historyService = historyService;
    }

    @GetMapping("/overview")
    @RequiresPermissions("campusqa:stats:view")
    public Result<Map<String, Object>> overview() {
        long knowledgeTotal = knowledgeService.count(activeKnowledgeWrapper());
        long knowledgeEnabled = knowledgeService.count(activeKnowledgeWrapper().eq("status", "enable"));

        long noticeTotal = noticeService.count(activeNoticeWrapper());
        long noticePublished = noticeService.count(activeNoticeWrapper().eq("status", "published"));

        long feedbackTotal = feedbackService.count(activeFeedbackWrapper());
        long feedbackHandled = feedbackService.count(activeFeedbackWrapper().eq("handled", 1));
        double feedbackHandledRate = feedbackTotal == 0 ? 0.0 : round((double) feedbackHandled / feedbackTotal);

        long historyTotal = historyService.count(activeHistoryWrapper());
        Date todayStart = getDayStart(new Date());
        long historyToday = historyService.count(activeHistoryWrapper().ge("create_time", todayStart));
        long askTotal = historyService.count(activeHistoryWrapper().likeRight("source", "ask:"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("knowledgeTotal", knowledgeTotal);
        result.put("knowledgeEnabled", knowledgeEnabled);
        result.put("noticeTotal", noticeTotal);
        result.put("noticePublished", noticePublished);
        result.put("feedbackTotal", feedbackTotal);
        result.put("feedbackHandled", feedbackHandled);
        result.put("feedbackHandledRate", feedbackHandledRate);
        result.put("historyTotal", historyTotal);
        result.put("historyToday", historyToday);
        result.put("askTotal", askTotal);
        return Result.OK(result);
    }

    @GetMapping("/topQuestions")
    @RequiresPermissions("campusqa:stats:view")
    public Result<List<Map<String, Object>>> topQuestions(@RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        int size = Math.min(Math.max(limit, 1), 50);
        QueryWrapper<QaKnowledge> qw = activeKnowledgeWrapper();
        qw.orderByDesc("hits", "hot_flag", "update_time");
        Page<QaKnowledge> page = knowledgeService.page(new Page<>(1, size), qw);

        List<Map<String, Object>> list = page.getRecords().stream().map(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.getId());
            row.put("question", item.getQuestion());
            row.put("hits", item.getHits() == null ? 0 : item.getHits());
            row.put("status", item.getStatus());
            row.put("hotFlag", item.getHotFlag() == null ? 0 : item.getHotFlag());
            row.put("updateTime", item.getUpdateTime());
            return row;
        }).collect(Collectors.toList());

        return Result.OK(list);
    }

    @GetMapping("/intentStats")
    @RequiresPermissions("campusqa:stats:view")
    public Result<List<Map<String, Object>>> intentStats(@RequestParam(name = "days", defaultValue = "30") Integer days) {
        int periodDays = Math.min(Math.max(days, 1), 365);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -periodDays);
        Date startTime = calendar.getTime();

        QueryWrapper<QaHistory> qw = activeHistoryWrapper();
        qw.likeRight("source", "ask:")
                .ge("create_time", startTime)
                .orderByDesc("create_time");
        List<QaHistory> historyList = historyService.list(qw);

        Map<String, Integer> counter = new HashMap<>();
        for (QaHistory item : historyList) {
            String source = item.getSource();
            String intentType = "GENERAL";
            if (source != null && source.startsWith("ask:") && source.length() > 4) {
                intentType = source.substring(4);
            }
            counter.put(intentType, counter.getOrDefault(intentType, 0) + 1);
        }

        Map<String, String> labels = intentLabels();
        List<Map<String, Object>> result = counter.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("intentType", entry.getKey());
                    row.put("intentLabel", labels.getOrDefault(entry.getKey(), entry.getKey()));
                    row.put("count", entry.getValue());
                    return row;
                })
                .collect(Collectors.toList());
        return Result.OK(result);
    }

    private Map<String, String> intentLabels() {
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put("QUERY_ACADEMIC", "教务咨询");
        labels.put("QUERY_STUDENT", "学工咨询");
        labels.put("QUERY_LOGISTICS", "后勤咨询");
        labels.put("PROCESS", "办事流程");
        labels.put("QUERY_TIME", "时间查询");
        labels.put("QUERY_LOCATION", "地点查询");
        labels.put("GENERAL", "综合咨询");
        return labels;
    }

    private QueryWrapper<QaKnowledge> activeKnowledgeWrapper() {
        QueryWrapper<QaKnowledge> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0");
        return wrapper;
    }

    private QueryWrapper<QaNotice> activeNoticeWrapper() {
        QueryWrapper<QaNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0");
        return wrapper;
    }

    private QueryWrapper<QaFeedback> activeFeedbackWrapper() {
        QueryWrapper<QaFeedback> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0");
        return wrapper;
    }

    private QueryWrapper<QaHistory> activeHistoryWrapper() {
        QueryWrapper<QaHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0");
        return wrapper;
    }

    private Date getDayStart(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
