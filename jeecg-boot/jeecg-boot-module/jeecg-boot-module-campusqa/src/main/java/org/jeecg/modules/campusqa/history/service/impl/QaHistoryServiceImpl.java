package org.jeecg.modules.campusqa.history.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.campusqa.history.entity.QaHistory;
import org.jeecg.modules.campusqa.history.mapper.QaHistoryMapper;
import org.jeecg.modules.campusqa.history.service.IQaHistoryService;
import org.springframework.stereotype.Service;

@Service
public class QaHistoryServiceImpl extends ServiceImpl<QaHistoryMapper, QaHistory> implements IQaHistoryService {}
