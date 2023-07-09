package com.yxh.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxh.reggie.common.CustomException;
import com.yxh.reggie.entity.Category;
import com.yxh.reggie.entity.Dish;
import com.yxh.reggie.entity.Setmeal;
import com.yxh.reggie.mapper.CategoryMapper;
import com.yxh.reggie.service.CategoryService;
import com.yxh.reggie.service.DishService;
import com.yxh.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImp extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setMealService;

    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1>0){
            throw new CustomException("当前分类关联了菜品");
        }
        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2=setMealService.count(setMealLambdaQueryWrapper);
        if (count2>0){
            throw new CustomException("当前套餐关联了菜品");
        }
        super.removeById(ids);
    }
}
