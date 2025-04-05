package com.asaki0019.advertising.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("ad_clicks")
public class AdClick {
    @TableId(type = IdType.ASSIGN_UUID) // 使用 UUID 生成主键
    String id;
    private String userId; // Corresponds to user_id in the table
    private Timestamp clickTime; // Corresponds to click_time in the table
    private String clientId; // Corresponds to client_id in the table
    private Integer electronicTag = 0; // Corresponds to electronic_tag in the table
    private Integer homeTag = 0; // Corresponds to home_tag in the table
    private Integer customTag = 0; // Corresponds to custom_tag in the table
    private Integer makeupTag = 0; // Corresponds to makeup_tag in the table
    private Integer foodTag = 0; // Corresponds to food_tag in the table
    private Integer transportationTag = 0; // Corresponds to transportation_tag in the table
    private Integer travelTag = 0; // Corresponds to trave_tag in the table
}