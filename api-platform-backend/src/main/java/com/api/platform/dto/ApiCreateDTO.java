package com.api.platform.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建API请求参数
 *
 * 用于 /api/create 接口，接收API上架时提交的完整信息，包括基本属性、参数定义和计费配置
 */
@Data
public class ApiCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** API名称，不能为空，最长100个字符 */
    @NotBlank(message = "API名称不能为空")
    @Size(max = 100, message = "API名称长度不能超过100个字符")
    private String name;

    /** API描述，最长1000个字符 */
    @Size(max = 1000, message = "API描述长度不能超过1000个字符")
    private String description;

    /** API分类ID，不能为空 */
    @NotNull(message = "API类型不能为空")
    private Long typeId;

    /** 请求方法，只能为GET、POST、PUT或DELETE */
    @NotBlank(message = "请求方法不能为空")
    @Pattern(regexp = "^(GET|POST|PUT|DELETE)$", message = "请求方法只能是GET、POST、PUT或DELETE")
    private String method;

    /** 接口地址，不能为空，最长255个字符 */
    @NotBlank(message = "接口地址不能为空")
    @Size(max = 255, message = "接口地址长度不能超过255个字符")
    private String endpoint;

    /** 目标服务器地址，最长255个字符 */
    @Size(max = 255, message = "目标服务器地址长度不能超过255个字符")
    private String targetUrl;

    /** 请求参数列表，不能为空，至少包含一个参数 */
    @NotNull(message = "请求参数不能为空")
    @Size(min = 1, message = "请求参数至少需要一个")
    @Valid
    private List<ApiParamDTO> requestParams;

    /** 响应参数列表，不能为空，至少包含一个参数 */
    @NotNull(message = "响应参数不能为空")
    @Size(min = 1, message = "响应参数至少需要一个")
    @Valid
    private List<ApiParamDTO> responseParams;

    /** 价格，不能为空，不能小于0 */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能小于0")
    private BigDecimal price;

    /** 计费单位，只能为per_call（按次）、per_month（按月）或per_year（按年） */
    @NotBlank(message = "计费单位不能为空")
    @Pattern(regexp = "^(per_call|per_month|per_year)$", message = "计费单位只能是per_call、per_month或per_year")
    private String priceUnit;

    /** 调用限制次数，不能为空，不能小于0 */
    @NotNull(message = "调用限制不能为空")
    @Min(value = 0, message = "调用限制不能小于0")
    private Integer callLimit;

    /** 文档地址，最长255个字符 */
    @Size(max = 255, message = "文档地址长度不能超过255个字符")
    private String docUrl;

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
