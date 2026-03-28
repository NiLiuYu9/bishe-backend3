package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.PageQueryDTO;
import com.api.platform.service.MatchingService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.PageResultVO;
import com.api.platform.vo.RequirementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/matching")
@Validated
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @GetMapping("/recommend")
    public Result<PageResultVO<RequirementVO>> getRecommendedRequirements(@Validated PageQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<RequirementVO> page = matchingService.getRecommendedRequirements(userId, queryDTO);
        PageResultVO<RequirementVO> result = new PageResultVO<>();
        result.setList(page.getRecords());
        result.setTotal(page.getTotal());
        return Result.success(result);
    }

}
