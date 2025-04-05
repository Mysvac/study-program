package com.asaki0019.advertising.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdStatusEnum {
    UNDER_REVIEW(1, "审核中"),
    UNPUBLISHED(2, "未发布"),
    PUBLISHED(3, "已发布");

    private final Integer id; // 状态ID
    private final String statusName; // 状态名称

    /**
     * 根据 ID 获取枚举
     * 此方法通过遍历枚举值来匹配给定的ID，返回对应的枚举实例
     * 如果没有找到匹配的枚举实例，则抛出IllegalArgumentException异常
     *
     * @param id 枚举实例的ID
     * @return 匹配给定ID的枚举实例
     * @throws IllegalArgumentException 如果给定的ID没有对应的枚举实例
     */
    public static AdStatusEnum getById(Integer id) {
        // 遍历枚举值
        for (AdStatusEnum status : values()) {
            // 检查当前枚举实例的ID是否与给定的ID相等
            if (status.getId().equals(id)) {
                // 如果相等，返回当前枚举实例
                return status;
            }
        }
        // 如果没有找到匹配的枚举实例，抛出异常
        throw new IllegalArgumentException("Invalid AdStatusEnum ID: " + id);
    }

    /**
     * 根据名称获取枚举
     *
     * @param statusName 枚举的名称
     * @return 对应名称的枚举实例
     * @throws IllegalArgumentException 如果名称不匹配任何枚举实例，则抛出此异常
     */
    public static AdStatusEnum getByName(String statusName) {
        for (AdStatusEnum status : values()) {
            if (status.getStatusName().equals(statusName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AdStatusEnum name: " + statusName);
    }
}
