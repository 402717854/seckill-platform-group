package com.seckill.platform.goods.application.service;

import com.seckill.platform.goods.domain.service.GoodsService;
import com.seckill.platform.goods.dto.EsProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wangys10
 * @date 2022/9/10 10:58
 */
@Component
public class GoodsApplicationService {

    @Autowired
    private GoodsService goodsService;

    public List<EsProductVO> getAllEsProductList() {
        return goodsService.getAllEsProductList(null);
    }
    public EsProductVO getEsProductById(Long id) {
        List<EsProductVO> allEsProductList = goodsService.getAllEsProductList(id);
        if(CollectionUtils.isEmpty(allEsProductList)){
            return null;
        }
        return allEsProductList.get(0);
    }
}
