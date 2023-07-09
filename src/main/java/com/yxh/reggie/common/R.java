package com.yxh.reggie.common;

import lombok.Data;

import java.util.Map;

@Data
public class R<T> {
    private Integer code;//1表示成功，0表示失败
    private String msg;//回显消息
    private T data;//数据
    private Map map;//动态数据
    public static <T> R<T> success(T object){
        R<T> r = new R<>();
        r.data=object;
        r.code=1;
        return r;
    }
    public static <T> R<T> error(String msg){
        R<T> r = new R<>();
        r.msg=msg;
        r.code=0;
        return r;
    }
    public R<T> add(String key,Object value){
        map.put(key,value);
        return this;
    }
}
