package com.asaki0019.advertising.service.impl;

import com.asaki0019.advertising.mapper.AdClickMapper;
import com.asaki0019.advertising.model.AdClick;
import com.asaki0019.advertising.service.AdClickService;
import com.asaki0019.advertising.utils.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdClickServiceImpl implements AdClickService {
    private final AdClickMapper adClickMapper;

    public AdClickServiceImpl(AdClickMapper adClickMapper) {
        this.adClickMapper = adClickMapper;
    }

    /**
     * 更新广告点击记录
     * 如果数据库中不存在对应的广告点击记录，则插入新记录
     * 如果存在，则更新记录中的用户ID、广告ID、新兴趣标签和点击时间
     *
     * @param adClick 广告点击对象，包含需要更新的数据
     */
    @Override
    @Transactional
    public void updateAdClick(AdClick adClick) {
        try {
            // 检查数据库中是否已存在该客户端ID的广告点击记录
            AdClick existingAdClick = adClickMapper.selectOne(
                    new QueryWrapper<AdClick>().eq("client_id", adClick.getClientId())
            );
            if (existingAdClick == null) {
                // 如果不存在，则插入新记录
                adClickMapper.insert(adClick);
            } else {
                // 如果存在，则动态更新非空字段
                UpdateWrapper<AdClick> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("client_id", adClick.getClientId());

                // 更新用户ID和点击时间
                updateWrapper.set(adClick.getUserId() != null, "user_id", adClick.getUserId());
                updateWrapper.set(adClick.getClickTime() != null, "click_time", adClick.getClickTime());

                // 更新兴趣标签（仅更新非零值）
                updateWrapper.set(adClick.getElectronicTag() != 0, "electronic_tag", adClick.getElectronicTag());
                updateWrapper.set(adClick.getHomeTag() != 0, "home_tag", adClick.getHomeTag());
                updateWrapper.set(adClick.getCustomTag() != 0, "custom_tag", adClick.getCustomTag());
                updateWrapper.set(adClick.getMakeupTag() != 0, "makeup_tag", adClick.getMakeupTag());
                updateWrapper.set(adClick.getFoodTag() != 0, "food_tag", adClick.getFoodTag());
                updateWrapper.set(adClick.getTransportationTag() != 0, "transportation_tag", adClick.getTransportationTag());
                updateWrapper.set(adClick.getTravelTag() != 0, "travel_tag", adClick.getTravelTag());

                adClickMapper.update(null, updateWrapper);
            }
        } catch (RuntimeException e) {
            // 记录更新广告点击记录时发生的错误
            Utils.logError("更新广告点击记录失败: " + adClick.toString(), e, "AdClickServiceImpl.updateAdClick");
            throw e;
        }
    }

    @Override
    public AdClick getAdClickInfo(String userId, String clientId) {
        return adClickMapper.selectOne(
                new QueryWrapper<AdClick>()
                        .eq("client_id", clientId)
                        .eq("user_id", userId)
        );
    }
}