package com.api.platform.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 创建需求请求参数
 *
 * 用于 /requirement/create 接口，接收需求方发布定制需求时提交的完整信息
 */
@Data
public class RequirementCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 需求标题，不能为空，最长100个字符 */
    @NotBlank(message = "需求标题不能为空")
    @Size(max = 100, message = "需求标题长度不能超过100个字符")
    private String title;

    /** 需求描述，最长2000个字符 */
    @Size(max = 2000, message = "需求描述长度不能超过2000个字符")
    private String description;

    /** 请求参数定义列表 */
    @Valid
    private List<ApiParamDTO> requestParams;

    /** 响应参数定义列表 */
    @Valid
    private List<ApiParamDTO> responseParams;

    /** 预算金额，不能为空，不能小于0 */
    @NotNull(message = "预算不能为空")
    @DecimalMin(value = "0.00", message = "预算不能小于0")
    private BigDecimal budget;

    /** 截止日期，不能为空 */
    @NotNull(message = "截止日期不能为空")
    private LocalDate deadline;

    /** 技术标签列表 */
    private List<String> tags;

    public String getRequestParamsJson() {
        if (requestParams == null) {
            return null;
        }
        return JSONUtil.toJsonStr(requestParams);
    }

    public String getResponseParamsJson() {
        if (responseParams == null) {
            return null;
        }
        return JSONUtil.toJsonStr(responseParams);
    }

}
