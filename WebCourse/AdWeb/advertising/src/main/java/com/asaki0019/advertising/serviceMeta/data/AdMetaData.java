package com.asaki0019.advertising.serviceMeta.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class AdMetaData {
    private String id;
    private String title;
    private String status;
    private String tags;
    private String description;
    private String distributor;
    private BigDecimal cost;
    private String fileId;
    private String isRequest; // 标记是否已申请
    private int distributed;
    public AdMetaData(String id,
                      String title,
                      String status,
                      String tags,
                      String description,
                      String distributor,
                      BigDecimal cost,
                      String fileId,
                      String isRequest,
                      int distributed) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.tags = tags;
        this.description = description;
        this.distributor = distributor;
        this.cost = cost;
        this.fileId = fileId;
        this.isRequest = isRequest;
        this.distributed = distributed;
    }
}
