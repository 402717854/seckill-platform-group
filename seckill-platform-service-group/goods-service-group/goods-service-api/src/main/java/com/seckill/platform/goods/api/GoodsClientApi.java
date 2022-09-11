package com.seckill.platform.goods.api;

import com.seckill.platform.goods.dto.EsProductVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author lenovo
 */
@FeignClient("seckill-platform-goods")
public interface GoodsClientApi {

    @GetMapping("/goods/getAllEsProductList")
    public List<EsProductVO> getAllEsProductList();
    @GetMapping("/goods/getEsProductById/{id}")
    public EsProductVO getEsProductById(@PathVariable Long id);
}
