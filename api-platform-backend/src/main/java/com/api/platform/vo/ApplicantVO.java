package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 申请人信息响应数据
 *
 * 返回需求申请人的基本信息和申请状态
 */
@Data
public class ApplicantVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请记录ID */
    private Long id;

    /** 申请人用户ID */
    private Long userId;

    /** 申请人用户名 */
    private String username;

    /** 申请说明 */
    private String description;

    /** 申请状态（pending/accepted/rejected） */
    private String status;

    /** 申请时间 */
    private LocalDateTime applyTime;

}
