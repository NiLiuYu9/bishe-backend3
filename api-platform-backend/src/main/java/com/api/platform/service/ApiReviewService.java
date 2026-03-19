package com.api.platform.service;

import com.api.platform.dto.ApiReviewCreateDTO;
import com.api.platform.dto.ApiReviewPublisherReplyDTO;
import com.api.platform.dto.ApiReviewQueryDTO;
import com.api.platform.dto.ApiReviewUpdateDTO;
import com.api.platform.dto.ApiReviewUserReplyDTO;
import com.api.platform.entity.ApiReview;
import com.api.platform.vo.ApiReviewVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ApiReviewService extends IService<ApiReview> {

    ApiReviewVO createReview(Long userId, ApiReviewCreateDTO createDTO);

    void publisherReplyReview(Long userId, ApiReviewPublisherReplyDTO replyDTO);

    void updateReview(Long userId, ApiReviewUpdateDTO updateDTO);

    void deleteReview(Long userId, Long reviewId);

    void userReplyReview(Long userId, ApiReviewUserReplyDTO replyDTO);

    IPage<ApiReviewVO> getApiReviews(ApiReviewQueryDTO queryDTO);

    IPage<ApiReviewVO> getMyReviews(Long userId, ApiReviewQueryDTO queryDTO);

    ApiReviewVO getReviewDetail(Long reviewId);

}
