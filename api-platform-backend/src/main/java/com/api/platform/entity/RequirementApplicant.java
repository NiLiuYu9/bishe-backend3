package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 需求申请人实体 —— 对应数据库表 requirement_applicant
 *
 * 记录开发者对定制需求的申请信息，一个需求可有多人申请，需求方从中选择一位开发者。
 * 申请人状态：pending(待审核) / accepted(已接受) / rejected(已拒绝)。
 * username 为非持久化字段，查询时关联填充。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("requirement_applicant")
public class RequirementApplicant implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联需求ID，关联 requirement.id */
    @TableField("requirement_id")
    private Long requirementId;

    /** 申请人用户ID，关联 sys_user.id */
    @TableField("user_id")
    private Long userId;

    /** 申请说明，开发者描述自己的技术方案和经验 */
    @TableField("description")
    private String description;

    /** 申请状态：pending(待审核) / accepted(已接受) / rejected(已拒绝) */
    @TableField("status")
    private String status;

    /** 申请时间，插入时自动填充 */
    @TableField(value = "apply_time", fill = FieldFill.INSERT)
    private LocalDateTime applyTime;

    /** 申请人用户名，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private String username;

}
