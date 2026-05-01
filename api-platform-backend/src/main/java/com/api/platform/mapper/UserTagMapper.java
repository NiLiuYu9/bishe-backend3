package com.api.platform.mapper;

import com.api.platform.entity.UserTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户标签Mapper接口
 * <p>核心职责：提供用户标签关联表（user_tag）的基础CRUD操作，
 * 并支持按用户ID查询标签、删除标签、检查标签是否存在等操作。</p>
 */
public interface UserTagMapper extends BaseMapper<UserTag> {

    /**
     * 根据用户ID查询关联的标签名称列表
     * <p>SQL定义在Mapper XML中，关联tag表获取标签名称。</p>
     *
     * @param userId 用户ID
     * @return 标签名称列表
     */
    List<String> selectTagNamesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID删除所有标签关联
     * <p>用户更新标签时，先删除旧关联再批量插入新关联。</p>
     *
     * @param userId 用户ID
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和标签名称删除指定标签关联
     * <p>用于移除用户的单个标签。</p>
     *
     * @param userId  用户ID
     * @param tagName 标签名称
     */
    void deleteByUserIdAndTagName(@Param("userId") Long userId, @Param("tagName") String tagName);

    /**
     * 检查用户是否已关联指定标签
     * <p>用于标签去重，避免重复关联同一标签。</p>
     *
     * @param userId  用户ID
     * @param tagName 标签名称
     * @return 存在时返回非null，不存在时返回null
     */
    Integer existsByUserIdAndTagName(@Param("userId") Long userId, @Param("tagName") String tagName);

}
