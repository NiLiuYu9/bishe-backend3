package com.api.platform.vo;

import lombok.Data;

@Data
public class AccessKeyVO {

    private Long id;

    private String username;

    private String accessKey;

    private String secretKey;

}
