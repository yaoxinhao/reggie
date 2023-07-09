package com.yxh.reggie.controller;

import com.yxh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
/**
 * 文件上传和下载
 */
public class CommonController {
    //存储路径
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //获取原文件名
        String originalFilename = file.getOriginalFilename();
        //获得后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid作为上传文件名
        UUID randomUUID = UUID.randomUUID();
        String filename=randomUUID+suffix;
        File file1 = new File(basePath);
        if (!file1.exists()){
            file1.mkdirs();
        }
        try {
            //将文件保存到指定目录
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(filename);
    }
    @GetMapping("download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，输入到浏览器
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));
            //获得输出流
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            //开始读取
            int len=0;
            byte[] bytes=new byte[1024];
            while ((len=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭流
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
