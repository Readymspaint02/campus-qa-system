package org.jeecg.modules.campusqa.category.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.campusqa.category.entity.QaCategory;
import org.jeecg.modules.campusqa.category.service.IQaCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/campusqa/category")
public class QaCategoryController extends JeecgController<QaCategory, IQaCategoryService> {

    @GetMapping("/list")
    public Result<IPage<QaCategory>> list(QaCategory category,
                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
        String rawKeyword = req.getParameter("name");
        String keyword = oConvertUtils.isNotEmpty(rawKeyword) ? rawKeyword.trim() : null;
        QueryWrapper<QaCategory> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        if (oConvertUtils.isNotEmpty(category.getType())) {
            qw.eq("type", category.getType());
        }
        if (oConvertUtils.isNotEmpty(category.getStatus())) {
            qw.eq("status", category.getStatus());
        }
        if (oConvertUtils.isNotEmpty(category.getParentId())) {
            qw.eq("parent_id", category.getParentId());
        }
        if (oConvertUtils.isNotEmpty(keyword)) {
            qw.and(w -> w.like("name", keyword).or().like("remark", keyword));
        }
        qw.orderByAsc("sort").orderByDesc("update_time");
        Page<QaCategory> page = new Page<>(pageNo, pageSize);
        return Result.OK(service.page(page, qw));
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    @RequiresPermissions("campusqa:category:edit")
    public Result<String> edit(@RequestBody QaCategory category) {
        service.saveOrUpdate(category);
        return Result.OK("OK");
    }

    @DeleteMapping("/delete")
    @RequiresPermissions("campusqa:category:delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        service.removeById(id);
        return Result.OK("OK");
    }

    @GetMapping("/queryById")
    public Result<QaCategory> queryById(@RequestParam(name = "id") String id) {
        QaCategory item = service.getById(id);
        return item == null ? Result.error("Not Found") : Result.OK(item);
    }
}
