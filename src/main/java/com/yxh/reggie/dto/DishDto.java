package com.yxh.reggie.dto;

import com.yxh.reggie.entity.Dish;
import com.yxh.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DishDto extends Dish {
    //保存菜品口味
    private List<DishFlavor> flavors=new ArrayList<>();
    //保存菜品的菜品分类
    private String categoryName;
    private Integer copies;
}
