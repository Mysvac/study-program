package com.asaki0019.advertising.serviceMeta.data;

import lombok.Getter;
import lombok.Setter;

// 数据类
@Setter
@Getter
public class UploadData {
    private String fileName;
    private String fileType;
    private String index;

    public UploadData(String fileName, String fileType, String index) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.index = index;
    }
}