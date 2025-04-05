package com.asaki0019.advertising.model;

import com.asaki0019.advertising.type.AdStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ad_statuses")
public class AdStatus {
    @TableId(type = IdType.AUTO)
    private Integer id; // 状态ID
    private String statusName; // 状态名称

    // 将枚举转换为模型
    public static AdStatus fromEnum(AdStatusEnum statusEnum) {
        AdStatus adStatus = new AdStatus();
        adStatus.setId(statusEnum.getId());
        adStatus.setStatusName(statusEnum.getStatusName());
        return adStatus;
    }

    // 将模型转换为枚举
    public AdStatusEnum toEnum() {
        return AdStatusEnum.getById(this.id);
    }
}