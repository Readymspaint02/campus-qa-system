package org.jeecg.modules.campusqa.feedback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.campusqa.feedback.entity.QaFeedback;
import org.jeecg.modules.campusqa.feedback.service.IQaFeedbackService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/campusqa/feedback")
public class QaFeedbackController extends JeecgController<QaFeedback, IQaFeedbackService> {

    @GetMapping("/list")
    public Result<IPage<QaFeedback>> list(QaFeedback feedback,
                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
        String rawKeyword = req.getParameter("content");
        String keyword = oConvertUtils.isNotEmpty(rawKeyword) ? rawKeyword.trim() : null;
        QueryWrapper<QaFeedback> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        if (oConvertUtils.isNotEmpty(feedback.getUserId())) {
            qw.eq("user_id", feedback.getUserId());
        }
        if (oConvertUtils.isNotEmpty(feedback.getRating())) {
            qw.eq("rating", feedback.getRating());
        }
        if (feedback.getHandled() != null) {
            qw.eq("handled", feedback.getHandled());
        }
        if (oConvertUtils.isNotEmpty(keyword)) {
            qw.and(w -> w.like("content", keyword).or().like("reply", keyword));
        }
        qw.orderByDesc("update_time");
        Page<QaFeedback> page = new Page<>(pageNo, pageSize);
        return Result.OK(service.page(page, qw));
    }
//将我的codex里面编译过的target代码删除了，只留下源代码，
    @PostMapping("/reply")
    @RequiresPermissions("campusqa:feedback:reply")
    public Result<String> reply(@RequestParam(name = "id") String id, @RequestParam(name = "reply") String reply) {
        QaFeedback feedback = new QaFeedback();
        feedback.setId(id);
        feedback.setReply(reply);
        feedback.setHandled(1);
        service.updateById(feedback);
        return Result.OK("OK");
    }

    /**
     * Compatible with frontend CRUD pattern (/edit).
     * This endpoint is used by the "回复反馈" modal in vue3 campusqa page.
     */
    @PostMapping("/edit")
    @RequiresPermissions("campusqa:feedback:reply")
    public Result<String> edit(@RequestBody QaFeedback request) {
        if (request == null || oConvertUtils.isEmpty(request.getId())) {
            return Result.error("id required");
        }
        QaFeedback feedback = new QaFeedback();
        feedback.setId(request.getId());
        if (oConvertUtils.isNotEmpty(request.getReply())) {
            feedback.setReply(request.getReply().trim());
            if (request.getHandled() == null) {
                feedback.setHandled(1);
            }
        }
        if (request.getHandled() != null) {
            feedback.setHandled(request.getHandled());
        }
        service.updateById(feedback);
        return Result.OK("OK");
    }

    @DeleteMapping("/delete")
    @RequiresPermissions("campusqa:feedback:delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        service.removeById(id);
        return Result.OK("OK");
    }

    @GetMapping("/queryById")
    public Result<QaFeedback> queryById(@RequestParam(name = "id") String id) {
        QaFeedback item = service.getById(id);
        return item == null ? Result.error("Not Found") : Result.OK(item);
    }
}
