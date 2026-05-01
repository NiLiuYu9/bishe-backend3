package com.api.platform.mapper;

import com.api.platform.entity.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 订单信息Mapper接口
 * <p>核心职责：提供订单表（order_info）的基础CRUD操作，
 * 并支持按API维度计算平均评分，用于API评分展示。</p>
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 根据API ID计算该API的平均评分
     * <p>仅统计已评分（rating非空）且未删除的订单，用于API详情页展示评分。</p>
     *
     * @param apiId API接口ID
     * @return 该API的平均评分，无评分数据时返回null
     */
    @Select("SELECT AVG(rating) FROM order_info WHERE api_id = #{apiId} AND rating IS NOT NULL AND deleted = 0")
    BigDecimal getAverageRatingByApiId(@Param("apiId") Long apiId);

}
