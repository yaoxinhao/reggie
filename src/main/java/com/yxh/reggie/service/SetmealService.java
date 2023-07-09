package com.yxh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxh.reggie.dto.SetmealDto;
import com.yxh.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveMealByDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
