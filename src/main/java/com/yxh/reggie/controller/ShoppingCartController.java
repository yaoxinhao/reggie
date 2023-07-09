package com.yxh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yxh.reggie.common.BaseContext;
import com.yxh.reggie.common.R;
import com.yxh.reggie.entity.ShoppingCart;
import com.yxh.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        return R.success(shoppingCarts);
    }
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart){
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //判断是套餐还是菜品
        Long dishId = shoppingCart.getDishId();
        if (dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //先检查数据库有没有相同菜品/套餐
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        //有就加一份，没有直接加入
        if (shoppingCartOne!=null){
                Integer number = shoppingCartOne.getNumber();
                shoppingCartOne.setNumber(number+1);
                shoppingCartService.updateById(shoppingCartOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(userId);
            shoppingCartService.save(shoppingCart);
        }
        return R.success("添加购物车成功");
    }
    @DeleteMapping("/clean")
    public R<String> delete(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(wrapper);
        return R.success("清空购物车");
    }
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart byId = shoppingCartService.getById(shoppingCart);
        Integer number = byId.getNumber();
        if (number!=1){
            shoppingCart.setNumber(number-1);
            shoppingCartService.updateById(shoppingCart);
        }else {
            shoppingCartService.removeById(shoppingCart);
        }
        return R.success("删除成功");
    }
}
