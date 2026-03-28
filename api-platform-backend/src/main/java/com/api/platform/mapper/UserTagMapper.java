package com.api.platform.mapper;

import com.api.platform.entity.UserTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserTagMapper extends BaseMapper<UserTag> {

    List<String> selectTagNamesByUserId(@Param("userId") Long userId);

    void deleteByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndTagName(@Param("userId") Long userId, @Param("tagName") String tagName);

    Integer existsByUserIdAndTagName(@Param("userId") Long userId, @Param("tagName") String tagName);

}
