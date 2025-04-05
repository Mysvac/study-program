package com.asaki0019.advertising.service;

import com.asaki0019.advertising.model.Ad;
import com.asaki0019.advertising.model.AdApplication;

import java.util.List;

public interface AdvertisingService {
    /**
     * 创建广告
     * @param ad 广告对象（包含广告的基本信息）
     * @param advertiserId 广告发布者ID
     * @param fileId 文件ID（广告关联的文件）
     * @return 创建的广告对象
     */
    Ad createAd(Ad ad, String advertiserId, String fileId);


    /**
     * 审核广告
     * @param adId 广告ID
     * @param statusId 审核后的状态ID（如未发布）
     * @return 审核后的广告对象
     */
    Ad reviewAd(String adId, int statusId);

    /**
     * 发布广告
     * @param adId 广告ID
     * @return 发布后的广告对象
     */
    Ad getAdByAdId(String adId);

    /**
     * 删除广告
     * @param adId 广告ID
     * @param advertiserId 广告发布者ID（用于验证权限）
     */
    void deleteAd(String adId, String advertiserId);

    /**
     * 获取广告列表
     * @param userId 用户ID（用于根据用户角色或状态获取广告）
     * @return 广告列表
     */
    List<Ad> getAdsByUser(String userId);

    /**
     * 获取广告申请列表
     * @param userId 用户ID（用于根据用户角色或状态获取广告申请）
     * @return 广告申请列表
     */
    List<AdApplication> getAdApplicationsByUser(String userId);

    // 获取所有审核过的广告，并标记当前用户是否已申请过
    List<Ad> getAllReviewedAdsWithUserAppliedStatus(String userId);

    List<Ad> getAllAds();

    Integer getAdCountByTag(String tag);

    Integer getDistributedAdCountByTag(String tag);
}
