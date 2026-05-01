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

/**
 * 评价服务接口 —— 定义API评价相关的核心业务操作
 *
 * 所属业务模块：评价管理模块
 * 包括创建评价、修改评价、删除评价、发布者回复、用户回复、评价查询等功能
 * 实现类为 ApiReviewServiceImpl
 */
public interface ApiReviewService extends IService<ApiReview> {

    /**
     * 创建评价
     *
     * 用户对已购买的API进行评价
     *
     * @param userId     评价者用户 ID
     * @param createDTO  评价创建表单（API ID、评分、评价内容）
     * @return ApiReviewVO 创建后的评价信息
     */
    ApiReviewVO createReview(Long userId, ApiReviewCreateDTO createDTO);

    /**
     * 发布者回复评价
     *
     * API发布者对用户的评价进行回复
     *
     * @param userId    回复者用户 ID，需为API发布者
     * @param replyDTO  回复表单（评价 ID、回复内容）
     */
    void publisherReplyReview(Long userId, ApiReviewPublisherReplyDTO replyDTO);

    /**
     * 修改评价
     *
     * 评价者修改自己已发布的评价
     *
     * @param userId    评价者用户 ID
     * @param updateDTO 评价修改表单（评价 ID、评分、评价内容）
     */
    void updateReview(Long userId, ApiReviewUpdateDTO updateDTO);

    /**
     * 删除评价
     *
     * 评价者删除自己已发布的评价
     *
     * @param userId    评价者用户 ID
     * @param reviewId  评价 ID
     */
    void deleteReview(Long userId, Long reviewId);

    /**
     * 用户回复评价
     *
     * 评价者对发布者的回复进行再次回复
     *
     * @param userId    回复者用户 ID，需为评价的原始发布者
     * @param replyDTO  回复表单（评价 ID、回复内容）
     */
    void userReplyReview(Long userId, ApiReviewUserReplyDTO replyDTO);

    /**
     * 分页查询API评价列表
     *
     * 按API ID、评分等条件筛选
     *
     * @param queryDTO 查询条件（API ID、评分、分页参数）
     * @return IPage<ApiReviewVO> 分页评价信息列表
     */
    IPage<ApiReviewVO> getApiReviews(ApiReviewQueryDTO queryDTO);

    /**
     * 查询我发布的评价列表
     *
     * @param userId   评价者用户 ID
     * @param queryDTO 查询条件（分页参数）
     * @return IPage<ApiReviewVO> 分页评价信息列表
     */
    IPage<ApiReviewVO> getMyReviews(Long userId, ApiReviewQueryDTO queryDTO);

    /**
     * 获取评价详情
     *
     * @param reviewId 评价 ID
     * @return ApiReviewVO 评价详细信息（含回复列表）
     */
    ApiReviewVO getReviewDetail(Long reviewId);

}
