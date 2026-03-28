package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.platform.constants.NotificationType;
import com.api.platform.constants.ReviewConstants;
import com.api.platform.dto.ApiReviewCreateDTO;
import com.api.platform.dto.ApiReviewPublisherReplyDTO;
import com.api.platform.dto.ApiReviewQueryDTO;
import com.api.platform.dto.ApiReviewUpdateDTO;
import com.api.platform.dto.ApiReviewUserReplyDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiReview;
import com.api.platform.entity.OrderInfo;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.ApiReviewMapper;
import com.api.platform.mapper.OrderInfoMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.ApiReviewService;
import com.api.platform.service.NotificationService;
import com.api.platform.vo.ApiReviewVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApiReviewServiceImpl extends ServiceImpl<ApiReviewMapper, ApiReview> implements ApiReviewService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiReviewVO createReview(Long userId, ApiReviewCreateDTO createDTO) {
        OrderInfo order = orderInfoMapper.selectById(createDTO.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权限评价该订单");
        }
        if (!"paid".equals(order.getStatus()) && !"completed".equals(order.getStatus())) {
            throw new BusinessException("订单未完成，无法评价");
        }
        Long existCount = this.baseMapper.selectCount(new LambdaQueryWrapper<ApiReview>()
                .eq(ApiReview::getOrderId, createDTO.getOrderId()));
        if (existCount > 0) {
            throw new BusinessException("该订单已评价，不能重复评价");
        }
        ApiReview review = new ApiReview();
        review.setOrderId(createDTO.getOrderId());
        review.setApiId(order.getApiId());
        review.setUserId(userId);
        review.setRating(createDTO.getRating());
        review.setContent(createDTO.getContent());
        review.setReplyType(ReviewConstants.REVIEW_TYPE_ORIGINAL);
        save(review);
        order.setRating(createDTO.getRating());
        orderInfoMapper.updateById(order);
        updateApiAverageRating(order.getApiId());
        ApiInfo apiInfo = apiInfoMapper.selectById(order.getApiId());
        if (apiInfo != null && !apiInfo.getUserId().equals(userId)) {
            notificationService.sendNotification(
                apiInfo.getUserId(),
                NotificationType.API_NEW_REVIEW.getCode(),
                "收到新评价",
                "您的API「" + apiInfo.getName() + "」收到了新评价",
                review.getId(),
                "api_review"
            );
        }
        return convertToVO(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userReplyReview(Long userId, ApiReviewUserReplyDTO replyDTO) {
        ApiReview publisherReply = getById(replyDTO.getReplyId());
        if (publisherReply == null) {
            throw new BusinessException("回复不存在");
        }
        if (!ReviewConstants.REVIEW_TYPE_PUBLISHER_REPLY.equals(publisherReply.getReplyType())) {
            throw new BusinessException("只能回复上架者的回复");
        }
        ApiReview originalReview = getById(publisherReply.getParentId());
        if (originalReview == null) {
            throw new BusinessException("原评论不存在");
        }
        if (!originalReview.getUserId().equals(userId)) {
            throw new BusinessException("无权限回复");
        }
        ApiReview userReply = new ApiReview();
        userReply.setApiId(originalReview.getApiId());
        userReply.setOrderId(originalReview.getOrderId());
        userReply.setUserId(userId);
        userReply.setRating(BigDecimal.ZERO);
        userReply.setContent(replyDTO.getContent());
        userReply.setParentId(originalReview.getId());
        userReply.setReplyType(ReviewConstants.REVIEW_TYPE_USER_REPLY);
        save(userReply);
        ApiInfo apiInfo = apiInfoMapper.selectById(originalReview.getApiId());
        if (apiInfo != null) {
            notificationService.sendNotification(
                apiInfo.getUserId(),
                NotificationType.API_REVIEW_REPLY.getCode(),
                "评价有新追评",
                "您的API「" + apiInfo.getName() + "」的评价有新追评",
                userReply.getId(),
                "api_review"
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publisherReplyReview(Long userId, ApiReviewPublisherReplyDTO replyDTO) {
        ApiReview originalReview = getById(replyDTO.getReviewId());
        if (originalReview == null) {
            throw new BusinessException("评论不存在");
        }
        ApiInfo apiInfo = apiInfoMapper.selectById(originalReview.getApiId());
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!apiInfo.getUserId().equals(userId)) {
            throw new BusinessException("无权限回复该评论");
        }
        ApiReview publisherReply = new ApiReview();
        publisherReply.setApiId(originalReview.getApiId());
        publisherReply.setOrderId(originalReview.getOrderId());
        publisherReply.setUserId(userId);
        publisherReply.setRating(BigDecimal.ZERO);
        publisherReply.setContent(replyDTO.getContent());
        publisherReply.setParentId(replyDTO.getReviewId());
        publisherReply.setReplyType(ReviewConstants.REVIEW_TYPE_PUBLISHER_REPLY);
        save(publisherReply);
        notificationService.sendNotification(
            originalReview.getUserId(),
            NotificationType.API_REVIEW_REPLY.getCode(),
            "评价已回复",
            "您的评价已得到开发者回复",
            publisherReply.getId(),
            "api_review"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReview(Long userId, ApiReviewUpdateDTO updateDTO) {
        ApiReview review = getById(updateDTO.getReviewId());
        if (review == null) {
            throw new BusinessException("评论不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改该评论");
        }
        review.setContent(updateDTO.getContent());
        updateById(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long userId, Long reviewId) {
        ApiReview review = getById(reviewId);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该评论");
        }
        this.baseMapper.delete(new LambdaQueryWrapper<ApiReview>()
                .eq(ApiReview::getParentId, reviewId));
        removeById(reviewId);
        updateApiAverageRating(review.getApiId());
    }

    @Override
    public IPage<ApiReviewVO> getApiReviews(ApiReviewQueryDTO queryDTO) {
        Page<ApiReview> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<ApiReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getApiId() != null, ApiReview::getApiId, queryDTO.getApiId())
                .eq(queryDTO.getReplyType() != null, ApiReview::getReplyType, queryDTO.getReplyType())
                .eq(queryDTO.getParentReviewId() != null, ApiReview::getParentId, queryDTO.getParentReviewId());
        if (Boolean.TRUE.equals(queryDTO.getQueryOriginalOnly()) 
                || (queryDTO.getReplyType() == null && queryDTO.getParentReviewId() == null)) {
            queryWrapper.eq(ApiReview::getReplyType, ReviewConstants.REVIEW_TYPE_ORIGINAL);
        }
        queryWrapper.orderByDesc(ApiReview::getCreateTime);
        IPage<ApiReview> reviewPage = page(page, queryWrapper);
        IPage<ApiReviewVO> voPage = convertToVOPage(reviewPage);
        if (Boolean.TRUE.equals(queryDTO.getIncludeReplies()) && !voPage.getRecords().isEmpty()) {
            List<Long> reviewIds = voPage.getRecords().stream()
                    .map(ApiReviewVO::getId)
                    .collect(Collectors.toList());
            List<ApiReview> allReplies = this.baseMapper.selectList(new LambdaQueryWrapper<ApiReview>()
                    .in(ApiReview::getParentId, reviewIds)
                    .orderByAsc(ApiReview::getCreateTime));
            Map<Long, List<ApiReviewVO>> repliesMap = groupRepliesByParentId(allReplies);
            for (ApiReviewVO vo : voPage.getRecords()) {
                vo.setReplies(repliesMap.getOrDefault(vo.getId(), Collections.emptyList()));
            }
        }
        return voPage;
    }

    @Override
    public IPage<ApiReviewVO> getMyReviews(Long userId, ApiReviewQueryDTO queryDTO) {
        Page<ApiReview> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<ApiReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiReview::getUserId, userId)
                .eq(queryDTO.getApiId() != null, ApiReview::getApiId, queryDTO.getApiId())
                .orderByDesc(ApiReview::getCreateTime);
        IPage<ApiReview> reviewPage = page(page, queryWrapper);
        return convertToVOPage(reviewPage);
    }

    @Override
    public ApiReviewVO getReviewDetail(Long reviewId) {
        ApiReview review = getById(reviewId);
        if (review == null) {
            return null;
        }
        return convertToVO(review);
    }

    private void updateApiAverageRating(Long apiId) {
        List<ApiReview> reviews = this.baseMapper.selectList(new LambdaQueryWrapper<ApiReview>()
                .eq(ApiReview::getApiId, apiId)
                .eq(ApiReview::getReplyType, ReviewConstants.REVIEW_TYPE_ORIGINAL)
                .select(ApiReview::getRating));
        if (reviews.isEmpty()) {
            return;
        }
        BigDecimal sum = reviews.stream()
                .map(ApiReview::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgRating = sum.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
        apiInfoMapper.update(null, new LambdaUpdateWrapper<ApiInfo>()
                .eq(ApiInfo::getId, apiId)
                .set(ApiInfo::getRating, avgRating));
    }

    private IPage<ApiReviewVO> convertToVOPage(IPage<ApiReview> reviewPage) {
        if (reviewPage.getRecords().isEmpty()) {
            IPage<ApiReviewVO> voPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), 0);
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }
        List<Long> apiIds = reviewPage.getRecords().stream()
                .map(ApiReview::getApiId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> userIds = reviewPage.getRecords().stream()
                .map(ApiReview::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> apiNameMap = Collections.emptyMap();
        if (!apiIds.isEmpty()) {
            List<ApiInfo> apiInfos = apiInfoMapper.selectBatchIds(apiIds);
            apiNameMap = apiInfos.stream()
                    .collect(Collectors.toMap(ApiInfo::getId, ApiInfo::getName));
        }
        Map<Long, String> usernameMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            usernameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }
        Map<Long, String> finalApiNameMap = apiNameMap;
        Map<Long, String> finalUsernameMap = usernameMap;
        IPage<ApiReviewVO> voPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        List<ApiReviewVO> voList = reviewPage.getRecords().stream()
                .map(review -> {
                    ApiReviewVO vo = new ApiReviewVO();
                    vo.setId(review.getId());
                    vo.setOrderId(review.getOrderId());
                    vo.setApiId(review.getApiId());
                    vo.setApiName(finalApiNameMap.get(review.getApiId()));
                    vo.setUserId(review.getUserId());
                    vo.setUsername(finalUsernameMap.get(review.getUserId()));
                    vo.setRating(review.getRating());
                    vo.setContent(review.getContent());
                    vo.setReply(review.getReply());
                    vo.setReplyTime(review.getReplyTime());
                    vo.setParentId(review.getParentId());
                    vo.setReplyType(review.getReplyType());
                    vo.setCreateTime(review.getCreateTime());
                    return vo;
                })
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    private ApiReviewVO convertToVO(ApiReview review) {
        ApiReviewVO vo = new ApiReviewVO();
        vo.setId(review.getId());
        vo.setOrderId(review.getOrderId());
        vo.setApiId(review.getApiId());
        vo.setUserId(review.getUserId());
        vo.setRating(review.getRating());
        vo.setContent(review.getContent());
        vo.setReply(review.getReply());
        vo.setReplyTime(review.getReplyTime());
        vo.setParentId(review.getParentId());
        vo.setReplyType(review.getReplyType());
        vo.setCreateTime(review.getCreateTime());
        ApiInfo apiInfo = apiInfoMapper.selectById(review.getApiId());
        if (apiInfo != null) {
            vo.setApiName(apiInfo.getName());
        }
        User user = userMapper.selectById(review.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }

    private Map<Long, List<ApiReviewVO>> groupRepliesByParentId(List<ApiReview> replies) {
        if (replies.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> apiIds = replies.stream()
                .map(ApiReview::getApiId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> userIds = replies.stream()
                .map(ApiReview::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> apiNameMap = Collections.emptyMap();
        if (!apiIds.isEmpty()) {
            List<ApiInfo> apiInfos = apiInfoMapper.selectBatchIds(apiIds);
            apiNameMap = apiInfos.stream()
                    .collect(Collectors.toMap(ApiInfo::getId, ApiInfo::getName));
        }
        Map<Long, String> usernameMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            usernameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }
        Map<Long, String> finalApiNameMap = apiNameMap;
        Map<Long, String> finalUsernameMap = usernameMap;
        return replies.stream()
                .map(reply -> {
                    ApiReviewVO vo = new ApiReviewVO();
                    vo.setId(reply.getId());
                    vo.setApiId(reply.getApiId());
                    vo.setApiName(finalApiNameMap.get(reply.getApiId()));
                    vo.setUserId(reply.getUserId());
                    vo.setUsername(finalUsernameMap.get(reply.getUserId()));
                    vo.setContent(reply.getContent());
                    vo.setParentId(reply.getParentId());
                    vo.setReplyType(reply.getReplyType());
                    vo.setCreateTime(reply.getCreateTime());
                    return vo;
                })
                .collect(Collectors.groupingBy(ApiReviewVO::getParentId));
    }

}
