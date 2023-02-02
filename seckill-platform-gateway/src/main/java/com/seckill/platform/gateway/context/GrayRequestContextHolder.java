package com.seckill.platform.gateway.context;

/**
 * 灰度发布上下文操作
 */
public class GrayRequestContextHolder {
    public static final ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();
    public static void setGrayTag(boolean flag){
        threadLocal.remove();
        threadLocal.set(flag);
    }
    public static Boolean getGrayTag(){
        return threadLocal.get()==null?false:threadLocal.get();
    }
    public static void remove(){
         threadLocal.remove();
    }
}
