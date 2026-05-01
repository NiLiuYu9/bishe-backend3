package com.api.platform.mapper;

import com.api.platform.entity.ApiInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API信息Mapper接口
 * <p>核心职责：提供API信息表（api_info）的基础CRUD操作，
 * 继承MyBatis-Plus的BaseMapper，自动拥有单表增删改查能力。</p>
 */
@Mapper
public interface ApiInfoMapper extends BaseMapper<ApiInfo> {
}
