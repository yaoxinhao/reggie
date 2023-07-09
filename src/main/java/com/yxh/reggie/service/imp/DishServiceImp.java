package com.yxh.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxh.reggie.dto.DishDto;
import com.yxh.reggie.entity.Dish;
import com.yxh.reggie.entity.DishFlavor;
import com.yxh.reggie.mapper.DishMapper;
import com.yxh.reggie.service.DishFlavorService;
import com.yxh.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImp extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存传入的菜品信息
        this.save(dishDto);
        //获得新菜品id
        Long dishId = dishDto.getId();
        //保存新菜的口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置口味表中的菜品id
        for(DishFlavor item:flavors){
            item.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //获得当前菜品信息
        Dish dish = this.getById(id);
        //拷贝到dto中
        BeanUtils.copyProperties(dish,dishDto);
        //查询对应口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        //将口味保存入dto中
        dishDto.setFlavors(list);
        return dishDto;
    }
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品信息
        this.updateById(dishDto);
        //查询对应口味表并保存更新信息
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor item:flavors){
            item.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品，主表和关联表都要操作
     * @param ids
     */
    @Override
    public void removeWithFlavor(List<Long> ids) {
        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
    }
}
