package com.api.platform.mapper;

import com.api.platform.entity.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    @Select("SELECT AVG(rating) FROM order_info WHERE api_id = #{apiId} AND rating IS NOT NULL AND deleted = 0")
    BigDecimal getAverageRatingByApiId(@Param("apiId") Long apiId);

}
