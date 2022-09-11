package com.seckill.platform.goods.domain.service.impl;

import com.seckill.platform.goods.domain.dao.EsProductDao;
import com.seckill.platform.goods.domain.service.GoodsService;
import com.seckill.platform.goods.dto.EsProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangys10
 * @date 2022/9/10 10:56
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private EsProductDao esProductDao;
    @Override
    public List<EsProductVO> getAllEsProductList(Long id) {
        return esProductDao.getAllEsProductList(id);
    }
}
