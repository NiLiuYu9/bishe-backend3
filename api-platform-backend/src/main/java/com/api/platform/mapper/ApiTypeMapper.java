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

@Mapper
public interface ApiTypeMapper extends BaseMapper<ApiType> {

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

    @InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
    @Select("SELECT * FROM api_type WHERE id = #{id}")
    ApiType selectByIdIgnoreLogicDelete(@Param("id") Long id);

    @InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
    @Update("UPDATE api_type SET deleted = #{deleted} WHERE id = #{id}")
    int updateDeletedById(@Param("id") Long id, @Param("deleted") Integer deleted);

    @Select("SELECT COUNT(*) FROM api_info WHERE type_id = #{typeId} AND status = 'approved' AND deleted = 0")
    int countApisByTypeId(@Param("typeId") Long typeId);
}
