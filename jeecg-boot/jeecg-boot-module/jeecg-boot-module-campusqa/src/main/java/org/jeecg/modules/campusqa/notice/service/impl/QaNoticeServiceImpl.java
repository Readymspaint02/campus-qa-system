package org.jeecg.modules.campusqa.notice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.campusqa.notice.entity.QaNotice;
import org.jeecg.modules.campusqa.notice.mapper.QaNoticeMapper;
import org.jeecg.modules.campusqa.notice.service.IQaNoticeService;
import org.springframework.stereotype.Service;

@Service
public class QaNoticeServiceImpl extends ServiceImpl<QaNoticeMapper, QaNotice> implements IQaNoticeService {}
