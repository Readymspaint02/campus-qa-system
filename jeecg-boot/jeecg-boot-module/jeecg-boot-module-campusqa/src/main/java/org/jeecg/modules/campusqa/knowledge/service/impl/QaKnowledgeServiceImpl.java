package org.jeecg.modules.campusqa.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.campusqa.knowledge.entity.QaKnowledge;
import org.jeecg.modules.campusqa.knowledge.mapper.QaKnowledgeMapper;
import org.jeecg.modules.campusqa.knowledge.service.IQaKnowledgeService;
import org.springframework.stereotype.Service;

@Service
public class QaKnowledgeServiceImpl extends ServiceImpl<QaKnowledgeMapper, QaKnowledge> implements IQaKnowledgeService {

    @Override
    public IPage<QaKnowledge> search(String keyword, String categoryId, int pageNo, int pageSize) {
        QueryWrapper<QaKnowledge> qw = new QueryWrapper<>();
        qw.eq("del_flag", "0");
        qw.eq("status", "enable");
        if (oConvertUtils.isNotEmpty(categoryId)) {
            qw.eq("category_id", categoryId);
        }
        if (oConvertUtils.isNotEmpty(keyword)) {
            qw.and(w -> w.like("question", keyword)
                    .or()
                    .like("keywords", keyword)
                    .or()
                    .like("tags", keyword));
        }
        qw.orderByDesc("hot_flag", "hits", "update_time");
        Page<QaKnowledge> page = new Page<>(pageNo, pageSize);
        return this.page(page, qw);
    }

    @Override
    public QaKnowledge getAndIncreaseHits(String id) {
        QaKnowledge item = this.getById(id);
        if (item == null) {
            return null;
        }
        Integer hits = item.getHits();
        if (hits == null) {
            hits = 0;
        }
        item.setHits(hits + 1);
        this.updateById(item);
        return item;
    }
}
