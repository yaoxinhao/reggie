package com.yxh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxh.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long ids);
}
