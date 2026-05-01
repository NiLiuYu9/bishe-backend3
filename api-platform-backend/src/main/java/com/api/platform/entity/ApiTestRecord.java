package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API测试记录实体 —— 对应数据库表 api_test_record
 *
 * 记录用户在平台在线测试 API 时的请求和响应信息，支持自动调用和手动保存两种类型。
 * type 标识记录类型：0-自动调用（在线测试页面自动记录） / 1-手动保存（用户主动保存）。
 * apiName 为冗余存储，避免关联查询。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("api_test_record")
public class ApiTestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自动调用类型常量 */
    public static final int TYPE_AUTO_CALL = 0;
    /** 手动保存类型常量 */
    public static final int TYPE_MANUAL_SAVE = 1;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 测试的API ID，关联 api_info.id */
    @TableField("api_id")
    private Long apiId;

    /** API名称，冗余存储，避免关联查询api_info表 */
    @TableField("api_name")
    private String apiName;

    /** 测试用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** 请求参数，JSON格式存储 */
    @TableField("params")
    private String params;

    /** 响应结果 */
    @TableField("result")
    private String result;

    /** 是否成功：0-失败，1-成功 */
    @TableField("success")
    private Integer success;

    /** 错误信息，失败时记录错误原因 */
    @TableField("error_msg")
    private String errorMsg;

    /** 响应时间，单位毫秒 */
    @TableField("response_time")
    private Integer responseTime;

    /** HTTP状态码，如 200、404、500 等 */
    @TableField("status_code")
    private Integer statusCode;

    /** 记录类型：0-自动调用（TYPE_AUTO_CALL） / 1-手动保存（TYPE_MANUAL_SAVE） */
    @TableField(value = "type")
    private Integer type;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
