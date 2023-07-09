package com.yxh.reggie.controller;

import ch.qos.logback.core.pattern.ConverterUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxh.reggie.common.R;
import com.yxh.reggie.dto.DishDto;
import com.yxh.reggie.entity.Category;
import com.yxh.reggie.entity.Dish;
import com.yxh.reggie.entity.DishFlavor;
import com.yxh.reggie.service.CategoryService;
import com.yxh.reggie.service.DishFlavorService;
import com.yxh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存菜品信息
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("保存成功");
    }

    /**
     * 分页展示菜品功能
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //创建dish分页
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //按需查找菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.hasText(name),Dish::getName,name);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        dishService.page(dishPage,dishLambdaQueryWrapper);
        //将dishpage的容器信息拷贝到dishdtopage,不拷贝菜品信息
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        //准备集合存放dto信息
        ArrayList<DishDto> dishDtos = new ArrayList<>();
        //获取菜品信息和分类，保存入dto，再将dto保存进集合,不可以在48行先拷贝一部分，后面直接加入
        //原因：要遍历dishdto，其中类型要为dish，但父类不能访问子类变量，设置不了菜品分类
        for (Dish dish:dishPage.getRecords()){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            dishDtos.add(dishDto);
        }
        dishDtoPage.setRecords(dishDtos);
        return R.success(dishDtoPage);
    }

    /**
     * 获得菜品信息回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable("id") Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
    /**
     * 修改菜品信息
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("更新成功");
    }

    /**
     * 修改菜品状态
     * @param status
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        //可以使用request来接受ids，但需要转为Long，不如@RequestParam方便，？链接不能用@PathVariable
        //List<Dish> dishes = dishService.listByIds(list);
        //只需操作一次数据库
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,ids).set(Dish::getStatus,status);
        dishService.update(updateWrapper);
        return R.success("状态修改成功");
    }
    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
//        String parameter = request.getParameter("ids");
//        String[] strings = parameter.split(",");
//        List<String> list = Arrays.asList(strings);
        dishService.removeWithFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 更具categoryId查询菜品
     * @param categoryId
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(@RequestParam Long categoryId){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId,categoryId);
//        List<Dish> dishes = dishService.list(queryWrapper);
//        return R.success(dishes);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(@RequestParam Long categoryId){
        ArrayList<DishDto> dtoArrayList = new ArrayList<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId).eq(Dish::getStatus,1);
        List<Dish> dishes = dishService.list(queryWrapper);
        for (Dish dish:dishes){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Long id = dish.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            dtoArrayList.add(dishDto);
        }
        return R.success(dtoArrayList);
    }
}
