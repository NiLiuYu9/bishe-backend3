package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.RequirementApplyDTO;
import com.api.platform.dto.RequirementApplicantSelectDTO;
import com.api.platform.dto.RequirementCreateDTO;
import com.api.platform.dto.RequirementDeliverDTO;
import com.api.platform.dto.RequirementQueryDTO;
import com.api.platform.service.RequirementService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.PageResultVO;
import com.api.platform.vo.RequirementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/requirement")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    @GetMapping("/list")
    public Result<PageResultVO<RequirementVO>> getList(RequirementQueryDTO queryDTO, HttpSession session) {
        Long currentUserId = SessionUtils.getCurrentUserIdOrNull(session);
        IPage<RequirementVO> page = requirementService.pageList(queryDTO, currentUserId);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    /**
     * 获取需求详情
     *
     * @param id 需求ID
     * @return Result&lt;RequirementVO&gt; 需求详情（含申请人列表、标签列表）
     */
    @GetMapping("/detail/{id}")
    public Result<RequirementVO> getDetail(@PathVariable Long id) {
        RequirementVO vo = requirementService.getDetailById(id);
        if (vo == null) {
            return Result.failed("需求不存在");
        }
        return Result.success(vo);
    }

    /**
     * 发布需求
     *
     * 需求创建后状态默认为 open，开发者可申请接单
     *
     * @param createDTO 需求创建表单（标题、描述、预算、截止日期、技术标签）
     * @param session   HttpSession，用于获取当前登录用户ID
     * @return Result&lt;RequirementVO&gt; 创建成功的需求信息
     */
    @PostMapping("/create")
    public Result<RequirementVO> create(@Validated @RequestBody RequirementCreateDTO createDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        RequirementVO vo = requirementService.create(userId, createDTO);
        return Result.success(vo);
    }

    /**
     * 更新需求信息
     *
     * 仅需求发布者可更新，且需求状态必须为 open
     *
     * @param id        需求ID
     * @param updateDTO 需求更新表单（与创建表单字段相同）
     * @param session   HttpSession，用于获取当前登录用户ID
     * @return Result&lt;RequirementVO&gt; 更新后的需求信息
     */
    @PutMapping("/update/{id}")
    public Result<RequirementVO> update(@PathVariable Long id, @Validated @RequestBody RequirementCreateDTO updateDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        RequirementVO vo = requirementService.update(userId, id, updateDTO);
        return Result.success(vo);
    }

    /**
     * 删除需求
     *
     * 仅需求发布者可删除，且需求状态必须为 open
     *
     * @param id      需求ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 删除成功无返回数据
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.delete(userId, id);
        return Result.success();
    }

    @PostMapping("/apply/{id}")
    public Result<Void> apply(@PathVariable Long id, @Validated @RequestBody RequirementApplyDTO applyDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.apply(userId, id, applyDTO);
        return Result.success();
    }

    @PostMapping("/withdraw-apply/{id}")
    public Result<Void> withdrawApply(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.withdrawApply(userId, id);
        return Result.success();
    }

    @PostMapping("/select-applicant/{id}")
    public Result<Void> selectApplicant(@PathVariable Long id, @Validated @RequestBody RequirementApplicantSelectDTO selectDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.selectApplicant(userId, id, selectDTO);
        return Result.success();
    }

    /**
     * 完成需求（需求方确认完成）
     *
     * 需求发布者确认需求已完成，需求状态变为 completed
     *
     * @param id      需求ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 确认成功无返回数据
     */
    @PostMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.complete(userId, id);
        return Result.success();
    }

    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.cancel(userId, id);
        return Result.success();
    }

    @PostMapping("/deliver/{id}")
    public Result<Void> deliver(@PathVariable Long id, @Validated @RequestBody RequirementDeliverDTO deliverDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.deliver(userId, id, deliverDTO);
        return Result.success();
    }

    @PostMapping("/confirm-delivery/{id}")
    public Result<Void> confirmDelivery(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        requirementService.confirmDelivery(userId, id);
        return Result.success();
    }

    /**
     * 获取当前用户相关的需求列表
     *
     * 当 status=applied 时返回用户申请过的需求，否则返回用户发布的需求
     *
     * @param queryDTO 查询条件（status=applied 时查申请列表，其他查发布列表）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;RequirementVO&gt;&gt; 分页的需求列表
     */
    @GetMapping("/my-requirements")
    public Result<PageResultVO<RequirementVO>> getMyRequirements(RequirementQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        String type = queryDTO.getStatus();
        IPage<RequirementVO> page;
        if ("applied".equals(type)) {
            queryDTO.setStatus(null);
            page = requirementService.getMyApplied(userId, queryDTO);
        } else {
            page = requirementService.getMyPublished(userId, queryDTO);
        }
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

}
