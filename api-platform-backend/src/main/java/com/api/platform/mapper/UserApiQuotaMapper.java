package com.api.platform.mapper;

import com.api.platform.entity.UserApiQuota;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户API配额Mapper接口
 * <p>核心职责：提供用户API配额表（user_api_quota）的基础CRUD操作，
 * 并支持原子性地扣减配额（剩余次数-1，已用次数+1）。</p>
 */
@Mapper
public interface UserApiQuotaMapper extends BaseMapper<UserApiQuota> {

    /**
     * 原子扣减用户配额
     * <p>使用乐观锁条件remaining_count > 0确保不会超扣，
     * 只有剩余次数大于0时才会成功扣减。</p>
     *
     * @param userId 用户ID
     * @param apiId  API接口ID
     * @return 影响行数（1-扣减成功，0-配额不足或记录不存在）
     */
    @Update("UPDATE user_api_quota SET used_count = used_count + 1, remaining_count = remaining_count - 1 " +
            "WHERE user_id = #{userId} AND api_id = #{apiId} AND remaining_count > 0")
    int deductQuota(@Param("userId") Long userId, @Param("apiId") Long apiId);

}
