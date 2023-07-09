package com.yxh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxh.reggie.common.R;
import com.yxh.reggie.entity.Employee;
import com.yxh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 后台登录处理
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //获取密码
        String password = employee.getPassword();
        //进行加密
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        //从数据库中查找
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //没有查到
        if (emp==null){
            return R.error("账号不存在");
        }
        //密码错误
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //状态禁用
        if (emp.getStatus()==0){
            return R.error("账号被禁用");
        }
        //将员工id存入session域中
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出操作
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    /**
     * 添加员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        //设置默认初始密码等信息后保存
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        long employeeId =(long) request.getSession().getAttribute("employee");
        employee.setCreateUser(employeeId);
        employee.setUpdateUser(employeeId);*/
        employeeService.save(employee);
        return R.success("保存成功");
    }

    /**
     * 展示员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page,int pageSize,String name){
        //创建分页对象
        Page pageInfo=new Page<>(page,pageSize);
        //查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //修改更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //修改更新者
        //long employeeId = (long)request.getSession().getAttribute("employee");
        //employee.setUpdateUser(employeeId);
        //执行修改
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    /**
     * 根据id获取员工信息回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
