package com.asaki0019.advertising.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ad_applications")
public class AdApplication {
    @TableId(type = IdType.ASSIGN_UUID) // 使用 UUID 生成主键
    private String id; // 申请ID，UUID
    private String adId; // 广告ID
    private String applicantId; // 申请者ID
    private LocalDateTime applicationTime; // 申请时间
}