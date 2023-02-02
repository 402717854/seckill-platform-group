package com.seckill.platform.search.context;

/**
 * 灰度发布上下文操作
 */
public class GrayRequestContextHolder {
    public static final ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();
    public static void setGrayTag(boolean flag){
        threadLocal.set(flag);
    }
    public static Boolean getGrayTag(){
        return threadLocal.get();
    }
    public static void remove(){
         threadLocal.remove();
    }
}
