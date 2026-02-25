package org.jeecg.modules.campusqa.favorite.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.campusqa.favorite.entity.QaFavorite;
import org.jeecg.modules.campusqa.favorite.mapper.QaFavoriteMapper;
import org.jeecg.modules.campusqa.favorite.service.IQaFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class QaFavoriteServiceImpl extends ServiceImpl<QaFavoriteMapper, QaFavorite> implements IQaFavoriteService {}
