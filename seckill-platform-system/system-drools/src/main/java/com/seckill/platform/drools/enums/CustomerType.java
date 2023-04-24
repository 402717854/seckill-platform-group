package com.seckill.platform.drools.enums;

/**
 * Todo
 *
 * @Author wys
 * @Date 2023/4/24 10:59
 */
public enum CustomerType {
    LOYAL, NEW, DISSATISFIED;

    public String getValue() {
        return this.toString();
    }
}
