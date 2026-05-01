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

/**
 * API收藏Mapper接口
 * <p>核心职责：提供API收藏表（api_favorite）的基础CRUD操作，
 * 并支持查询用户收藏的API ID列表及分页查询收藏的API详情。</p>
 */
@Mapper
public interface ApiFavoriteMapper extends BaseMapper<ApiFavorite> {

    /**
     * 查询用户收藏的所有API ID列表
     * <p>用于判断用户是否已收藏某API（前端收藏按钮状态）。</p>
     *
     * @param userId 用户ID
     * @return 用户收藏的API ID列表
     */
    @Select("SELECT api_id FROM api_favorite WHERE user_id = #{userId}")
    List<Long> selectUserFavoriteApiIds(@Param("userId") Long userId);

    /**
     * 分页查询用户收藏的API详情
     * <p>SQL定义在Mapper XML中，关联api_info表获取完整API信息。</p>
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 包含API详情的分页结果
     */
    IPage<ApiVO> selectUserFavoriteApis(Page<ApiVO> page, @Param("userId") Long userId);

}
