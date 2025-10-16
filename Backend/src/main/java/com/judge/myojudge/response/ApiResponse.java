package com.judge.myojudge.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ApiResponse<T>  {
    private Boolean success;
    private int statusCode;
    private String message;
    private T data;
    private String timestamp;
    private String path;
}
