package com.seckill.platform.goods.interfaces.facade;

import com.seckill.platform.goods.api.GoodsClientApi;
import com.seckill.platform.goods.application.service.GoodsApplicationService;
import com.seckill.platform.goods.dto.EsProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangys10
 * @date 2022/9/10 10:31
 */
@RestController
public class GoodsController implements GoodsClientApi {

    @Autowired
    private GoodsApplicationService goodsApplicationService;


    @Override
    public List<EsProductVO> getAllEsProductList() {
        return goodsApplicationService.getAllEsProductList();
    }

    @Override
    public EsProductVO getEsProductById(Long id) {
        return goodsApplicationService.getEsProductById(id);
    }
}
