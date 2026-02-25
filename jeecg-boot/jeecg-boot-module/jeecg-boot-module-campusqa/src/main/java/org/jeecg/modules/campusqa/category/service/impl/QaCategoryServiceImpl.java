package org.jeecg.modules.campusqa.category.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.campusqa.category.entity.QaCategory;
import org.jeecg.modules.campusqa.category.mapper.QaCategoryMapper;
import org.jeecg.modules.campusqa.category.service.IQaCategoryService;
import org.springframework.stereotype.Service;

@Service
public class QaCategoryServiceImpl extends ServiceImpl<QaCategoryMapper, QaCategory> implements IQaCategoryService {}
