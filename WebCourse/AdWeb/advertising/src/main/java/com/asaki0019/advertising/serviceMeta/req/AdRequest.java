package com.asaki0019.advertising.serviceMeta.req;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdRequest {
    private String jwt;
    private String tag; // 广告标签
    private String title; // 广告标题
    private String description; // 广告描述
    private String distributor; // 发布商
    private BigDecimal cost; // 广告价格
    private String fileId; // 嵌套对象
}