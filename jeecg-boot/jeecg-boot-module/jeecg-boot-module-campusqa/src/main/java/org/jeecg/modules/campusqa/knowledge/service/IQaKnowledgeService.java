package org.jeecg.modules.campusqa.knowledge.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.campusqa.knowledge.entity.QaKnowledge;

public interface IQaKnowledgeService extends IService<QaKnowledge> {
    IPage<QaKnowledge> search(String keyword, String categoryId, int pageNo, int pageSize);
    QaKnowledge getAndIncreaseHits(String id);
}
