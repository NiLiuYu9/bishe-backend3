package com.api.platform.mapper;

import com.api.platform.entity.ApiTestRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ApiTestRecordMapper extends BaseMapper<ApiTestRecord> {

    @Select("SELECT COUNT(*) FROM api_test_record WHERE user_id = #{userId} AND api_id = #{apiId} AND type = 1")
    int countByUserIdAndApiId(@Param("userId") Long userId, @Param("apiId") Long apiId);

    @Select("SELECT COUNT(*) FROM api_test_record WHERE user_id = #{userId} AND api_id = #{apiId} AND type = 0 AND DATE(create_time) = CURDATE()")
    int countTodayCallsByUserIdAndApiId(@Param("userId") Long userId, @Param("apiId") Long apiId);

}
