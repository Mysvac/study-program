package com.asaki0019.advertising.controller;

import com.asaki0019.advertising.model.Ad;
import com.asaki0019.advertising.model.AdClick;
import com.asaki0019.advertising.service.AdClickService;
import com.asaki0019.advertising.service.AdvertisingApplicationService;
import com.asaki0019.advertising.service.AdvertisingService;
import com.asaki0019.advertising.service.UploadedFileService;
import com.asaki0019.advertising.type.AdTagEnum;
import com.asaki0019.advertising.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AdClickController {

    private final AdvertisingService advertisingService;
    private final AdClickService adClickService;
    private final AdvertisingApplicationService advertisingApplicationService;
    private final UploadedFileService uploadedFileService;
    public AdClickController(AdvertisingService advertisingService,
                             AdClickService adClickService,
                             UploadedFileService uploadedFileService,
                             AdvertisingApplicationService advertisingApplicationService) {
        this.advertisingService = advertisingService;
        this.uploadedFileService = uploadedFileService;
        this.adClickService = adClickService;
        this.advertisingApplicationService = advertisingApplicationService;
    }


    /**
     * 处理广告点击请求。
     *
     * @param request 包含广告点击信息的请求体
     * @return 包含匹配广告 URL 的响应实体
     */
    @PostMapping("/ad-click")
    public ResponseEntity<Map<String, Object>> handleAdClick(@RequestBody Map<String, Object> request) {
        try {
            // 获取请求参数
            String clientId = (String) request.get("client_id");
            String userId = (String) request.get("user_id");
            String tag = (String) request.get("tag");

            // 验证请求参数
            if (clientId == null || tag == null || userId == null) {
                Utils.logError("不合适的参数", null, "AdClickController");
                return ResponseEntity.status(500).body(Map.of("Error", "Invalid"));
            }

            // 获取用户已申请的广告
            List<Ad> reviewedAds = advertisingService.getAllReviewedAdsWithUserAppliedStatus(userId);
            List<String> appliedAdIds = advertisingApplicationService.selectAdIdsByUserId(userId);
            List<Ad> appliedAds = reviewedAds.stream()
                    .filter(ad -> appliedAdIds.contains(ad.getId()))
                    .toList();

            // 记录广告点击
            AdTagEnum adTag = matchTag(tag);
            recordAdClick(userId, clientId, adTag);

            // 获取用户的广告点击记录
            AdClick adClick = adClickService.getAdClickInfo(userId, clientId);

            // 统计各标签的点击量
            Map<AdTagEnum, Integer> tagClickCounts = new HashMap<>();
            tagClickCounts.put(AdTagEnum.ELECTRONICS, adClick.getElectronicTag());
            tagClickCounts.put(AdTagEnum.HOUSEHOLD, adClick.getHomeTag());
            tagClickCounts.put(AdTagEnum.CLOTHING, adClick.getCustomTag());
            tagClickCounts.put(AdTagEnum.BEAUTY, adClick.getMakeupTag());
            tagClickCounts.put(AdTagEnum.FOOD, adClick.getFoodTag());
            tagClickCounts.put(AdTagEnum.AUTOMOTIVE, adClick.getTransportationTag());
            tagClickCounts.put(AdTagEnum.TRAVEL, adClick.getTravelTag());

            // 按点击量从高到低排序标签
            List<AdTagEnum> sortedTags = tagClickCounts.entrySet().stream()
                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                    .map(Map.Entry::getKey)
                    .toList();

            // 根据排序后的标签推荐广告
            List<Map<String, Object>> recommendedAds = new ArrayList<>();
            for (AdTagEnum sortedTag : sortedTags) {
                // 获取当前标签对应的广告列表
                List<Ad> adsForTag = appliedAds.stream()
                        .filter(ad -> ad.getTags().contains(sortedTag.getTagName()))
                        .toList();

                // 如果当前标签有广告，则随机挑选最多 2 个广告
                if (!adsForTag.isEmpty()) {
                    List<Ad> randomAds = getRandomAds(adsForTag, 4); // 随机挑选最多 2 个广告
                    recommendedAds.addAll(randomAds.stream()
                            .map(this::createAdWithUrl)
                            .toList());
                }

                // 如果推荐的广告数量达到上限，则停止推荐
                if (recommendedAds.size() >= 6) {
                    break;
                }
            }

            // 如果推荐的广告数量不足，则补充一些不相关的广告
            if (recommendedAds.size() < 6) {
                List<Ad> otherAds = appliedAds.stream()
                        .filter(ad -> sortedTags.stream().noneMatch(tagEnum -> ad.getTags().contains(tagEnum.getTagName())))
                        .toList();

                List<Ad> randomOtherAds = getRandomAds(otherAds, 6 - recommendedAds.size()); // 随机挑选不足的广告
                recommendedAds.addAll(randomOtherAds.stream()
                        .map(this::createAdWithUrl)
                        .toList());
            }

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("ad_urls", recommendedAds);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Utils.logError("广告点击处理失败", e, "AdClickController");
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 从广告列表中随机挑选指定数量的广告。
     *
     * @param ads      广告列表
     * @param maxCount 最大挑选数量
     * @return 随机挑选的广告列表
     */
    private List<Ad> getRandomAds(List<Ad> ads, int maxCount) {
        if (ads.size() <= maxCount) {
            return ads; // 如果广告数量不足，则返回全部
        }

        // 随机打乱广告列表
        List<Ad> shuffledAds = new ArrayList<>(ads);
        Collections.shuffle(shuffledAds);

        // 返回前 maxCount 个广告
        return shuffledAds.subList(0, maxCount);
    }

    private void recordAdClick(String userId, String clientId, AdTagEnum adTag) {
        // 查询数据库中是否已存在该用户和客户端的广告点击记录
        AdClick adClick = adClickService.getAdClickInfo(userId, clientId);

        if (adClick == null) {
            // 如果不存在，则创建新记录
            adClick = new AdClick();
            adClick.setUserId(userId);
            adClick.setClientId(clientId);
        }

        // 设置点击时间
        adClick.setClickTime(new Timestamp(System.currentTimeMillis()));

        // 更新对应标签的点击量
        if (adTag != null) {
            switch (adTag) {
                case ELECTRONICS -> adClick.setElectronicTag(adClick.getElectronicTag() + 1);
                case HOUSEHOLD -> adClick.setHomeTag(adClick.getHomeTag() + 1);
                case CLOTHING -> adClick.setCustomTag(adClick.getCustomTag() + 1);
                case BEAUTY -> adClick.setMakeupTag(adClick.getMakeupTag() + 1);
                case FOOD -> adClick.setFoodTag(adClick.getFoodTag() + 1);
                case AUTOMOTIVE -> adClick.setTransportationTag(adClick.getTransportationTag() + 1);
                case TRAVEL -> adClick.setTravelTag(adClick.getTravelTag() + 1);
            }
        }
        // 保存或更新记录
        adClickService.updateAdClick(adClick);
    }

    private AdTagEnum matchTag(String tag) {
        for (AdTagEnum adTag : AdTagEnum.values()) {
            if (adTag.getTagName().equals(tag)) {
                return adTag;
            }
        }
        return null;
    }

    private Map<String, Object> createAdWithUrl(Ad ad) {
        Map<String, Object> adWithUrl = new HashMap<>();
        adWithUrl.put("adId", ad.getId());
        adWithUrl.put("adTitle", ad.getTitle());
        adWithUrl.put("adTag", ad.getTags());
        adWithUrl.put("adDescription", ad.getDescription());
        adWithUrl.put("adPrice", ad.getPrice());
        adWithUrl.put("adUrl", uploadedFileService.getUploadedFileById(ad.getFileId()).getFileUrl());
        return adWithUrl;
    }
}