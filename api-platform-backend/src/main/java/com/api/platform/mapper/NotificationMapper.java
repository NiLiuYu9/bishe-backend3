package com.api.platform.mapper;

import com.api.platform.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知Mapper接口
 * <p>核心职责：提供通知表（notification）的基础CRUD操作，
 * 继承MyBatis-Plus的BaseMapper，自动拥有单表增删改查能力。</p>
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
