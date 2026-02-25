package org.jeecg.modules.campusqa.notice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.campusqa.notice.entity.QaNotice;
import org.jeecg.modules.campusqa.notice.service.IQaNoticeService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/campusqa/notice")
public class QaNoticeController extends JeecgController<QaNotice, IQaNoticeService> {

    @GetMapping("/list")
    public Result<IPage<QaNotice>> list(QaNotice notice,
                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                       HttpServletRequest req) {
        String rawKeyword = req.getParameter("title");
        String keyword = oConvertUtils.isNotEmpty(rawKeyword) ? rawKeyword.trim() : null;
        QueryWrapper<QaNotice> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        if (oConvertUtils.isNotEmpty(notice.getStatus())) {
            qw.eq("status", notice.getStatus());
        }
        if (oConvertUtils.isNotEmpty(notice.getLevel())) {
            qw.eq("level", notice.getLevel());
        }
        if (oConvertUtils.isNotEmpty(keyword)) {
            qw.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        qw.orderByDesc("publish_time", "update_time");
        Page<QaNotice> page = new Page<>(pageNo, pageSize);
        return Result.OK(service.page(page, qw));
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    @RequiresPermissions("campusqa:notice:edit")
    public Result<String> edit(@RequestBody QaNotice notice) {
        service.saveOrUpdate(notice);
        return Result.OK("OK");
    }

    @DeleteMapping("/delete")
    @RequiresPermissions("campusqa:notice:delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        service.removeById(id);
        return Result.OK("OK");
    }

    @GetMapping("/queryById")
    public Result<QaNotice> queryById(@RequestParam(name = "id") String id) {
        QaNotice item = service.getById(id);
        return item == null ? Result.error("Not Found") : Result.OK(item);
    }
}
