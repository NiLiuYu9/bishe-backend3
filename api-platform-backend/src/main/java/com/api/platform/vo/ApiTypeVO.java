package com.api.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API分类响应数据
 *
 * 返回API分类的完整信息，包括分类名称、描述和该分类下的API数量
 */
@Data
public class ApiTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类描述 */
    private String description;

    /** 分类状态 */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 该分类下的API数量 */
    private Integer apiCount;

}
