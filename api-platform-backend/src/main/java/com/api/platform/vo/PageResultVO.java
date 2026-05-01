package com.api.platform.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果响应数据
 *
 * 通用分页封装，包含数据列表和总记录数
 *
 * @param <T> 数据类型
 */
@Data
public class PageResultVO<T> {

    /** 数据列表 */
    private List<T> list;

    /** 总记录数 */
    private Long total;

    /**
     * 构建分页结果的静态工厂方法
     *
     * @param list  数据列表
     * @param total 总记录数
     * @param <T>   数据类型
     * @return 分页结果
     */
    public static <T> PageResultVO<T> of(List<T> list, Long total) {
        PageResultVO<T> result = new PageResultVO<>();
        result.setList(list);
        result.setTotal(total);
        return result;
    }

}
