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

@RestController
@RequestMapping("/review")
public class ApiReviewController {

    @Autowired
    private ApiReviewService apiReviewService;

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

    @GetMapping("/detail/{reviewId}")
    public Result<ApiReviewVO> getReviewDetail(@PathVariable Long reviewId) {
        ApiReviewVO vo = apiReviewService.getReviewDetail(reviewId);
        if (vo == null) {
            return Result.failed("评价不存在");
        }
        return Result.success(vo);
    }

}
