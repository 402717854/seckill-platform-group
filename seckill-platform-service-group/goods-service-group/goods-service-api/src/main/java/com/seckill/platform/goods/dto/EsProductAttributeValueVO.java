package com.seckill.platform.goods.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 搜索商品的属性信息
 *
 * @author macro
 * @date 2018/6/27
 */
@Getter
@Setter
public class EsProductAttributeValueVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long productAttributeId;
    //属性值
    private String value;
    //属性参数：0->规格；1->参数
    private Integer type;
    //属性名称
    private String name;
}
