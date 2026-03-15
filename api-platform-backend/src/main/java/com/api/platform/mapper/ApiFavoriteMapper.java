package com.api.platform.mapper;

import com.api.platform.entity.ApiFavorite;
import com.api.platform.vo.ApiVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApiFavoriteMapper extends BaseMapper<ApiFavorite> {

    @Select("SELECT api_id FROM api_favorite WHERE user_id = #{userId}")
    List<Long> selectUserFavoriteApiIds(@Param("userId") Long userId);

    IPage<ApiVO> selectUserFavoriteApis(Page<ApiVO> page, @Param("userId") Long userId);

}
