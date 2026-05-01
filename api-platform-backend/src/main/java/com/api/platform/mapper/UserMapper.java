package com.api.platform.mapper;

import com.api.platform.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 * <p>核心职责：提供用户表（user）的基础CRUD操作，
 * 继承MyBatis-Plus的BaseMapper，自动拥有单表增删改查能力。</p>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
