package com.asaki0019.advertising.controller;

import com.asaki0019.advertising.service.AdvertisingService;
import com.asaki0019.advertising.serviceMeta.data.AdChartData;
import com.asaki0019.advertising.serviceMeta.res.BaseResponse;
import com.asaki0019.advertising.type.AdTagEnum;
import com.asaki0019.advertising.utils.JWTToken;
import com.asaki0019.advertising.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class AdvertisingChartDataController {

    private final AdvertisingService advertisingService;
    /**
     * 构造函数，使用构造函数注入依赖。
     *
     * @param advertisingService 广告服务
     */
    public AdvertisingChartDataController(AdvertisingService advertisingService) {
        this.advertisingService = advertisingService;
    }

    /**
     * 获取广告图表数据。
     *
     * @return 包含广告图表数据的响应实体
     */
    @PostMapping("/advertising-chart-data")
    public ResponseEntity<BaseResponse<List<AdChartData>>> getAdvertisingChartData(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(new BaseResponse<>(401, "用户不存在", null));
            }
            var userId = (String) JWTToken.parsePayload(jwt).get("uuid");

            // 获取每个标签的广告数量和分发数量
            List<AdChartData> adDataList = collectAdChartData();

            // 构建响应
            var response = new BaseResponse<>(200, "获取广告表格数据成功", adDataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new BaseResponse<>(500, "获取广告表格数据失败: " + e.getMessage(), null));
        }
    }

    /**
     * 收集每个标签的广告数量和分发数量。
     *
     * @return 包含广告数据的列表
     */
    private List<AdChartData> collectAdChartData() {
        List<AdChartData> adDataList = new ArrayList<>();
        for (AdTagEnum tag : AdTagEnum.values()) {
            Integer totalAds = advertisingService.getAdCountByTag(tag.getTagName());
            Integer distributedCount = advertisingService.getDistributedAdCountByTag(tag.getTagName());

            // 处理空值
            totalAds = totalAds != null ? totalAds : 0;
            distributedCount = distributedCount != null ? distributedCount : 0;

            adDataList.add(new AdChartData(tag.getTagName(), totalAds, distributedCount));
        }
        return adDataList;
    }
}