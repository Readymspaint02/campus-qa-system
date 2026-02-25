package org.jeecg.modules.campusqa.subscribe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.campusqa.subscribe.entity.QaSubscribe;
import org.jeecg.modules.campusqa.subscribe.mapper.QaSubscribeMapper;
import org.jeecg.modules.campusqa.subscribe.service.IQaSubscribeService;
import org.springframework.stereotype.Service;

@Service
public class QaSubscribeServiceImpl extends ServiceImpl<QaSubscribeMapper, QaSubscribe> implements IQaSubscribeService {}
