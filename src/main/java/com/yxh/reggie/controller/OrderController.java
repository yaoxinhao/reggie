package com.yxh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxh.reggie.common.BaseContext;
import com.yxh.reggie.common.R;
import com.yxh.reggie.dto.OrdersDto;
import com.yxh.reggie.entity.OrderDetail;
import com.yxh.reggie.entity.Orders;
import com.yxh.reggie.service.OrderDetailService;
import com.yxh.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

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
    /**
     * 用户端订单显示
     */
    @GetMapping("userPage")
    public R<Page> page(int page,int pageSize){
        Page<OrdersDto> ordersDtoPage = new Page<>();
        Page<Orders> ordersPage = new Page<>();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, BaseContext.getCurrentId()).orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage,wrapper);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        OrdersDto ordersDto = new OrdersDto();
        Orders orders = ordersPage.getRecords().get(0);
        BeanUtils.copyProperties(orders,ordersDto);
        LambdaQueryWrapper<OrderDetail> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(wrapper1);
        ordersDto.setOrderDetails(orderDetails);
        ordersDtoPage.setRecords(Arrays.asList(ordersDto));
        return R.success(ordersDtoPage);
    }
    /**
     * 再来一单
     */
    @PostMapping("again")
    public R<String> again(@RequestBody Long id){
        //body接受请求体jason，param接受请求头及地址栏后的，pathvariable为/{id}
        return R.success("再来一单");
    }
}
