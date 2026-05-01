package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * API信息实体 —— 对应数据库表 api_info
 *
 * 记录平台上架 API 的完整信息，包括接口定义、定价、调用限制、审核状态、统计数据等。
 * API状态流转：pending(待审核) → approved(已通过) / rejected(已拒绝) → offline(已下线)。
 * invokeCount/successCount/failCount 为冗余统计字段，由定时任务或网关回调更新。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("api_info")
public class ApiInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** API分类ID，关联 api_type.id */
    @TableField("type_id")
    private Long typeId;

    /** API发布者用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** API名称 */
    @TableField("name")
    private String name;

    /** API描述说明 */
    @TableField("description")
    private String description;

    /** HTTP请求方法：GET / POST / PUT / DELETE 等 */
    @TableField("method")
    private String method;

    /** API网关路由路径，如 /weather/beijing，网关根据此路径匹配转发 */
    @TableField("endpoint")
    private String endpoint;

    /** API实际后端目标URL，网关动态路由转发目标地址 */
    @TableField("target_url")
    private String targetUrl;

    /** 请求参数定义，JSON格式存储，格式为JSON数组，每个元素包含name/type/required/description字段 */
    @TableField("request_params")
    private String requestParams;

    /** 响应参数定义，JSON格式存储，格式为JSON数组，每个元素包含name/type/required/description字段 */
    @TableField("response_params")
    private String responseParams;

    /** 单次调用价格 */
    @TableField("price")
    private BigDecimal price;

    /** 价格单位，如 次/天/月 */
    @TableField("price_unit")
    private String priceUnit;

    /** 调用次数限制，每次购买允许调用的总次数 */
    @TableField("call_limit")
    private Integer callLimit;

    /** 是否启用白名单：0-未启用，1-已启用。启用后仅白名单用户可调用 */
    @TableField("whitelist_enabled")
    private Integer whitelistEnabled;

    /** API审核状态：pending(待审核) / approved(已通过) / rejected(已拒绝) / offline(已下线) */
    @TableField("status")
    private String status;

    /** 技术文档URL */
    @TableField("doc_url")
    private String docUrl;

    /** 平均评分，冗余存储，由评价聚合计算更新 */
    @TableField("rating")
    private BigDecimal rating;

    /** 总调用次数，冗余统计字段 */
    @TableField("invoke_count")
    private Long invokeCount;

    /** 成功调用次数，冗余统计字段 */
    @TableField("success_count")
    private Long successCount;

    /** 失败调用次数，冗余统计字段 */
    @TableField("fail_count")
    private Long failCount;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除标记：0-未删除，1-已删除。@TableLogic 使 delete 操作变为 update */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 分类名称，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String typeName;

    /** 发布者用户名，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String username;

}
