package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.constants.SessionConstants;
import com.api.platform.dto.RequirementApplyDTO;
import com.api.platform.dto.RequirementApplicantSelectDTO;
import com.api.platform.dto.RequirementCreateDTO;
import com.api.platform.dto.RequirementQueryDTO;
import com.api.platform.service.RequirementService;
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
    public Result<PageResultVO<RequirementVO>> getList(RequirementQueryDTO queryDTO) {
        IPage<RequirementVO> page = requirementService.pageList(queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/detail/{id}")
    public Result<RequirementVO> getDetail(@PathVariable Long id) {
        RequirementVO vo = requirementService.getDetailById(id);
        if (vo == null) {
            return Result.failed("需求不存在");
        }
        return Result.success(vo);
    }

    @PostMapping("/create")
    public Result<RequirementVO> create(@Validated @RequestBody RequirementCreateDTO createDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        RequirementVO vo = requirementService.create(userId, createDTO);
        return Result.success(vo);
    }

    @PutMapping("/update/{id}")
    public Result<RequirementVO> update(@PathVariable Long id, @Validated @RequestBody RequirementCreateDTO updateDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        RequirementVO vo = requirementService.update(userId, id, updateDTO);
        return Result.success(vo);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        requirementService.delete(userId, id);
        return Result.success();
    }

    @PostMapping("/apply/{id}")
    public Result<Void> apply(@PathVariable Long id, @Validated @RequestBody RequirementApplyDTO applyDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        requirementService.apply(userId, id, applyDTO);
        return Result.success();
    }

    @PostMapping("/withdraw-apply/{id}")
    public Result<Void> withdrawApply(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        requirementService.withdrawApply(userId, id);
        return Result.success();
    }

    @PostMapping("/select-applicant/{id}")
    public Result<Void> selectApplicant(@PathVariable Long id, @Validated @RequestBody RequirementApplicantSelectDTO selectDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        requirementService.selectApplicant(userId, id, selectDTO);
        return Result.success();
    }

    @PostMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        requirementService.complete(userId, id);
        return Result.success();
    }

    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        requirementService.cancel(userId, id);
        return Result.success();
    }

    @GetMapping("/my-requirements")
    public Result<PageResultVO<RequirementVO>> getMyRequirements(RequirementQueryDTO queryDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
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
