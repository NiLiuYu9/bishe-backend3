package com.api.platform.mapper;

import com.api.platform.entity.ApiReview;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API评价Mapper接口
 * <p>核心职责：提供API评价表（api_review）的基础CRUD操作，
 * 继承MyBatis-Plus的BaseMapper，自动拥有单表增删改查能力。</p>
 */
@Mapper
public interface ApiReviewMapper extends BaseMapper<ApiReview> {
}
