package com.api.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体 —— 对应数据库表 sys_user
 *
 * 记录平台用户的核心信息，包括登录凭证、AK/SK 密钥对、管理员标识和账号状态等。
 * AK/SK 用于网关鉴权签名，isAdmin 控制后台访问权限，status 控制账号是否可用。
 * 使用 MyBatis-Plus 注解完成 ORM 映射。
 */
@Data
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名，登录凭证，唯一 */
    @TableField("username")
    private String username;

    /** 密码，加密存储 */
    @TableField("password")
    private String password;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 是否管理员：0-普通用户，1-管理员 */
    @TableField("is_admin")
    private Integer isAdmin;

    /** Access Key，API调用鉴权的访问密钥，注册时自动生成 */
    @TableField("access_key")
    private String accessKey;

    /** Secret Key，API调用鉴权的签名密钥，与accessKey配对使用，注册时自动生成 */
    @TableField("secret_key")
    private String secretKey;

    /** 账号状态：0-正常，1-冻结 */
    @TableField("status")
    private Integer status;

    /** 冻结原因，status=1时记录冻结理由 */
    @TableField("freeze_reason")
    private String freezeReason;

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

    /** 用户技能标签列表，非数据库字段，通过关联查询填充 */
    @TableField(exist = false)
    private List<String> tags;

}
