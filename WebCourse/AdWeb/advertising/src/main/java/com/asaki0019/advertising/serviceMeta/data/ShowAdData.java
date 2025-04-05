package com.asaki0019.advertising.serviceMeta.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowAdData {
    private String title;
    private String tags;
    private String description;
    private String fileType;
    private String fileUrl;


    public ShowAdData(String title, String tags, String description, String fileType, String fileUrl) {
        this.title = title;
        this.tags = tags;
        this.description = description;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
    }
}