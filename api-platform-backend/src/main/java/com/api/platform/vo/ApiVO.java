package com.api.platform.vo;

import com.api.platform.dto.ApiParamDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API详情响应数据
 *
 * 返回API接口的完整信息，包括基本信息、参数定义、调用统计和收藏状态
 */
@Data
public class ApiVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** API ID */
    private Long id;

    /** API名称 */
    private String name;

    /** API描述 */
    private String description;

    /** 所属分类名称 */
    private String typeName;

    /** 所属分类ID */
    private Long typeId;

    /** API提供者用户ID */
    private Long userId;

    /** API提供者用户名 */
    private String username;

    /** 请求方法（GET/POST/PUT/DELETE等） */
    private String method;

    /** API请求路径 */
    private String endpoint;

    /** API目标服务地址 */
    private String targetUrl;

    /** 请求参数定义列表 */
    private List<ApiParamDTO> requestParams;

    /** 响应参数定义列表 */
    private List<ApiParamDTO> responseParams;

    /** 请求参数JSON字符串（冗余存储） */
    private String requestParamsJson;

    /** 响应参数JSON字符串（冗余存储） */
    private String responseParamsJson;

    /** 价格 */
    private BigDecimal price;

    /** 价格单位（次/天/月） */
    private String priceUnit;

    /** 调用次数限制 */
    private Integer callLimit;

    /** 是否启用白名单（0-未启用，1-启用） */
    private Integer whitelistEnabled;

    /** API状态（pending/approved/rejected/offline） */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 技术文档地址 */
    private String docUrl;

    /** 平均评分 */
    private BigDecimal rating;

    /** 总调用次数 */
    private Long invokeCount;

    /** 成功调用次数 */
    private Long successCount;

    /** 失败调用次数 */
    private Long failCount;

    /** 当前用户是否已收藏 */
    private Boolean isFavorited;

}
