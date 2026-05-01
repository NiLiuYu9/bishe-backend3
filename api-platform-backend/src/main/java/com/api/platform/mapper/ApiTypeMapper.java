package com.api.platform.mapper;

import com.api.platform.entity.ApiType;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * API分类Mapper接口
 * <p>核心职责：提供API分类表（api_type）的基础CRUD操作，
 * 并支持忽略逻辑删除的查询/更新（管理后台需查看已删除分类），
 * 以及按分类统计已上架API数量。</p>
 */
@Mapper
public interface ApiTypeMapper extends BaseMapper<ApiType> {

    /**
     * 分页查询分类列表（忽略逻辑删除）
     * <p>管理后台使用，需查看所有分类（含已删除），支持按状态和关键词筛选。
     * 使用@InterceptorIgnore跳过MyBatis-Plus的逻辑删除拦截器。</p>
     *
     * @param page    分页参数
     * @param status  状态筛选：active-未删除，inactive-已删除，空-全部
     * @param keyword 名称关键词（模糊匹配）
     * @return 分类分页结果
     */
    @InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
    @Select("<script>" +
            "SELECT * FROM api_type WHERE 1=1 " +
            "<if test='status != null and status != \"\"'>" +
            "<if test='status == \"active\"'>" +
            " AND deleted = 0 " +
            "</if>" +
            "<if test='status == \"inactive\"'>" +
            " AND deleted = 1 " +
            "</if>" +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            " AND name LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<ApiType> selectPageIgnoreLogicDelete(Page<ApiType> page, @Param("status") String status, @Param("keyword") String keyword);

    /**
     * 根据ID查询分类（忽略逻辑删除）
     * <p>管理后台编辑分类时使用，可查看已删除的分类详情。</p>
     *
     * @param id 分类ID
     * @return 分类实体
     */
    @InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
    @Select("SELECT * FROM api_type WHERE id = #{id}")
    ApiType selectByIdIgnoreLogicDelete(@Param("id") Long id);

    /**
     * 更新分类的删除状态（逻辑删除/恢复）
     * <p>管理后台使用，通过修改deleted字段实现软删除和恢复。</p>
     *
     * @param id      分类ID
     * @param deleted 删除标记：0-正常，1-已删除
     * @return 影响行数
     */
    @InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
    @Update("UPDATE api_type SET deleted = #{deleted} WHERE id = #{id}")
    int updateDeletedById(@Param("id") Long id, @Param("deleted") Integer deleted);

    /**
     * 更新分类信息（忽略逻辑删除）
     * <p>管理后台编辑已删除分类时使用。</p>
     *
     * @param apiType 包含更新字段的分类实体
     * @return 影响行数
     */
    @InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
    @Update("<script>" +
            "UPDATE api_type SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}" +
            "</script>")
    int updateByIdIgnoreLogicDelete(ApiType apiType);

    /**
     * 统计指定分类下已审核通过的API数量
     * <p>用于分类删除前的引用检查，如果存在已上架API则不允许删除。</p>
     *
     * @param typeId 分类ID
     * @return 该分类下已审核通过的API数量
     */
    @Select("SELECT COUNT(*) FROM api_info WHERE type_id = #{typeId} AND status = 'approved' AND deleted = 0")
    int countApisByTypeId(@Param("typeId") Long typeId);
}
