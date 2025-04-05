package com.asaki0019.advertising.service;

import com.asaki0019.advertising.model.AdClick;

public interface AdClickService {
    void updateAdClick(AdClick adClick);
    AdClick getAdClickInfo(String userId, String clientId);
}
