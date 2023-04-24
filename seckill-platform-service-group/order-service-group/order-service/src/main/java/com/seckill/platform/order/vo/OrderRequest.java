package com.seckill.platform.order.vo;

import com.seckill.platform.order.enums.CustomerType;
import lombok.Getter;
import lombok.Setter;

/**
 * Todo 订单对象
 *
 * @Author wys
 * @Date 2023/4/24 10:58
 */
@Getter
@Setter
public class OrderRequest {
    /**
     * 客户号
     */
    private String customerNumber;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 订单金额
     */
    private Long amount;
    /**
     * 客户类型
     */
    private CustomerType customerType;
}
