//package com.seckill.platform.drools.config;
//
//import com.seckill.platform.drools.enums.CustomerType;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.core.convert.converter.ConverterFactory;
//import org.springframework.stereotype.Component;
//
///**
// * Todo 请求数据转化
// *
// * @Author wys
// * @Date 2023/4/24 17:42
// */
//@Component
//public class EnumsConverterFactory implements ConverterFactory<String,CustomerType>{
//    @Override
//    public <T extends CustomerType> Converter<String, T> getConverter(Class<T> aClass) {
//        return new Converter<String, T>() {
//            @Override
//            public T convert(String s) {
//                T[] enumConstants = aClass.getEnumConstants();
//                for (T enumConstant : enumConstants) {
//                    if(enumConstant.getValue().equals(s)){
//                        return enumConstant;
//                    }
//                }
//                return null;
//            }
//        };
//    }
//}
