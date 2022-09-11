package com.seckill.platform.goods.domain.dao;

import com.seckill.platform.goods.dto.EsProductVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索商品管理自定义Dao
 * Created by macro on 2018/6/19.
 */
public interface EsProductDao {
    /**
     * 获取指定ID的搜索商品
     */
    List<EsProductVO> getAllEsProductList(@Param("id") Long id);
}
