package com.yxh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxh.reggie.dto.DishDto;
import com.yxh.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品时加入口味
    void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品和口味
    DishDto getByIdWithFlavor(Long id);
    //更新菜品时同时更新口味
    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(List<Long> ids);
}
