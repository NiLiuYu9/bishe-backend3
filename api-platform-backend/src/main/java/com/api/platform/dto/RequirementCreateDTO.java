package com.api.platform.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class RequirementCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "需求标题不能为空")
    @Size(max = 100, message = "需求标题长度不能超过100个字符")
    private String title;

    @Size(max = 2000, message = "需求描述长度不能超过2000个字符")
    private String description;

    @Valid
    private List<ApiParamDTO> requestParams;

    @Valid
    private List<ApiParamDTO> responseParams;

    @NotNull(message = "预算不能为空")
    @DecimalMin(value = "0.00", message = "预算不能小于0")
    private BigDecimal budget;

    @NotNull(message = "截止日期不能为空")
    private LocalDate deadline;

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
