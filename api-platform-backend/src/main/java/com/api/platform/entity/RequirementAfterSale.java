package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 需求售后实体 —— 对应数据库表 requirement_after_sale
 *
 * 记录需求交付后的售后纠纷信息，包括申请人（需求方）、开发者、管理员裁定等。
 * 售后状态流转：pending(待处理) → resolved(已解决) / rejected(已拒绝)。
 * 售后结果(result)：completed(完成交付) / refunded(退款)。
 * applicantName/developerName/adminName 为非持久化字段，查询时关联填充。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("requirement_after_sale")
public class RequirementAfterSale implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联需求ID，关联 requirement.id */
    @TableField("requirement_id")
    private Long requirementId;

    /** 售后申请人ID（需求方），关联 sys_user.id */
    @TableField("applicant_id")
    private Long applicantId;

    /** 开发者ID，关联 sys_user.id */
    @TableField("developer_id")
    private Long developerId;

    /** 售后申请原因 */
    @TableField("reason")
    private String reason;

    /** 未实现的功能列表，JSON格式存储，描述交付中缺失的功能点 */
    @TableField("unimplemented_features")
    private String unimplementedFeatures;

    /** 处理管理员ID，关联 sys_user.id，管理员裁定时填入 */
    @TableField("admin_id")
    private Long adminId;

    /** 管理员裁定意见 */
    @TableField("admin_decision")
    private String adminDecision;

    /** 管理员裁定时间 */
    @TableField("admin_decision_time")
    private LocalDateTime adminDecisionTime;

    /** 售后状态：pending(待处理) / resolved(已解决) / rejected(已拒绝) */
    @TableField("status")
    private String status;

    /** 售后结果：completed(完成交付) / refunded(退款) */
    @TableField("result")
    private String result;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 需求标题，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String requirementTitle;

    /** 申请人用户名，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String applicantName;

    /** 开发者用户名，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String developerName;

    /** 管理员用户名，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String adminName;

}
