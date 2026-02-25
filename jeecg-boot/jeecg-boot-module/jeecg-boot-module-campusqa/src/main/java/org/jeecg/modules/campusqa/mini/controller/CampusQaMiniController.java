package org.jeecg.modules.campusqa.mini.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.campusqa.favorite.entity.QaFavorite;
import org.jeecg.modules.campusqa.favorite.service.IQaFavoriteService;
import org.jeecg.modules.campusqa.feedback.entity.QaFeedback;
import org.jeecg.modules.campusqa.feedback.service.IQaFeedbackService;
import org.jeecg.modules.campusqa.guide.entity.QaGuide;
import org.jeecg.modules.campusqa.guide.service.IQaGuideService;
import org.jeecg.modules.campusqa.history.entity.QaHistory;
import org.jeecg.modules.campusqa.history.service.IQaHistoryService;
import org.jeecg.modules.campusqa.knowledge.entity.QaKnowledge;
import org.jeecg.modules.campusqa.knowledge.service.IQaKnowledgeService;
import org.jeecg.modules.campusqa.mini.dto.MiniFavoriteToggleRequest;
import org.jeecg.modules.campusqa.mini.dto.MiniFeedbackRequest;
import org.jeecg.modules.campusqa.mini.dto.MiniHistoryRequest;
import org.jeecg.modules.campusqa.mini.dto.MiniSubscribeToggleRequest;
import org.jeecg.modules.campusqa.notice.entity.QaNotice;
import org.jeecg.modules.campusqa.notice.service.IQaNoticeService;
import org.jeecg.modules.campusqa.rag.dto.RagAnswerResponse;
import org.jeecg.modules.campusqa.rag.dto.RagAskRequest;
import org.jeecg.modules.campusqa.rag.service.IRagService;
import org.jeecg.modules.campusqa.subscribe.entity.QaSubscribe;
import org.jeecg.modules.campusqa.subscribe.service.IQaSubscribeService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/campusqa/mini")
public class CampusQaMiniController {
    private final IQaKnowledgeService knowledgeService;
    private final IQaNoticeService noticeService;
    private final IQaGuideService guideService;
    private final IQaFeedbackService feedbackService;
    private final IQaHistoryService historyService;
    private final IQaFavoriteService favoriteService;
    private final IQaSubscribeService subscribeService;
    private final IRagService ragService;

    public CampusQaMiniController(IQaKnowledgeService knowledgeService,
                                  IQaNoticeService noticeService,
                                  IQaGuideService guideService,
                                  IQaFeedbackService feedbackService,
                                  IQaHistoryService historyService,
                                  IQaFavoriteService favoriteService,
                                  IQaSubscribeService subscribeService,
                                  IRagService ragService) {
        this.knowledgeService = knowledgeService;
        this.noticeService = noticeService;
        this.guideService = guideService;
        this.feedbackService = feedbackService;
        this.historyService = historyService;
        this.favoriteService = favoriteService;
        this.subscribeService = subscribeService;
        this.ragService = ragService;
    }

    @IgnoreAuth
    @GetMapping("/knowledge/search")
    public Result<IPage<QaKnowledge>> search(@RequestParam(name = "keyword", required = false) String keyword,
                                            @RequestParam(name = "categoryId", required = false) String categoryId,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);
        return Result.OK(knowledgeService.search(keyword, categoryId, safePageNo, safePageSize));
    }

    @IgnoreAuth
    @GetMapping("/knowledge/detail")
    public Result<QaKnowledge> detail(@RequestParam(name = "id") String id) {
        if (oConvertUtils.isEmpty(id)) {
            return Result.error("id required");
        }
        QaKnowledge item = knowledgeService.getAndIncreaseHits(id);
        return item == null ? Result.error("Not Found") : Result.OK(item);
    }

    @IgnoreAuth
    @GetMapping("/notice/list")
    public Result<IPage<QaNotice>> noticeList(@RequestParam(name = "deptCode", required = false) String deptCode,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);
        QueryWrapper<QaNotice> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        qw.eq("status", "published");
        qw.and(wrapper -> wrapper.isNull("expire_time").or().ge("expire_time", new Date()));
        if (oConvertUtils.isNotEmpty(deptCode)) {
            qw.eq("dept_code", deptCode);
        }
        qw.orderByDesc("publish_time");
        return Result.OK(noticeService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(safePageNo, safePageSize), qw));
    }

    @IgnoreAuth
    @GetMapping("/guide/list")
    public Result<IPage<QaGuide>> guideList(@RequestParam(name = "categoryId", required = false) String categoryId,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);
        QueryWrapper<QaGuide> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        qw.eq("status", "enable");
        if (oConvertUtils.isNotEmpty(categoryId)) {
            qw.eq("category_id", categoryId);
        }
        qw.orderByDesc("update_time");
        return Result.OK(guideService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(safePageNo, safePageSize), qw));
    }

    @IgnoreAuth
    @PostMapping("/feedback")
    public Result<String> feedback(@RequestBody MiniFeedbackRequest request) {
        if (request == null || oConvertUtils.isEmpty(request.getUserId()) || oConvertUtils.isEmpty(request.getKnowledgeId())) {
            return Result.error("userId and knowledgeId required");
        }
        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (oConvertUtils.isEmpty(content)) {
            return Result.error("feedback content required");
        }
        if (content.length() > 500) {
            return Result.error("feedback content too long");
        }
        QaFeedback feedback = new QaFeedback();
        feedback.setKnowledgeId(request.getKnowledgeId());
        feedback.setUserId(request.getUserId());
        feedback.setRating(oConvertUtils.isNotEmpty(request.getRating()) ? request.getRating() : "like");
        feedback.setContent(content);
        feedback.setHandled(0);
        feedbackService.save(feedback);
        return Result.OK("OK");
    }

    @IgnoreAuth
    @PostMapping("/history")
    public Result<String> history(@RequestBody MiniHistoryRequest request) {
        if (request == null || oConvertUtils.isEmpty(request.getUserId())) {
            return Result.error("userId required");
        }
        if (oConvertUtils.isEmpty(request.getQuery()) && oConvertUtils.isEmpty(request.getKnowledgeId())) {
            return Result.error("query or knowledgeId required");
        }
        QaHistory history = new QaHistory();
        history.setUserId(request.getUserId());
        history.setQuery(request.getQuery());
        history.setKnowledgeId(request.getKnowledgeId());
        history.setSource(request.getSource());
        historyService.save(history);
        return Result.OK("OK");
    }

    @IgnoreAuth
    @GetMapping("/history/list")
    public Result<IPage<QaHistory>> historyList(@RequestParam(name = "userId") String userId,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (oConvertUtils.isEmpty(userId)) {
            return Result.error("userId required");
        }
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);
        QueryWrapper<QaHistory> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0")
                .eq("user_id", userId)
                .orderByDesc("create_time");
        return Result.OK(historyService.page(new Page<>(safePageNo, safePageSize), qw));
    }

    @IgnoreAuth
    @PostMapping("/favorite/toggle")
    public Result<String> toggleFavorite(@RequestBody MiniFavoriteToggleRequest request) {
        if (oConvertUtils.isEmpty(request.getUserId()) || oConvertUtils.isEmpty(request.getKnowledgeId())) {
            return Result.error("userId and knowledgeId required");
        }
        QueryWrapper<QaFavorite> qw = new QueryWrapper<>();
        qw.eq("user_id", request.getUserId()).eq("knowledge_id", request.getKnowledgeId());
        QaFavorite exist = favoriteService.getOne(qw, false);
        if (exist != null) {
            favoriteService.removeById(exist.getId());
            return Result.OK("removed");
        }
        QaFavorite favorite = new QaFavorite();
        favorite.setUserId(request.getUserId());
        favorite.setKnowledgeId(request.getKnowledgeId());
        favoriteService.save(favorite);
        return Result.OK("added");
    }

    @IgnoreAuth
    @GetMapping("/favorite/list")
    public Result<IPage<QaKnowledge>> favoriteList(@RequestParam(name = "userId") String userId,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (oConvertUtils.isEmpty(userId)) {
            return Result.error("userId required");
        }
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);

        QueryWrapper<QaFavorite> favoriteQw = new QueryWrapper<>();
        favoriteQw.eq("del_flag", "0")
                .eq("user_id", userId)
                .orderByDesc("create_time");
        IPage<QaFavorite> favoritePage = favoriteService.page(new Page<>(safePageNo, safePageSize), favoriteQw);

        Page<QaKnowledge> resultPage = new Page<>(safePageNo, safePageSize);
        resultPage.setTotal(favoritePage.getTotal());
        if (favoritePage.getRecords().isEmpty()) {
            resultPage.setRecords(Collections.emptyList());
            return Result.OK(resultPage);
        }

        List<String> knowledgeIds = favoritePage.getRecords().stream()
                .map(QaFavorite::getKnowledgeId)
                .filter(oConvertUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (knowledgeIds.isEmpty()) {
            resultPage.setRecords(Collections.emptyList());
            return Result.OK(resultPage);
        }

        QueryWrapper<QaKnowledge> knowledgeQw = new QueryWrapper<>();
        knowledgeQw.eq("del_flag", "0").in("id", knowledgeIds);
        List<QaKnowledge> knowledgeList = knowledgeService.list(knowledgeQw);
        Map<String, QaKnowledge> knowledgeMap = knowledgeList.stream()
                .collect(Collectors.toMap(QaKnowledge::getId, k -> k, (a, b) -> a));

        List<QaKnowledge> orderedList = favoritePage.getRecords().stream()
                .map(item -> knowledgeMap.get(item.getKnowledgeId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        resultPage.setRecords(orderedList);
        return Result.OK(resultPage);
    }

    @IgnoreAuth
    @PostMapping("/subscribe/toggle")
    public Result<String> toggleSubscribe(@RequestBody MiniSubscribeToggleRequest request) {
        if (oConvertUtils.isEmpty(request.getUserId())) {
            return Result.error("userId required");
        }
        if (oConvertUtils.isEmpty(request.getDeptCode()) && oConvertUtils.isEmpty(request.getCategoryId())) {
            return Result.error("deptCode or categoryId required");
        }
        QueryWrapper<QaSubscribe> qw = new QueryWrapper<>();
        qw.eq("user_id", request.getUserId());
        if (oConvertUtils.isNotEmpty(request.getDeptCode())) {
            qw.eq("dept_code", request.getDeptCode());
        }
        if (oConvertUtils.isNotEmpty(request.getCategoryId())) {
            qw.eq("category_id", request.getCategoryId());
        }
        QaSubscribe exist = subscribeService.getOne(qw, false);
        if (exist != null) {
            subscribeService.removeById(exist.getId());
            return Result.OK("removed");
        }
        QaSubscribe subscribe = new QaSubscribe();
        subscribe.setUserId(request.getUserId());
        subscribe.setDeptCode(request.getDeptCode());
        subscribe.setCategoryId(request.getCategoryId());
        subscribeService.save(subscribe);
        return Result.OK("added");
    }

    @IgnoreAuth
    @GetMapping("/subscribe/list")
    public Result<IPage<QaSubscribe>> subscribeList(@RequestParam(name = "userId") String userId,
                                                    @RequestParam(name = "deptCode", required = false) String deptCode,
                                                    @RequestParam(name = "categoryId", required = false) String categoryId,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (oConvertUtils.isEmpty(userId)) {
            return Result.error("userId required");
        }
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);
        QueryWrapper<QaSubscribe> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0")
                .eq("user_id", userId)
                .orderByDesc("create_time");
        if (oConvertUtils.isNotEmpty(deptCode)) {
            qw.eq("dept_code", deptCode);
        }
        if (oConvertUtils.isNotEmpty(categoryId)) {
            qw.eq("category_id", categoryId);
        }
        return Result.OK(subscribeService.page(new Page<>(safePageNo, safePageSize), qw));
    }

    @IgnoreAuth
    @PostMapping("/ask")
    public Result<RagAnswerResponse> ask(@RequestBody RagAskRequest request) {
        if (request == null || oConvertUtils.isEmpty(request.getQuestion())) {
            return Result.error("question required");
        }
        RagAnswerResponse response = ragService.ask(request);
        if (request != null && oConvertUtils.isNotEmpty(request.getUserId()) && oConvertUtils.isNotEmpty(request.getQuestion())) {
            QaHistory history = new QaHistory();
            history.setUserId(request.getUserId());
            history.setQuery(request.getQuestion());
            history.setKnowledgeId(response.getSourceId());
            history.setSource("ask:" + (oConvertUtils.isNotEmpty(response.getIntentType()) ? response.getIntentType() : "GENERAL"));
            historyService.save(history);
        }
        return Result.OK(response);
    }

    private int normalizePageNo(Integer pageNo) {
        return (pageNo == null || pageNo < 1) ? 1 : pageNo;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 50);
    }
}
