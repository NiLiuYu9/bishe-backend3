package com.api.platform.mapper;

import com.api.platform.entity.RequirementAfterSale;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 需求售后Mapper接口
 * <p>核心职责：提供需求售后表（requirement_after_sale）的基础CRUD操作，
 * 继承MyBatis-Plus的BaseMapper，自动拥有单表增删改查能力。</p>
 */
@Mapper
public interface RequirementAfterSaleMapper extends BaseMapper<RequirementAfterSale> {
}
