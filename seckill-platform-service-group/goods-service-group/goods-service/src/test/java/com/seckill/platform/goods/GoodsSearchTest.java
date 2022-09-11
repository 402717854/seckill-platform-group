package com.seckill.platform.goods;

import cn.hutool.json.JSONUtil;
import com.seckill.platform.goods.domain.service.GoodsService;
import com.seckill.platform.goods.dto.EsProductVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author wangys10
 * @date 2022/9/11 12:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsSearchTest {

    @Autowired
    private GoodsService goodsService;


    @Test
    public void testSearchES(){
        List<EsProductVO> allEsProductList = goodsService.getAllEsProductList(null);
        System.out.println(JSONUtil.parse(allEsProductList));
    }
}
