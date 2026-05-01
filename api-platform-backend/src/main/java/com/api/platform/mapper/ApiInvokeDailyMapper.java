package com.api.platform.mapper;

import com.api.platform.entity.ApiInvokeDaily;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * API每日调用统计Mapper接口
 * <p>核心职责：提供API每日调用统计表（api_invoke_daily）的基础CRUD操作，
 * 并支持原子性地递增某天的调用计数（总调用、成功、失败）。</p>
 */
@Mapper
public interface ApiInvokeDailyMapper extends BaseMapper<ApiInvokeDaily> {

    /**
     * 原子递增指定日期的调用统计
     * <p>在统计同步任务中调用，将内存中累计的调用数据批量更新到数据库。
     * 使用SQL原子操作保证并发安全。</p>
     *
     * @param apiId        API接口ID
     * @param callerId     调用者用户ID
     * @param statDate     统计日期
     * @param totalCount   本次新增的总调用次数
     * @param successCount 本次新增的成功次数
     * @param failCount    本次新增的失败次数
     * @return 影响行数（0表示记录不存在）
     */
    @Update("UPDATE api_invoke_daily SET total_count = total_count + #{totalCount}, " +
            "success_count = success_count + #{successCount}, fail_count = fail_count + #{failCount} " +
            "WHERE api_id = #{apiId} AND caller_id = #{callerId} AND stat_date = #{statDate}")
    int incrementCounts(@Param("apiId") Long apiId, @Param("callerId") Long callerId, 
                        @Param("statDate") java.time.LocalDate statDate,
                        @Param("totalCount") long totalCount, @Param("successCount") long successCount, 
                        @Param("failCount") long failCount);

}
