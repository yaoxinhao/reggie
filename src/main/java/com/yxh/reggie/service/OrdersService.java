package com.yxh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxh.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
