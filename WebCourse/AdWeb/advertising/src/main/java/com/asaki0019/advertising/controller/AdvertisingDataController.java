package com.asaki0019.advertising.controller;

import com.asaki0019.advertising.model.Ad;
import com.asaki0019.advertising.service.AdvertisingApplicationService;
import com.asaki0019.advertising.service.AdvertisingService;
import com.asaki0019.advertising.service.UploadedFileService;
import com.asaki0019.advertising.serviceMeta.data.AdData;
import com.asaki0019.advertising.serviceMeta.data.AdMetaData;
import com.asaki0019.advertising.serviceMeta.data.AdReviewData;
import com.asaki0019.advertising.serviceMeta.res.BaseResponse;
import com.asaki0019.advertising.type.AdStatusEnum;
import com.asaki0019.advertising.utils.JWTToken;
import com.asaki0019.advertising.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理广告数据获取请求的控制器。
 */
@RestController
@RequestMapping("/api")
public class AdvertisingDataController {

    private final AdvertisingService advertisingService;
    private final UploadedFileService uploadedFileService;
    private final AdvertisingApplicationService advertisingApplicationService;
    private static final String AD_URL_PREFIX = "http://localhost:5173/#/SAD/";

    /**
     * 构造函数，使用构造函数注入依赖。
     *
     * @param advertisingService            广告服务
     * @param uploadedFileService           文件上传服务
     * @param advertisingApplicationService 广告申请服务
     */
    public AdvertisingDataController(AdvertisingService advertisingService,
                                     UploadedFileService uploadedFileService,
                                     AdvertisingApplicationService advertisingApplicationService) {
        this.advertisingService = advertisingService;
        this.uploadedFileService = uploadedFileService;
        this.advertisingApplicationService = advertisingApplicationService;
    }

    /**
     * 获取广告表格数据。
     *
     * @return 包含广告表格数据的响应实体
     */
    @PostMapping("/advertising-table-data")
    public ResponseEntity<BaseResponse<List<AdMetaData>>> getAdvertisingTableData(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(new BaseResponse<>(401, "用户不存在", null));
            }
            var userId = (String) JWTToken.parsePayload(jwt).get("uuid");
            List<Ad> reviewedAds = advertisingService.getAllReviewedAdsWithUserAppliedStatus(userId);
            List<String> appliedAdIds = advertisingApplicationService.selectAdIdsByUserId(userId);

            // 将广告数据转换为前端需要的格式
            List<AdMetaData> adDataList = reviewedAds.stream()
                    .map(ad -> convertToAdMetaData(ad, appliedAdIds))
                    .toList();

            // 构建响应
            var response = new BaseResponse<>(200, "获取广告表格数据成功", adDataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Utils.logError("获取广告表格数据失败", e, "AdvertisingDataController.getAdvertisingTableData");
            return ResponseEntity.status(500).body(new BaseResponse<>(500, "获取广告表格数据失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取当前用户的广告表格数据。
     *
     * @return 包含当前用户广告表格数据的响应实体
     */
    @PostMapping("/advertising-id-table-data")
    public ResponseEntity<BaseResponse<List<AdData>>> getAdvertisingUseTableData(
            @RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(new BaseResponse<>(401, "用户不存在", null));
            }
            var userId = (String) JWTToken.parsePayload(jwt).get("uuid");
            // 获取当前用户的广告列表
            List<Ad> ads = advertisingService.getAdsByUser(userId);

            // 过滤广告状态，只保留 "审核中"、"已发布" 的广告
            List<Ad> filteredAds = ads.stream()
                    .filter(ad -> Objects.equals(ad.getStatusId(), AdStatusEnum.UNDER_REVIEW.getId())
                            || Objects.equals(ad.getStatusId(), AdStatusEnum.PUBLISHED.getId()))
                    .toList();

            // 将广告数据转换为前端需要的格式
            List<AdData> adDataList = filteredAds.stream()
                    .map(this::convertToAdData)
                    .toList();

            // 构建响应
            var response = new BaseResponse<>(200, "获取广告表格数据成功", adDataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Utils.logError("获取个人请求数据失败", e, "AdvertisingDataController.getAdvertisingUseTableData");
            return ResponseEntity.status(500).body(new BaseResponse<>(500, "获取个人请求数据失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取需要审核的广告表格数据。
     *
     * @return 包含需要审核广告表格数据的响应实体
     */
    @PostMapping("/advertising-review-data")
    public ResponseEntity<BaseResponse<List<AdReviewData>>> adReviewTableResponse(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(new BaseResponse<>(401, "用户不存在", null));
            }

            // 获取所有广告
            List<Ad> ads = advertisingService.getAllAds();

            // 过滤广告状态，只保留 "审核中" 的广告
            List<Ad> filteredAds = ads.stream()
                    .filter(ad -> Objects.equals(ad.getStatusId(), AdStatusEnum.UNDER_REVIEW.getId()))
                    .toList();

            // 将广告数据转换为前端需要的格式
            List<AdReviewData> adReviewDataList = filteredAds.stream()
                    .map(this::convertToAdReviewData)
                    .toList();

            // 构建响应
            var response = new BaseResponse<>(200, "获取需要审核广告表格数据成功", adReviewDataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new BaseResponse<>(500, "获取需要审核广告表格数据失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取已申请的广告数据。
     *
     * @param body 包含用户 cookie 的请求体
     * @return 包含已申请广告数据的响应实体
     */
    @PostMapping("/fetch-request-ads")
    public ResponseEntity<BaseResponse<List<AdMetaData>>> getAdvertisingAppliedData(@RequestBody Map<String, String> body) {
        try {
            var userCookie = body.get("userCookie");
            if (userCookie == null) {
                return ResponseEntity.status(500).body(new BaseResponse<>(500, "获取已申请广告数据失败: cookie 错误 ", null));
            }

            List<Ad> reviewedAds = advertisingService.getAllReviewedAdsWithUserAppliedStatus(userCookie);
            List<String> appliedAdIds = advertisingApplicationService.selectAdIdsByUserId(userCookie);

            // 只保留已申请的广告
            List<AdMetaData> appliedAdDataList =
                    reviewedAds.stream()
                    .filter(ad -> appliedAdIds.contains(ad.getId())) // 过滤已申请的广告
                    .map(ad -> convertToAdMetaData(ad, appliedAdIds))
                    .toList();

            // 构建响应
            var response = new BaseResponse<>(200, "获取已申请广告数据成功", appliedAdDataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Utils.logError("获取已申请广告数据失败", e, "AdvertisingDataController.getAdvertisingAppliedData");
            return ResponseEntity.status(500).body(new BaseResponse<>(500, "获取已申请广告数据失败: " + e.getMessage(), null));
        }
    }

    /**
     * 将广告对象转换为 AdMetaData 对象。
     *
     * @param ad           广告对象
     * @param appliedAdIds 已申请广告的 ID 列表
     * @return 转换后的 AdMetaData 对象
     */
    private AdMetaData convertToAdMetaData(Ad ad, List<String> appliedAdIds) {
        String status = getAdStatus(ad.getStatusId());
        boolean isRequest = appliedAdIds.contains(ad.getId()); // 判断广告是否已申请
        return new AdMetaData(
                ad.getId(),
                ad.getTitle(),
                status,
                ad.getTags(),
                ad.getDescription(),
                ad.getAdvertiserName(), // 假设广告中有 advertiserName 字段
                ad.getPrice(),
                uploadedFileService.getUploadedFileById(ad.getFileId()).getFileUrl(),
                isRequest ? "已申请" : "未申请",
                ad.getDistributed()
        );
    }

    /**
     * 将广告对象转换为 AdData 对象。
     *
     * @param ad 广告对象
     * @return 转换后的 AdData 对象
     */
    private AdData convertToAdData(Ad ad) {
        String status = getAdStatus(ad.getStatusId());
        return new AdData(
                ad.getId(),
                ad.getTitle(),
                status,
                ad.getTags(),
                ad.getDescription(),
                ad.getAdvertiserName(), // 假设广告中有 advertiserName 字段
                ad.getPrice(),
                ad.getFileId()
        );
    }

    /**
     * 将广告对象转换为 AdReviewData 对象。
     *
     * @param ad 广告对象
     * @return 转换后的 AdReviewData 对象
     */
    private AdReviewData convertToAdReviewData(Ad ad) {
        String url = uploadedFileService.getUploadedFileById(ad.getFileId()).getFileUrl();
        return new AdReviewData(
                ad.getId(),
                ad.getTags(),
                ad.getTitle(),
                url,
                ad.getDescription(),
                ad.getAdvertiserName(),
                ad.getPrice()
        );
    }

    /**
     * 根据广告状态 ID 获取状态名称。
     *
     * @param statusId 广告状态 ID
     * @return 广告状态名称
     */
    private String getAdStatus(int statusId) {
        return switch (statusId) {
            case 1 -> AdStatusEnum.UNDER_REVIEW.getStatusName();
            case 2 -> AdStatusEnum.UNPUBLISHED.getStatusName();
            case 3 -> AdStatusEnum.PUBLISHED.getStatusName();
            default -> "未知状态";
        };
    }
}