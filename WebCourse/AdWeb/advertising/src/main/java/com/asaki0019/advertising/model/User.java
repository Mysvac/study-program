package com.asaki0019.advertising.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data // Lombok 注解，自动生成 getter、setter、toString 等方法
@TableName("users") // MyBatis-Plus 注解，指定数据库表名
public class User {

    @TableId(type = IdType.ASSIGN_UUID) // 使用 UUID 生成主键
    private String id; // 申请ID，UUID

    private String role;

    private String username;

    private String name;

    private String password;

}