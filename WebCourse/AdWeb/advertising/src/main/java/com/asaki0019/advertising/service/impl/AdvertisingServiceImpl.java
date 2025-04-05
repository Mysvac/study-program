package com.asaki0019.advertising.service.impl;

import com.asaki0019.advertising.mapper.AdMapper;
import com.asaki0019.advertising.model.Ad;
import com.asaki0019.advertising.model.AdApplication;
import com.asaki0019.advertising.service.AdvertisingService;
import com.asaki0019.advertising.service.UploadedFileService;
import com.asaki0019.advertising.type.AdStatusEnum;
import com.asaki0019.advertising.utils.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdvertisingServiceImpl implements AdvertisingService {

    private final AdMapper adMapper;
    private final UploadedFileService uploadedFileService;

    @Autowired
    public AdvertisingServiceImpl(
            AdMapper adMapper,
            UploadedFileService uploadedFileService) {
        this.adMapper = adMapper;
        this.uploadedFileService = uploadedFileService;
    }

    @Override
    @Transactional
    public Ad createAd(Ad ad, String advertiserId, String fileId) {
        try {
            // 验证 fileId 是否存在
            if (uploadedFileService.getUploadedFileById(fileId) == null) {
                throw new IllegalArgumentException("File with ID " + fileId + " does not exist.");
            }

            // 设置广告的初始状态为“审核中”
            ad.setStatusId(AdStatusEnum.UNDER_REVIEW.getId());
            ad.setAdvertiserId(advertiserId);
            ad.setFileId(fileId);

            // 保存广告到数据库
            adMapper.insert(ad);
            return ad;
        } catch (RuntimeException e) {
            Utils.logError("创建广告失败", e, "advertiserId: " + advertiserId + ", fileId: " + fileId);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }

    @Override
    @Transactional
    public Ad reviewAd(String adId, int statusId) {
        try {
            Ad ad = adMapper.selectById(adId);
            if (ad == null) {
                throw new IllegalArgumentException("Ad with ID " + adId + " does not exist.");
            }
            ad.setStatusId(statusId);
            adMapper.updateById(ad);
            return ad;
        } catch (RuntimeException e) {
            Utils.logError("审核广告失败", e, "adId: " + adId + ", statusId: " + statusId);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }

    @Override
    public Ad getAdByAdId(String adId) {
        try {
            Ad ad = adMapper.selectById(adId);
            if (ad == null) {
                throw new IllegalArgumentException("Ad with ID " + adId + " does not exist.");
            }
            return ad;
        } catch (RuntimeException e) {
            Utils.logError("获取广告失败", e, "adId: " + adId);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public void deleteAd(String adId, String advertiserId) {
        try {
            Ad ad = adMapper.selectById(adId);
            if (ad == null) {
                throw new IllegalArgumentException("Ad with ID " + adId + " does not exist.");
            }
            if (!ad.getAdvertiserId().equals(advertiserId)) {
                throw new IllegalArgumentException("You are not authorized to delete this ad.");
            }

            // 删除广告
            adMapper.deleteById(adId);
        } catch (RuntimeException e) {
            Utils.logError("删除广告失败", e, "adId: " + adId + ", advertiserId: " + advertiserId);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public List<Ad> getAdsByUser(String userId) {
        try {
            QueryWrapper<Ad> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("advertiser_id", userId); // 根据 advertiser_id 查询
            return adMapper.selectList(queryWrapper);
        } catch (RuntimeException e) {
            Utils.logError("获取用户广告列表失败", e, "userId: " + userId);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public List<AdApplication> getAdApplicationsByUser(String userId) {
        try {
            // 这里应该是实际的业务逻辑
            return List.of(); // 返回空列表作为示例
        } catch (RuntimeException e) {
            Utils.logError("获取用户广告申请列表失败", e, "userId: " + userId);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public List<Ad> getAllReviewedAdsWithUserAppliedStatus(String userId) {
        try {
            return adMapper.selectList(new QueryWrapper<Ad>()
                    .ne("status_id", AdStatusEnum.UNDER_REVIEW.getId()));
        } catch (RuntimeException e) {
            Utils.logError("获取已审核广告列表失败", e, "userId: " + userId);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public List<Ad> getAllAds() {
        try {
            return adMapper.selectList(null); // 查询所有广告
        } catch (RuntimeException e) {
            Utils.logError("获取所有广告列表失败", e, null);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public Integer getAdCountByTag(String tag) {
        try {
            return adMapper.countByTag(tag);
        } catch (RuntimeException e) {
            Utils.logError("根据标签获取广告数量失败", e, "tag: " + tag);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public Integer getDistributedAdCountByTag(String tag) {
        try {
            return adMapper.getDistributedCountByTag(tag);
        } catch (RuntimeException e) {
            Utils.logError("根据标签获取已分发广告数量失败", e, "tag: " + tag);
            throw e; // 重新抛出异常
        }
    }
}