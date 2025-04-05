package com.asaki0019.advertising.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ads")
public class Ad {
    @TableId(type = IdType.ASSIGN_UUID) // 使用 UUID 生成主键
    private String id; // 申请ID，UUID
    private String title; // 广告标题
    private String tags; // 广告标签
    private String description; // 广告描述
    private String advertiserName;
    private String advertiserId; // 广告发布商ID
    private BigDecimal price; // 广告价格
    private String fileId; // 文件ID
    private Integer statusId; // 广告状态ID
    private LocalDateTime createdTime; // 广告创建时间
    private Integer distributed; // 广告分发量
}