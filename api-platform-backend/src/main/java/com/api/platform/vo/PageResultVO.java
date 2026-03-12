package com.api.platform.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResultVO<T> {

    private List<T> list;

    private Long total;

    public static <T> PageResultVO<T> of(List<T> list, Long total) {
        PageResultVO<T> result = new PageResultVO<>();
        result.setList(list);
        result.setTotal(total);
        return result;
    }

}
