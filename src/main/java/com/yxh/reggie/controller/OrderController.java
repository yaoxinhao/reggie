package com.yxh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxh.reggie.common.R;
import com.yxh.reggie.entity.Orders;
import com.yxh.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @RequestMapping("/submit")
    public R<String> submit (@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("订单提交成功");
    }

    /**
     * 订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping ("/page")
    public R<Page> page(int page, int pageSize, Long number,  LocalDateTime beginTime, LocalDateTime endTime){
        //加@requestparam表示传过的参数必须有这些，除非设置为false
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(number!=null,Orders::getNumber,number);
        wrapper.between(beginTime!=null&&endTime!=null,Orders::getOrderTime,beginTime,endTime);
        wrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage,wrapper);
        return R.success(ordersPage);
    }

    /**
     * 修改状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Orders::getId,orders.getId()).set(Orders::getStatus,orders.getStatus());
        ordersService.update(wrapper);
        return R.success("派送成功");
    }
}
