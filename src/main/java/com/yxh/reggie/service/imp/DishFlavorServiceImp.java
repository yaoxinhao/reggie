package com.yxh.reggie.service.imp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxh.reggie.entity.Dish;
import com.yxh.reggie.entity.DishFlavor;
import com.yxh.reggie.mapper.DishFlavorMapper;
import com.yxh.reggie.mapper.DishMapper;
import com.yxh.reggie.service.DishFlavorService;
import com.yxh.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImp extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
