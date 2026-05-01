package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.ApiReviewCreateDTO;
import com.api.platform.dto.ApiReviewPublisherReplyDTO;
import com.api.platform.dto.ApiReviewQueryDTO;
import com.api.platform.dto.ApiReviewUpdateDTO;
import com.api.platform.dto.ApiReviewUserReplyDTO;
import com.api.platform.service.ApiReviewService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.ApiReviewVO;
import com.api.platform.vo.PageResultVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * API评价控制器 —— 处理API评价的创建、回复、修改、删除及查询请求
 *
 * 路由前缀：/review
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 评价体系采用嵌套回复模型：
 * - parentId 指向父评价
 * - replyType: 0=评价, 1=发布者回复, 2=用户追问
 */
@RestController
@RequestMapping("/review")
public class ApiReviewController {

    @Autowired
    private ApiReviewService apiReviewService;

    /**
     * 创建评价
     *
     * 用户对已购买的API进行评价，需关联有效订单
     *
     * @param createDTO 评价创建表单（apiId、orderId、评分1-5、评价内容）
     * @param session   HttpSession，用于获取当前登录用户ID
     * @return Result&lt;ApiReviewVO&gt; 创建成功的评价信息
     */
    @PostMapping("/create")
    public Result<ApiReviewVO> createReview(@Validated @RequestBody ApiReviewCreateDTO createDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        ApiReviewVO vo = apiReviewService.createReview(userId, createDTO);
        return Result.success(vo);
    }

    @PostMapping("/publisher/reply")
    public Result<Void> publisherReplyReview(@Validated @RequestBody ApiReviewPublisherReplyDTO replyDTO,
                                               HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiReviewService.publisherReplyReview(userId, replyDTO);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> updateReview(@Validated @RequestBody ApiReviewUpdateDTO updateDTO,
                                      HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiReviewService.updateReview(userId, updateDTO);
        return Result.success();
    }

    @PostMapping("/delete/{reviewId}")
    public Result<Void> deleteReview(@PathVariable Long reviewId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiReviewService.deleteReview(userId, reviewId);
        return Result.success();
    }

    @PostMapping("/user/reply")
    public Result<Void> userReplyReview(@Validated @RequestBody ApiReviewUserReplyDTO replyDTO,
                                         HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiReviewService.userReplyReview(userId, replyDTO);
        return Result.success();
    }

    /**
     * 获取指定API的评价列表
     *
     * 支持分页和是否包含回复记录
     *
     * @param apiId          API ID
     * @param pageNum        页码，默认1
     * @param pageSize       每页数量，默认10
     * @param includeReplies 是否包含回复，默认true
     * @return Result&lt;PageResultVO&lt;ApiReviewVO&gt;&gt; 分页的评价列表
     */
    @GetMapping("/list/{apiId}")
    public Result<PageResultVO<ApiReviewVO>> getApiReviews(
            @PathVariable Long apiId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean includeReplies) {
        ApiReviewQueryDTO queryDTO = new ApiReviewQueryDTO();
        queryDTO.setApiId(apiId);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setIncludeReplies(includeReplies);
        IPage<ApiReviewVO> page = apiReviewService.getApiReviews(queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/my-reviews")
    public Result<PageResultVO<ApiReviewVO>> getMyReviews(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        ApiReviewQueryDTO queryDTO = new ApiReviewQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        IPage<ApiReviewVO> page = apiReviewService.getMyReviews(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    /**
     * 获取评价详情
     *
     * @param reviewId 评价ID
     * @return Result&lt;ApiReviewVO&gt; 评价详情（含回复列表）
     */
    @GetMapping("/detail/{reviewId}")
    public Result<ApiReviewVO> getReviewDetail(@PathVariable Long reviewId) {
        ApiReviewVO vo = apiReviewService.getReviewDetail(reviewId);
        if (vo == null) {
            return Result.failed("评价不存在");
        }
        return Result.success(vo);
    }

}
