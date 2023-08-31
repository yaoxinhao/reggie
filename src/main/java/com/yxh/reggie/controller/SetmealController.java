package com.yxh.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxh.reggie.common.R;
import com.yxh.reggie.dto.SetmealDto;
import com.yxh.reggie.entity.Category;
import com.yxh.reggie.entity.Setmeal;
import com.yxh.reggie.service.CategoryService;
import com.yxh.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    /**
     * 保存套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CachePut(value = "setmealCache")
    public R<String> add(@RequestBody SetmealDto setmealDto){
        setmealService.saveMealByDish(setmealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 参照dishcontroller
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<SetmealDto> setmealDtoPage = new Page<>();
        Page<Setmeal> setmealPage = new Page<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(StringUtils.hasText(name),Setmeal::getName,name).orderByAsc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,setmealLambdaQueryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        ArrayList<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal setmeal:setmealPage.getRecords()){
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            setmealDtos.add(setmealDto);
        }
        setmealDtoPage.setRecords(setmealDtos);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }
    @PostMapping("/status/{status}")
    public R<String> update(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,status);
        setmealService.update(updateWrapper);
        return R.success("更新套餐状态成功");
    }
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#categoryId+'_'+#status")
    public R<List<Setmeal>> list(@RequestParam Long categoryId, @RequestParam Integer status){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId,categoryId).eq(Setmeal::getStatus,status);
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }
}
