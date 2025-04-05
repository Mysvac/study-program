package com.asaki0019.advertising.serviceMeta.res;

import com.asaki0019.advertising.serviceMeta.data.UploadData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadResponse {
    private int code;
    private String message;
    private UploadData data;

    public UploadResponse(int code, String message, UploadData data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}