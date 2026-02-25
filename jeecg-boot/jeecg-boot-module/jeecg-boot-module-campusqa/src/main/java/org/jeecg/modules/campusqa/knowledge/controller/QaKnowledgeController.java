package org.jeecg.modules.campusqa.knowledge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.campusqa.knowledge.entity.QaKnowledge;
import org.jeecg.modules.campusqa.knowledge.service.IQaKnowledgeService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/campusqa/knowledge")
public class QaKnowledgeController extends JeecgController<QaKnowledge, IQaKnowledgeService> {

    @GetMapping("/list")
    public Result<IPage<QaKnowledge>> list(QaKnowledge knowledge,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpServletRequest req) {
        String rawKeyword = req.getParameter("question");
        String keyword = oConvertUtils.isNotEmpty(rawKeyword) ? rawKeyword.trim() : null;
        QueryWrapper<QaKnowledge> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        if (oConvertUtils.isNotEmpty(knowledge.getCategoryId())) {
            qw.eq("category_id", knowledge.getCategoryId());
        }
        if (oConvertUtils.isNotEmpty(knowledge.getStatus())) {
            qw.eq("status", knowledge.getStatus());
        }
        if (oConvertUtils.isNotEmpty(keyword)) {
            qw.and(w -> w.like("question", keyword)
                    .or()
                    .like("keywords", keyword)
                    .or()
                    .like("tags", keyword));
        }
        qw.orderByDesc("update_time");
        Page<QaKnowledge> page = new Page<>(pageNo, pageSize);
        return Result.OK(service.page(page, qw));
    }

    @GetMapping("/search")
    public Result<IPage<QaKnowledge>> search(@RequestParam(name = "keyword", required = false) String keyword,
                                            @RequestParam(name = "categoryId", required = false) String categoryId,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.OK(service.search(keyword, categoryId, pageNo, pageSize));
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    @RequiresPermissions("campusqa:knowledge:edit")
    public Result<String> edit(@RequestBody QaKnowledge knowledge) {
        if (knowledge.getHits() == null) {
            knowledge.setHits(0);
        }
        service.saveOrUpdate(knowledge);
        return Result.OK("OK");
    }

    @DeleteMapping("/delete")
    @RequiresPermissions("campusqa:knowledge:delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        service.removeById(id);
        return Result.OK("OK");
    }

    @GetMapping("/queryById")
    public Result<QaKnowledge> queryById(@RequestParam(name = "id") String id) {
        QaKnowledge item = service.getById(id);
        return item == null ? Result.error("Not Found") : Result.OK(item);
    }
}
