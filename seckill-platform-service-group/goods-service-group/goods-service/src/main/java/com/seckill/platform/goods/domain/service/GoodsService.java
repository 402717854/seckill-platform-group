package com.seckill.platform.goods.domain.service;

import com.seckill.platform.goods.dto.EsProductVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lenovo
 */
public interface GoodsService {
    /**
     * 获取指定ID的搜索商品
     * @param id
     * @return
     */
    List<EsProductVO> getAllEsProductList(Long id);
}
