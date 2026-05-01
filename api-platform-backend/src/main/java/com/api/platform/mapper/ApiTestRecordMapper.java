package com.api.platform.mapper;

import com.api.platform.entity.ApiTestRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * API测试记录Mapper接口
 * <p>核心职责：提供API测试记录表（api_test_record）的基础CRUD操作，
 * 并支持统计用户的测试调用次数和每日免费调用次数。</p>
 */
@Mapper
public interface ApiTestRecordMapper extends BaseMapper<ApiTestRecord> {

    /**
     * 统计用户对指定API的测试调用总次数
     * <p>type=1表示测试调用，用于判断用户是否已使用过免费测试机会。</p>
     *
     * @param userId 用户ID
     * @param apiId  API接口ID
     * @return 测试调用总次数
     */
    @Select("SELECT COUNT(*) FROM api_test_record WHERE user_id = #{userId} AND api_id = #{apiId} AND type = 1")
    int countByUserIdAndApiId(@Param("userId") Long userId, @Param("apiId") Long apiId);

    /**
     * 统计用户今日对指定API的免费调用次数
     * <p>type=0表示免费调用，按当天日期统计，用于每日免费调用次数限制。</p>
     *
     * @param userId 用户ID
     * @param apiId  API接口ID
     * @return 今日免费调用次数
     */
    @Select("SELECT COUNT(*) FROM api_test_record WHERE user_id = #{userId} AND api_id = #{apiId} AND type = 0 AND DATE(create_time) = CURDATE()")
    int countTodayCallsByUserIdAndApiId(@Param("userId") Long userId, @Param("apiId") Long apiId);

}
