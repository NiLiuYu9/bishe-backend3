package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API分类实体 —— 对应数据库表 api_type
 *
 * 记录 API 的分类信息（如天气、金融、生活服务等），用于 API 归类和筛选。
 * 每个分类下可包含多个 API，apiCount 为非持久化字段，查询时动态统计。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("api_type")
public class ApiType implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 分类名称，如"天气服务"、"金融数据"等 */
    @TableField("name")
    private String name;

    /** 分类描述 */
    @TableField("description")
    private String description;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 删除标记：0-未删除，1-已删除（该表未使用@TableLogic，需手动处理） */
    @TableField("deleted")
    private Integer deleted;

    /** 该分类下的API数量，非数据库字段，通过关联查询统计填充 */
    @TableField(exist = false)
    private Integer apiCount;

}
