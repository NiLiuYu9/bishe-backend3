package com.api.platform.mapper;

import com.api.platform.entity.ApiInvokeDaily;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ApiInvokeDailyMapper extends BaseMapper<ApiInvokeDaily> {

    @Update("UPDATE api_invoke_daily SET total_count = total_count + #{totalCount}, " +
            "success_count = success_count + #{successCount}, fail_count = fail_count + #{failCount} " +
            "WHERE api_id = #{apiId} AND caller_id = #{callerId} AND stat_date = #{statDate}")
    int incrementCounts(@Param("apiId") Long apiId, @Param("callerId") Long callerId, 
                        @Param("statDate") java.time.LocalDate statDate,
                        @Param("totalCount") long totalCount, @Param("successCount") long successCount, 
                        @Param("failCount") long failCount);

}
