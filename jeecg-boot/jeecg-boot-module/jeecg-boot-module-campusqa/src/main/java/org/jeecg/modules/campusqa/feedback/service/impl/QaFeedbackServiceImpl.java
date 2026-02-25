package org.jeecg.modules.campusqa.feedback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.campusqa.feedback.entity.QaFeedback;
import org.jeecg.modules.campusqa.feedback.mapper.QaFeedbackMapper;
import org.jeecg.modules.campusqa.feedback.service.IQaFeedbackService;
import org.springframework.stereotype.Service;

@Service
public class QaFeedbackServiceImpl extends ServiceImpl<QaFeedbackMapper, QaFeedback> implements IQaFeedbackService {}
