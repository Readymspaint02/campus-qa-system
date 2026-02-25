package org.jeecg.modules.campusqa.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.campusqa.guide.entity.QaGuide;
import org.jeecg.modules.campusqa.guide.service.IQaGuideService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/campusqa/guide")
public class QaGuideController extends JeecgController<QaGuide, IQaGuideService> {

    @GetMapping("/list")
    public Result<IPage<QaGuide>> list(QaGuide guide,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      HttpServletRequest req) {
        String rawKeyword = req.getParameter("title");
        String keyword = oConvertUtils.isNotEmpty(rawKeyword) ? rawKeyword.trim() : null;
        QueryWrapper<QaGuide> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        if (oConvertUtils.isNotEmpty(guide.getCategoryId())) {
            qw.eq("category_id", guide.getCategoryId());
        }
        if (oConvertUtils.isNotEmpty(guide.getStatus())) {
            qw.eq("status", guide.getStatus());
        }
        if (oConvertUtils.isNotEmpty(keyword)) {
            qw.and(w -> w.like("title", keyword)
                    .or()
                    .like("content", keyword)
                    .or()
                    .like("steps", keyword));
        }
        qw.orderByDesc("update_time");
        Page<QaGuide> page = new Page<>(pageNo, pageSize);
        return Result.OK(service.page(page, qw));
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    @RequiresPermissions("campusqa:guide:edit")
    public Result<String> edit(@RequestBody QaGuide guide) {
        service.saveOrUpdate(guide);
        return Result.OK("OK");
    }

    @DeleteMapping("/delete")
    @RequiresPermissions("campusqa:guide:delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        service.removeById(id);
        return Result.OK("OK");
    }

    @GetMapping("/queryById")
    public Result<QaGuide> queryById(@RequestParam(name = "id") String id) {
        QaGuide item = service.getById(id);
        return item == null ? Result.error("Not Found") : Result.OK(item);
    }
}
