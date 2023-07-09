package com.yxh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxh.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
