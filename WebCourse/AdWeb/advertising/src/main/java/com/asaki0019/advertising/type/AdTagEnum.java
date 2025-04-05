package com.asaki0019.advertising.type;

import lombok.Getter;

@Getter
public enum AdTagEnum {
    ELECTRONICS("电子产品"),
    HOUSEHOLD("家居用品"),
    CLOTHING("服装服饰"),
    BEAUTY("美妆护肤"),
    FOOD("食品饮料"),
    AUTOMOTIVE("汽车交通"),
    TRAVEL("旅游出行");

    private final String tagName;

    AdTagEnum(String tagName) {
        this.tagName = tagName;
    }

}