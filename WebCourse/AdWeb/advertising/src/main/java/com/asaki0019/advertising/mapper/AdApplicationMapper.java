package com.asaki0019.advertising.mapper;

import com.asaki0019.advertising.model.AdApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdApplicationMapper extends
        BaseMapper<AdApplication> {
    @Select("SELECT ad_id FROM advertising.ad_applications WHERE applicant_id = #{userId}")
    List<String> selectAdIdsByUserId(@Param("userId") String userId);
}
