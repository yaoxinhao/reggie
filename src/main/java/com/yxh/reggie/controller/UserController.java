package com.yxh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxh.reggie.Utils.ValidateCodeUtils;
import com.yxh.reggie.common.BaseContext;
import com.yxh.reggie.common.R;
import com.yxh.reggie.entity.Orders;
import com.yxh.reggie.entity.User;
import com.yxh.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取输入的手机号
        String phone = user.getPhone();
        //生成验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code="+code);
        //redis保存
        redisTemplate.opsForValue().set(phone,code, 5,TimeUnit.MINUTES);
        System.out.println("取出"+redisTemplate.opsForValue().get(phone));
        //保存在session中
        //session.setAttribute(phone,code);
        return R.success("短信发送成功");
    }

    /**
     * 用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //获得输入的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

//        String phone1 = session.getAttribute(phone).toString();不能用这个
        //获得生产的验证码
        //Object phone1 = session.getAttribute(phone);
        //redis获得输入的手机号和验证码
        Object phone1 = redisTemplate.opsForValue().get(phone);
        //进行比对
        if (phone1!=null&&phone1.equals(code)){
            //查询是否存在用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);
            //不存在就生产新用户
            if (user==null){
                user= new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //将用户信息存入session中
            session.setAttribute("user",user.getId());
            //登录后删除redis中的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
