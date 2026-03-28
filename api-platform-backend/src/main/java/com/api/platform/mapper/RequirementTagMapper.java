package com.api.platform.mapper;

import com.api.platform.entity.RequirementTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RequirementTagMapper extends BaseMapper<RequirementTag> {

    List<String> selectTagNamesByRequirementId(@Param("requirementId") Long requirementId);

    void deleteByRequirementId(@Param("requirementId") Long requirementId);

    List<Long> selectRequirementIdsByTagName(@Param("tagName") String tagName);

    List<Map<String, Object>> selectTagsByRequirementIds(@Param("requirementIds") List<Long> requirementIds);

}
