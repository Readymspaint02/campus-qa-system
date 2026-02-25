package org.jeecg.modules.campusqa.guide.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.campusqa.guide.entity.QaGuide;
import org.jeecg.modules.campusqa.guide.mapper.QaGuideMapper;
import org.jeecg.modules.campusqa.guide.service.IQaGuideService;
import org.springframework.stereotype.Service;

@Service
public class QaGuideServiceImpl extends ServiceImpl<QaGuideMapper, QaGuide> implements IQaGuideService {}
