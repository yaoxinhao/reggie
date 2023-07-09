package com.yxh.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxh.reggie.entity.AddressBook;
import com.yxh.reggie.mapper.AddressBookMapper;
import com.yxh.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImp extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
