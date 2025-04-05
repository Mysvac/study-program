package com.asaki0019.advertising.serviceMeta.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdData {
    private String id; // 广告 ID
    private String title; // 广告标题
    private String status; // 广告状态
    private String tag; // 广告标签
    private String description; // 广告描述
    private String distributor; // 发布商
    private BigDecimal cost; // 广告价格
    private String fileId; // 文件 ID

    public AdData(String id, String title,
                  String status, String tag,
                  String description,
                  String distributor,
                  BigDecimal cost, String fileId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.tag = tag;
        this.description = description;
        this.distributor = distributor;
        this.cost = cost;
        this.fileId = fileId;
    }
}