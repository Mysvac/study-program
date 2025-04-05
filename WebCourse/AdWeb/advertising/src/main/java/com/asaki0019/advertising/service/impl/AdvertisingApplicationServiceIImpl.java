package com.asaki0019.advertising.service.impl;

import com.asaki0019.advertising.mapper.AdApplicationMapper;
import com.asaki0019.advertising.mapper.AdMapper;
import com.asaki0019.advertising.model.Ad;
import com.asaki0019.advertising.model.AdApplication;
import com.asaki0019.advertising.service.AdvertisingApplicationService;
import com.asaki0019.advertising.utils.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdvertisingApplicationServiceIImpl implements AdvertisingApplicationService {

    private final AdApplicationMapper adApplicationMapper;
    private final AdMapper adMapper;

    public AdvertisingApplicationServiceIImpl(AdApplicationMapper adApplicationMapper, AdMapper adMapper) {
        this.adApplicationMapper = adApplicationMapper;
        this.adMapper = adMapper;
    }

    @Override
    public List<String> selectAdIdsByUserId(String userId) {
        try {
            return adApplicationMapper.selectAdIdsByUserId(userId);
        } catch (RuntimeException e) {
            Utils.logError("无法根据 userId 获取列表信息", e, "userId: " + userId);
            throw e; // 重新抛出异常，确保调用方能够处理
        }
    }

    @Override
    @Transactional
    public AdApplication applyForAd(String adId, String applicantId) {
        try {
            // 查询广告信息
            Ad ad = adMapper.selectById(adId);
            if (ad == null) {
                throw new RuntimeException("广告不存在，adId: " + adId);
            }

            // 更新广告分发数量
            int distribution = ad.getDistributed();
            ad.setDistributed(distribution + 1);
            adMapper.updateById(ad);

            // 创建广告申请记录
            AdApplication application = new AdApplication();
            application.setAdId(adId);
            application.setApplicantId(applicantId);
            application.setApplicationTime(LocalDateTime.now());

            // 保存广告申请记录
            adApplicationMapper.insert(application);

            return application;
        } catch (RuntimeException e) {
            Utils.logError("申请广告失败", e, "adId: " + adId + ", applicantId: " + applicantId);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }

    @Override
    @Transactional
    public AdApplication unApplyForAd(String adId, String applicantId) {
        try {
            // 构建删除条件
            QueryWrapper<AdApplication> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ad_id", adId).eq("applicant_id", applicantId);

            // 删除广告申请记录
            int deleted = adApplicationMapper.delete(queryWrapper);
            if (deleted > 0) {
                // 更新广告分发数量
                Ad ad = adMapper.selectById(adId);
                if (ad != null) {
                    int distribution = ad.getDistributed();
                    ad.setDistributed(distribution - 1);
                    adMapper.updateById(ad);
                }
                return new AdApplication();
            } else {
                throw new RuntimeException("删除广告申请记录失败，adId: " + adId + ", applicantId: " + applicantId);
            }
        } catch (RuntimeException e) {
            Utils.logError("取消申请广告失败", e, "adId: " + adId + ", applicantId: " + applicantId);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }
}