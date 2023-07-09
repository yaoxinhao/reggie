package com.yxh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yxh.reggie.common.BaseContext;
import com.yxh.reggie.common.R;
import com.yxh.reggie.entity.AddressBook;
import com.yxh.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    public R<String> add(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("地址保存成功");
    }
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        Long userId = BaseContext.getCurrentId();
        wrapper.eq(userId!=null, AddressBook::getUserId,userId).orderByAsc(AddressBook::getUpdateUser);
        List<AddressBook> addressBookList = addressBookService.list(wrapper);
        return R.success(addressBookList);
    }
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        Long id = addressBook.getId();
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("默认地址修改成功");
    }
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改地址成功");
    }
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,userId).eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(wrapper);
        return R.success(one);
    }
}
