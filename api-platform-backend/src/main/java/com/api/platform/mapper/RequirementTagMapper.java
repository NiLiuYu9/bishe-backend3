package com.api.platform.mapper;

import com.api.platform.entity.RequirementTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 需求标签Mapper接口
 * <p>核心职责：提供需求标签关联表（requirement_tag）的基础CRUD操作，
 * 并支持按需求ID查询标签名称、按标签名称反查需求ID等关联查询。</p>
 */
public interface RequirementTagMapper extends BaseMapper<RequirementTag> {

    /**
     * 根据需求ID查询关联的标签名称列表
     * <p>SQL定义在Mapper XML中，关联tag表获取标签名称。</p>
     *
     * @param requirementId 需求ID
     * @return 标签名称列表
     */
    List<String> selectTagNamesByRequirementId(@Param("requirementId") Long requirementId);

    /**
     * 根据需求ID删除所有标签关联
     * <p>需求更新标签时，先删除旧关联再批量插入新关联。</p>
     *
     * @param requirementId 需求ID
     */
    void deleteByRequirementId(@Param("requirementId") Long requirementId);

    /**
     * 根据标签名称查询关联的需求ID列表
     * <p>用于按标签筛选需求的场景。</p>
     *
     * @param tagName 标签名称
     * @return 需求ID列表
     */
    List<Long> selectRequirementIdsByTagName(@Param("tagName") String tagName);

    /**
     * 批量查询多个需求的标签信息
     * <p>用于需求列表页展示标签，避免N+1查询问题。</p>
     *
     * @param requirementIds 需求ID列表
     * @return 每个需求的标签信息（包含requirementId和tagName）
     */
    List<Map<String, Object>> selectTagsByRequirementIds(@Param("requirementIds") List<Long> requirementIds);

}
