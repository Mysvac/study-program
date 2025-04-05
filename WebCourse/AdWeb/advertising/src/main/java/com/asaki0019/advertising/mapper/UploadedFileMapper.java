package com.asaki0019.advertising.mapper;

import com.asaki0019.advertising.model.UploadedFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadedFileMapper extends BaseMapper<UploadedFile> {
    // 继承 BaseMapper，无需额外定义方法
}
