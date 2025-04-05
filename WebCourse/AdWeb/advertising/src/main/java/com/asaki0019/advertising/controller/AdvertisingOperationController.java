package com.asaki0019.advertising.controller;

import com.asaki0019.advertising.model.Ad;
import com.asaki0019.advertising.model.AdApplication;
import com.asaki0019.advertising.model.UploadedFile;
import com.asaki0019.advertising.service.AdvertisingApplicationService;
import com.asaki0019.advertising.service.AdvertisingService;
import com.asaki0019.advertising.service.UploadedFileService;
import com.asaki0019.advertising.serviceMeta.data.ShowAdData;
import com.asaki0019.advertising.serviceMeta.res.BaseResponse;
import com.asaki0019.advertising.type.AdStatusEnum;
import com.asaki0019.advertising.utils.JWTToken;
import com.asaki0019.advertising.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 处理广告操作请求的控制器。
 */
@RestController
@RequestMapping("/api")
public class AdvertisingOperationController {

    private final AdvertisingService advertisingService;
    private final AdvertisingApplicationService advertisingApplicationService;
    private final UploadedFileService uploadedFileService;

    /**
     * 构造函数，使用构造函数注入依赖。
     *
     * @param advertisingService            广告服务
     * @param advertisingApplicationService 广告申请服务
     * @param uploadedFileService           文件上传服务
     */
    public AdvertisingOperationController(AdvertisingService advertisingService,
                                          AdvertisingApplicationService advertisingApplicationService,
                                          UploadedFileService uploadedFileService) {
        this.advertisingService = advertisingService;
        this.advertisingApplicationService = advertisingApplicationService;
        this.uploadedFileService = uploadedFileService;
    }

    /**
     * 审核通过广告。
     *
     * @param body    包含广告 ID 的请求体
     * @return 包含审核结果的响应实体
     */
    @PostMapping("/advertising-review-data-ok")
    public ResponseEntity<Map<String, Object>> approveAd(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户不存在"));
            }
            var id = body.get("id");
            Ad ad = advertisingService.reviewAd(id, AdStatusEnum.PUBLISHED.getId());
            return ResponseEntity.ok(Map.of("code", 200, "message", "审核广告数据成功", "id", ad.getId()));
        } catch (Exception e) {
            Utils.logError("广告审核处理失败", e, "AdvertisingDataController.getAdvertisingTableData");
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
    /**
     * 审核通过广告。
     *
     * @param body    包含广告 ID 的请求体
     * @return 包含审核结果的响应实体
     */
    @PostMapping("/advertising-review-data-false")
    public ResponseEntity<Map<String, Object>> refuseAd(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户不存在"));
            }
            var adId = body.get("id");
            Ad ad = advertisingService.getAdByAdId(adId);
            advertisingService.deleteAd(adId, ad.getAdvertiserId());
            return ResponseEntity.ok(Map.of("code", 200, "message", "拒绝广告数据成功", "id", ad.getId()));
        } catch (Exception e) {
            Utils.logError("广告审核处理失败", e, "AdvertisingDataController.getAdvertisingTableData");
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
    /**
     * 申请广告。
     *
     * @param body    包含广告 ID 的请求体
     * @return 包含申请结果的响应实体
     */
    @PostMapping("/request-advertising")
    public ResponseEntity<Map<String, Object>> requestAd(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户未登录"));
            }
            var userId = (String) JWTToken.parsePayload(jwt).get("uuid");
            var id = body.get("id");
            AdApplication application = advertisingApplicationService.applyForAd(id, userId);
            return ResponseEntity.ok(Map.of("code", 200, "message", "申请广告成功", "id", application.getApplicantId()));
        } catch (Exception e) {
            Utils.logError("广告申请处理失败", e, "AdvertisingDataController.getAdvertisingTableData");
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }

    /**
     * 取消申请广告。
     *
     * @param body    包含广告 ID 的请求体
     * @return 包含取消申请结果的响应实体
     */
    @PostMapping("/unRequest-advertising")
    public ResponseEntity<Map<String, Object>> unRequestAd(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户未登录"));
            }
            var userId = (String) JWTToken.parsePayload(jwt).get("uuid");
            var id = body.get("id");
            advertisingApplicationService.unApplyForAd(id, userId);
            return ResponseEntity.ok(Map.of("code", 200, "message", "解除广告成功"));
        } catch (Exception e) {
            Utils.logError("广告申请处理失败", e, "AdvertisingDataController.getAdvertisingTableData");
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }

    /**
     * 删除广告。
     *
     * @param body    包含广告 ID 的请求体
     * @param session HTTP 会话对象
     * @return 包含删除结果的响应实体
     */
    @PostMapping("/delete-advertising")
    public ResponseEntity<Map<String, Object>> deleteAd(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            var jwt = body.get("jwt");
            if (Utils.isNotUserLoggedIn(jwt)) {
                return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户未登录"));
            }
            var userId = (String) JWTToken.parsePayload(jwt).get("uuid");
            var adId = body.get("id");
            Ad ad = advertisingService.getAdByAdId(adId);
            String fileId = ad.getFileId();
            if (fileId == null) {
                Utils.logError("广告文件不存在", null, "AdvertisingDataController.getAdvertisingTableData");
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "错误的资源链接"));
            }
            uploadedFileService.deleteFileFromFileSystem(fileId);
            advertisingService.deleteAd(adId, userId);
            return ResponseEntity.ok(Map.of("code", 200, "message", "删除广告成功"));
        } catch (Exception e) {
            Utils.logError("广告删除处理失败", e, "AdvertisingDataController.getAdvertisingTableData");
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }

    /**
     * 展示广告详情。
     *
     * @param adId 广告 ID
     * @return 包含广告详情的响应实体
     */
    @GetMapping("/show-ad")
    public ResponseEntity<BaseResponse<ShowAdData>> showAd(@RequestParam("adId") String adId) {
        try {
            if (adId == null) {
                Utils.logError("广告 ID 为空", null, "AdvertisingDataController.showAd");
                return ResponseEntity.status(400).body(new BaseResponse<>(400, "广告 ID 为空", null));
            }
            Ad ad = advertisingService.getAdByAdId(adId);
            UploadedFile file = uploadedFileService.getUploadedFileById(ad.getFileId());
            BaseResponse<ShowAdData> adResponse = new BaseResponse<>(
                    200,
                    "访问成功",
                    new ShowAdData(
                            ad.getTitle(),
                            ad.getTags(),
                            ad.getDescription(),
                            file.getFileType(),
                            file.getFileUrl()
                    )
            );
            return ResponseEntity.ok(adResponse);
        } catch (Exception e) {
            Utils.logError("广告详情访问失败", e, "AdvertisingDataController.showAd");
            return ResponseEntity.status(500).body(new BaseResponse<>(500, "访问失败: " + e.getMessage(), null));
        }
    }
}