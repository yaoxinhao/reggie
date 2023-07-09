package com.yxh.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxh.reggie.entity.User;
import com.yxh.reggie.mapper.UserMapper;
import com.yxh.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService {
}
