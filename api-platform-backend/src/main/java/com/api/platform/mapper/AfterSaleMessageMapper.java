package com.api.platform.mapper;

import com.api.platform.entity.AfterSaleMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 售后消息Mapper接口
 * <p>核心职责：提供售后消息表（after_sale_message）的基础CRUD操作，
 * 继承MyBatis-Plus的BaseMapper，自动拥有单表增删改查能力。</p>
 */
@Mapper
public interface AfterSaleMessageMapper extends BaseMapper<AfterSaleMessage> {
}
