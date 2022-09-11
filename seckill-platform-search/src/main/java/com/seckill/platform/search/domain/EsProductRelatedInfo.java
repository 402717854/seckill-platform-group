package com.seckill.platform.search.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 搜索商品的品牌名称，分类名称及属性
 *
 * @author wys
 * @date 2022/09/11
 */
@Getter
@Setter
public class EsProductRelatedInfo {
    private List<String> brandNames;
    private List<String> productCategoryNames;
    private List<ProductAttr>   productAttrs;

    @Setter
    @Getter
    public static class ProductAttr{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }
}
