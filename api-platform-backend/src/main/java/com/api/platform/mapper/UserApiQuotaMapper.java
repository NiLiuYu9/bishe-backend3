package com.api.platform.mapper;

import com.api.platform.entity.UserApiQuota;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserApiQuotaMapper extends BaseMapper<UserApiQuota> {

    @Update("UPDATE user_api_quota SET used_count = used_count + 1, remaining_count = remaining_count - 1 " +
            "WHERE user_id = #{userId} AND api_id = #{apiId} AND remaining_count > 0")
    int deductQuota(@Param("userId") Long userId, @Param("apiId") Long apiId);

}
