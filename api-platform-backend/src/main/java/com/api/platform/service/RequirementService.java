package com.api.platform.service;

import com.api.platform.dto.RequirementApplyDTO;
import com.api.platform.dto.RequirementApplicantSelectDTO;
import com.api.platform.dto.RequirementCreateDTO;
import com.api.platform.dto.RequirementDeliverDTO;
import com.api.platform.dto.RequirementQueryDTO;
import com.api.platform.entity.Requirement;
import com.api.platform.vo.RequirementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 需求服务接口 —— 定义需求相关的核心业务操作
 *
 * 所属业务模块：需求管理模块
 * 包括需求发布、申请、接单、交付、确认交付、完成、取消等完整生命周期管理
 * 实现类为 RequirementServiceImpl
 */
public interface RequirementService extends IService<Requirement> {

    /**
     * 分页查询需求列表
     *
     * 已登录用户可查看是否已申请该需求
     *
     * @param queryDTO       查询条件（关键词、状态、分页参数）
     * @param currentUserId  当前登录用户 ID，为 null 则不查询申请状态
     * @return IPage<RequirementVO> 分页需求信息列表
     */
    IPage<RequirementVO> pageList(RequirementQueryDTO queryDTO, Long currentUserId);

    /**
     * 根据ID获取需求详情
     *
     * @param id 需求 ID
     * @return RequirementVO 需求详细信息
     */
    RequirementVO getDetailById(Long id);

    /**
     * 发布需求
     *
     * @param userId    发布者用户 ID
     * @param createDTO 需求创建表单（标题、描述、预算、截止日期、标签等）
     * @return RequirementVO 创建后的需求信息
     */
    RequirementVO create(Long userId, RequirementCreateDTO createDTO);

    /**
     * 更新需求信息
     *
     * 仅需求发布者可更新，且需求需处于可编辑状态
     *
     * @param userId    操作者用户 ID，需为需求发布者
     * @param id        需求 ID
     * @param updateDTO 需求更新表单
     * @return RequirementVO 更新后的需求信息
     */
    RequirementVO update(Long userId, Long id, RequirementCreateDTO updateDTO);

    /**
     * 删除需求
     *
     * 仅需求发布者可删除，且需求需处于可删除状态
     *
     * @param userId 操作者用户 ID，需为需求发布者
     * @param id     需求 ID
     */
    void delete(Long userId, Long id);

    /**
     * 申请接单
     *
     * 开发者对需求提出接单申请，附带申请说明和报价
     *
     * @param userId        申请者用户 ID
     * @param requirementId 需求 ID
     * @param applyDTO      申请表单（申请说明、报价）
     */
    void apply(Long userId, Long requirementId, RequirementApplyDTO applyDTO);

    /**
     * 选择接单开发者
     *
     * 需求发布者从申请列表中选择一位开发者接单
     *
     * @param userId        操作者用户 ID，需为需求发布者
     * @param requirementId 需求 ID
     * @param selectDTO     选择表单（被选中的申请者 ID）
     */
    void selectApplicant(Long userId, Long requirementId, RequirementApplicantSelectDTO selectDTO);

    /**
     * 撤回接单申请
     *
     * 开发者撤回自己已提交的接单申请
     *
     * @param userId        申请者用户 ID
     * @param requirementId 需求 ID
     */
    void withdrawApply(Long userId, Long requirementId);

    /**
     * 完成需求
     *
     * 需求发布者确认需求已完成
     *
     * @param userId        操作者用户 ID，需为需求发布者
     * @param requirementId 需求 ID
     */
    void complete(Long userId, Long requirementId);

    /**
     * 取消需求
     *
     * 需求发布者取消需求
     *
     * @param userId        操作者用户 ID，需为需求发布者
     * @param requirementId 需求 ID
     */
    void cancel(Long userId, Long requirementId);

    /**
     * 交付需求
     *
     * 接单开发者提交交付成果
     *
     * @param userId        交付者用户 ID，需为被选中的开发者
     * @param requirementId 需求 ID
     * @param deliverDTO    交付表单（交付说明、交付附件等）
     */
    void deliver(Long userId, Long requirementId, RequirementDeliverDTO deliverDTO);

    /**
     * 确认交付
     *
     * 需求发布者确认开发者的交付成果
     *
     * @param userId        操作者用户 ID，需为需求发布者
     * @param requirementId 需求 ID
     */
    void confirmDelivery(Long userId, Long requirementId);

    /**
     * 查询我发布的需求
     *
     * @param userId   发布者用户 ID
     * @param queryDTO 查询条件（状态、分页参数）
     * @return IPage<RequirementVO> 分页需求信息列表
     */
    IPage<RequirementVO> getMyPublished(Long userId, RequirementQueryDTO queryDTO);

    /**
     * 查询我申请接单的需求
     *
     * @param userId   申请者用户 ID
     * @param queryDTO 查询条件（状态、分页参数）
     * @return IPage<RequirementVO> 分页需求信息列表
     */
    IPage<RequirementVO> getMyApplied(Long userId, RequirementQueryDTO queryDTO);

    /**
     * 更新需求状态（内部调用）
     *
     * @param id     需求 ID
     * @param status 目标状态
     */
    void updateStatus(Long id, String status);

}
