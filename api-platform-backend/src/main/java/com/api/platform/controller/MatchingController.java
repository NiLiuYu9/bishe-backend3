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

/**
 * 智能推荐控制器 —— 处理基于用户标签的需求智能推荐请求
 *
 * 路由前缀：/matching
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 使用 Levenshtein 编辑距离算法计算用户标签与需求标签的匹配度，
 * 按匹配度从高到低排序返回推荐需求列表
 */
@RestController
@RequestMapping("/matching")
@Validated
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    /**
     * 获取为当前用户智能推荐的需求列表
     *
     * 根据用户技能标签与需求技术标签的匹配度排序推荐
     *
     * @param queryDTO 分页查询参数
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;RequirementVO&gt;&gt; 按匹配度排序的需求列表
     */
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
