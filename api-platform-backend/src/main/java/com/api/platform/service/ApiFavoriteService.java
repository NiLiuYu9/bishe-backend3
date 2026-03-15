package com.api.platform.service;

import com.api.platform.entity.ApiFavorite;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ApiFavoriteService extends IService<ApiFavorite> {

    void addFavorite(Long userId, Long apiId);

    void removeFavorite(Long userId, Long apiId);

    boolean isFavorited(Long userId, Long apiId);

    List<Long> getUserFavoriteApiIds(Long userId);

    IPage<ApiVO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize);

}
