package com.asaki0019.advertising.mapper;

import com.asaki0019.advertising.model.Ad;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdMapper extends BaseMapper<Ad> {
    /**
     * 根据标签获取广告的总数量
     *
     * @param tag 广告标签
     * @return 广告数量
     */
    @Select("SELECT COUNT(*) FROM ads WHERE tags = #{tag}")
    int countByTag(@Param("tag") String tag);

    /**
     * 获取每个标签的分发数量（通过 GROUP BY 实现）
     *
     * @param tag 广告标签
     * @return 分发数量
     */
    @Select("SELECT SUM(distributed) FROM ads WHERE tags = #{tag} GROUP BY tags")
    Integer getDistributedCountByTag(@Param("tag") String tag);
}
