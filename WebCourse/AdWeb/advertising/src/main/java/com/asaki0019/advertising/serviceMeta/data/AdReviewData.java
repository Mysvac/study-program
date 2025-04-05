package com.asaki0019.advertising.serviceMeta.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdReviewData {
    private String id;
    private String tag;
    private String title;
    private String description;
    private String distributor;
    private BigDecimal cost;
    private String url;

    // 构造函数
    public AdReviewData(String id, String tag, String title,String url,
                        String description, String distributor, BigDecimal cost) {
        this.id = id;
        this.tag = tag;
        this.title = title;
        this.url = url;
        this.description = description;
        this.distributor = distributor;
        this.cost = cost;
    }
}