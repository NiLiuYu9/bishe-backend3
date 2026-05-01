package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 需求技术标签实体 —— 对应数据库表 requirement_tag
 *
 * 记录需求关联的技术标签（如 Java、Python、RESTful 等），用于需求分类和智能匹配。
 * 一个需求可关联多个标签，与 requirement 表为一对多关系。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("requirement_tag")
public class RequirementTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联需求ID，关联 requirement.id */
    @TableField("requirement_id")
    private Long requirementId;

    /** 标签名称，如 Java、Python、RESTful、微服务等 */
    @TableField("tag_name")
    private String tagName;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
