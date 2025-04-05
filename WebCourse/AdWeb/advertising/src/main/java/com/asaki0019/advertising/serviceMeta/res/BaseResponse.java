package com.asaki0019.advertising.serviceMeta.res;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T>  {
    private int code; // 响应状态码
    private String message; // 响应消息
    private T data; // 泛型数据字段

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse() {
        // 默认构造函数
    }
}