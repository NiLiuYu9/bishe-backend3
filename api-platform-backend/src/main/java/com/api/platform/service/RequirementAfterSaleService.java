package com.api.platform.service;

import com.api.platform.dto.AfterSaleCreateDTO;
import com.api.platform.dto.AfterSaleDecideDTO;
import com.api.platform.dto.AfterSaleQueryDTO;
import com.api.platform.entity.RequirementAfterSale;
import com.api.platform.vo.RequirementAfterSaleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 售后服务接口 —— 定义需求售后相关的核心业务操作
 *
 * 所属业务模块：售后管理模块
 * 包括售后申请、管理员裁定、售后详情查询、售后列表查询等功能
 * 实现类为 RequirementAfterSaleServiceImpl
 */
public interface RequirementAfterSaleService extends IService<RequirementAfterSale> {

    /**
     * 创建售后申请
     *
     * 需求发布者或开发者对已完成的需求发起售后申请
     *
     * @param applicantId 申请者用户 ID
     * @param createDTO   售后创建表单（需求 ID、售后原因、申请说明）
     * @return RequirementAfterSaleVO 创建后的售后信息
     */
    RequirementAfterSaleVO createAfterSale(Long applicantId, AfterSaleCreateDTO createDTO);

    /**
     * 管理员裁定售后
     *
     * 管理员审核售后申请，裁定同意或拒绝
     *
     * @param adminId      管理员用户 ID
     * @param afterSaleId  售后 ID
     * @param decideDTO    裁定表单（裁定结果、裁定说明）
     */
    void decideAfterSale(Long adminId, Long afterSaleId, AfterSaleDecideDTO decideDTO);

    /**
     * 根据ID获取售后详情
     *
     * @param afterSaleId 售后 ID
     * @return RequirementAfterSaleVO 售后详细信息
     */
    RequirementAfterSaleVO getDetailById(Long afterSaleId);

    /**
     * 根据ID获取售后详情（带权限校验）
     *
     * 仅售后相关方（申请者、开发者、管理员）可查看
     *
     * @param afterSaleId 售后 ID
     * @param userId      当前用户 ID
     * @param isAdmin     是否为管理员
     * @return RequirementAfterSaleVO 售后详细信息
     */
    RequirementAfterSaleVO getDetailByIdWithPermission(Long afterSaleId, Long userId, boolean isAdmin);

    /**
     * 分页查询售后列表（管理端）
     *
     * @param queryDTO 查询条件（状态、分页参数）
     * @return IPage<RequirementAfterSaleVO> 分页售后列表
     */
    IPage<RequirementAfterSaleVO> pageList(AfterSaleQueryDTO queryDTO);

    /**
     * 查询我发起的售后列表
     *
     * @param userId   申请者用户 ID
     * @param queryDTO 查询条件（状态、分页参数）
     * @return IPage<RequirementAfterSaleVO> 分页售后列表
     */
    IPage<RequirementAfterSaleVO> getMyAfterSales(Long userId, AfterSaleQueryDTO queryDTO);

    /**
     * 查询我作为开发者的售后列表
     *
     * @param userId   开发者用户 ID
     * @param queryDTO 查询条件（状态、分页参数）
     * @return IPage<RequirementAfterSaleVO> 分页售后列表
     */
    IPage<RequirementAfterSaleVO> getDeveloperAfterSales(Long userId, AfterSaleQueryDTO queryDTO);

}
