package com.yxh.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxh.reggie.entity.OrderDetail;
import com.yxh.reggie.mapper.OrderDetailMapper;
import com.yxh.reggie.service.OrderDetailService;
import com.yxh.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImp extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
