package com.asaki0019.advertising.service;


import com.asaki0019.advertising.model.AdApplication;

import java.util.List;

public interface AdvertisingApplicationService  {
    List<String> selectAdIdsByUserId(String userId);

    /**
     * 申请广告
     * @param adId 广告ID
     * @param applicantId 申请者ID
     * @return 申请记录
     */
    AdApplication applyForAd(String adId, String applicantId);
    AdApplication unApplyForAd(String adId, String applicantId);
}