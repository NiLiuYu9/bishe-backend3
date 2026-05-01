package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.platform.dto.OrderCreateDTO;
import com.api.platform.dto.OrderQueryDTO;
import com.api.platform.dto.OrderRatingDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiReview;
import com.api.platform.entity.OrderInfo;
import com.api.platform.entity.User;
import com.api.platform.mapper.ApiInfoMapper;
import com.api.platform.mapper.ApiReviewMapper;
import com.api.platform.mapper.OrderInfoMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.OrderInfoService;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.vo.OrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现 —— 处理订单的创建、查询、状态流转、评价等核心业务逻辑
 *
 * 订单状态流转：pending(待支付) → paid(已支付) → completed(已完成)
 *                                            → cancelled(已取消)
 * 支付完成后会自动给用户增加 API 调用配额
 *
 * 阶梯折扣规则：
 * - 购买次数 >= 2000：7折
 * - 购买次数 >= 500：8折
 * - 购买次数 >= 100：9折
 * - 购买次数 < 100：无折扣
 *
 * 订单号格式：ORD + 时间戳(14位) + 4位随机数
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserApiQuotaService userApiQuotaService;

    @Autowired
    private ApiReviewMapper apiReviewMapper;

    /**
     * 创建订单
     *
     * 业务流程：
     * 1. 校验 API 是否存在且已审核通过（approved 状态才可购买）
     * 2. 根据购买次数计算折扣（阶梯折扣：100次9折、500次8折、2000次7折）
     * 3. 计算总价 = 单价 × 调用次数 × 折扣
     * 4. 生成唯一订单号（格式：ORD + 时间戳 + 4位随机数）
     * 5. 保存订单，初始状态为 pending（待支付）
     */
    @Override
    public OrderVO createOrder(Long userId, String username, OrderCreateDTO createDTO) {
        ApiInfo apiInfo = apiInfoMapper.selectById(createDTO.getApiId());
        if (apiInfo == null) {
            throw new RuntimeException("API不存在");
        }
        if (!"approved".equals(apiInfo.getStatus())) { // 只有已通过的API才可购买
            throw new RuntimeException("该API暂不可购买");
        }
        BigDecimal unitPrice = apiInfo.getPrice();
        int invokeCount = createDTO.getInvokeCount();
        BigDecimal discount = getDiscount(invokeCount); // 计算阶梯折扣
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(invokeCount))
                .multiply(discount) // 总价 = 单价 × 次数 × 折扣
                .setScale(2, RoundingMode.HALF_UP);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(generateOrderNo());
        orderInfo.setApiId(createDTO.getApiId());
        orderInfo.setApiName(apiInfo.getName());
        orderInfo.setBuyerId(userId);
        orderInfo.setBuyerName(username);
        orderInfo.setInvokeCount(invokeCount);
        orderInfo.setPrice(totalPrice);
        orderInfo.setStatus("pending"); // 初始状态：待支付
        save(orderInfo);
        return convertToVO(orderInfo);
    }

    /** 分页查询订单列表（支持按订单号、状态、买家ID筛选） */
    @Override
    public IPage<OrderVO> pageOrderList(OrderQueryDTO queryDTO) {
        Page<OrderInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(queryDTO.getOrderNo()), OrderInfo::getOrderNo, queryDTO.getOrderNo())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), OrderInfo::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getBuyerId() != null, OrderInfo::getBuyerId, queryDTO.getBuyerId())
                .orderByDesc(OrderInfo::getCreateTime);
        IPage<OrderInfo> orderInfoPage = page(page, queryWrapper);
        IPage<OrderVO> orderVOPage = new Page<>(orderInfoPage.getCurrent(), orderInfoPage.getSize(), orderInfoPage.getTotal());
        List<OrderVO> orderVOList = orderInfoPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        orderVOPage.setRecords(orderVOList);
        return orderVOPage;
    }

    /** 获取订单详情 */
    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderInfo orderInfo = getById(orderId);
        if (orderInfo == null) {
            return null;
        }
        return convertToVO(orderInfo);
    }

    /**
     * 更新订单状态
     *
     * 状态流转规则：
     * pending → paid（支付成功，增加API调用配额）
     * pending → cancelled（取消订单）
     * paid → completed（确认完成，记录完成时间）
     * paid → cancelled（已支付订单取消）
     * completed/cancelled 为终态，不可变更
     *
     * 关键业务规则：支付成功时自动给用户增加API调用配额
     */
    @Override
    public void updateOrderStatus(Long orderId, String status) {
        OrderInfo orderInfo = getById(orderId);
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        String oldStatus = orderInfo.getStatus();
        validateStatusTransition(oldStatus, status); // 校验状态流转合法性
        orderInfo.setStatus(status);
        if ("completed".equals(status)) {
            // 兜底逻辑：当订单从非paid状态直接变为completed时，也需要设置payTime并增加配额，防止数据不一致
            if (!"paid".equals(oldStatus) && !"completed".equals(oldStatus)) {
                orderInfo.setPayTime(LocalDateTime.now());
                userApiQuotaService.addQuota(orderInfo.getBuyerId(), orderInfo.getApiId(), orderInfo.getInvokeCount()); // 支付完成增加配额
            }
            orderInfo.setCompleteTime(LocalDateTime.now()); // 记录完成时间
        } else if ("paid".equals(status)) {
            orderInfo.setPayTime(LocalDateTime.now()); // 记录支付时间
            if (!"paid".equals(oldStatus)) {
                userApiQuotaService.addQuota(orderInfo.getBuyerId(), orderInfo.getApiId(), orderInfo.getInvokeCount()); // 支付成功增加API调用配额
            }
        }
        updateById(orderInfo);
    }

    /** 校验订单状态流转合法性（pending→paid/cancelled, paid→completed/cancelled, 终态不可变更） */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return;
        }
        switch (currentStatus) {
            case "pending":
                if (!"paid".equals(newStatus) && !"cancelled".equals(newStatus)) {
                    throw new RuntimeException("待支付订单只能变更为已支付或已取消");
                }
                break;
            case "paid":
                if (!"completed".equals(newStatus) && !"cancelled".equals(newStatus)) {
                    throw new RuntimeException("已支付订单只能变更为已完成或已取消");
                }
                break;
            case "completed":
                throw new RuntimeException("已完成订单不能变更状态");
            case "cancelled":
                throw new RuntimeException("已取消订单不能变更状态");
            default:
                throw new RuntimeException("未知的订单状态");
        }
    }

    /**
     * 删除订单
     *
     * 注意：当前实现未做状态校验，任何状态订单均可删除
     */
    @Override
    public void deleteOrder(Long orderId) {
        removeById(orderId);
    }

    /**
     * 评价订单
     *
     * 业务流程：
     * 1. 校验订单存在性和评价权限（只有买家能评价）
     * 2. 校验订单状态（已支付/已完成才可评价）
     * 3. 更新订单评分
     * 4. 同步更新关联的API评价记录
     */
    @Override
    public void rateOrder(Long orderId, Long userId, OrderRatingDTO ratingDTO) {
        OrderInfo orderInfo = getById(orderId);
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!orderInfo.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权限评价该订单");
        }
        if (!"paid".equals(orderInfo.getStatus()) && !"completed".equals(orderInfo.getStatus())) {
            throw new RuntimeException("订单未完成，无法评价");
        }
        orderInfo.setRating(ratingDTO.getRating());
        updateById(orderInfo);
        ApiReview review = apiReviewMapper.selectOne(new LambdaQueryWrapper<ApiReview>()
                .eq(ApiReview::getOrderId, orderId));
        if (review != null) {
            review.setRating(ratingDTO.getRating());
            apiReviewMapper.updateById(review);
        }
    }

    /** 根据API ID计算平均评分 */
    @Override
    public BigDecimal getAverageRatingByApiId(Long apiId) {
        BigDecimal avgRating = baseMapper.getAverageRatingByApiId(apiId);
        if (avgRating == null) {
            return BigDecimal.ZERO;
        }
        return avgRating.setScale(1, RoundingMode.HALF_UP);
    }

    /** 生成订单号：ORD + 时间戳(14位) + 4位随机数 */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return "ORD" + timestamp + String.format("%04d", random);
    }

    /**
     * 阶梯折扣计算
     * >= 2000次：7折, >= 500次：8折, >= 100次：9折, < 100次：无折扣
     */
    private BigDecimal getDiscount(int invokeCount) {
        if (invokeCount >= 2000) {
            return new BigDecimal("0.7");
        } else if (invokeCount >= 500) {
            return new BigDecimal("0.8");
        } else if (invokeCount >= 100) {
            return new BigDecimal("0.9");
        }
        return BigDecimal.ONE;
    }

    /** OrderInfo转OrderVO（含时间格式化和关联评价信息） */
    private OrderVO convertToVO(OrderInfo orderInfo) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderInfo, orderVO);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (orderInfo.getCreateTime() != null) {
            orderVO.setCreateTime(orderInfo.getCreateTime().format(formatter));
        }
        if (orderInfo.getPayTime() != null) {
            orderVO.setPayTime(orderInfo.getPayTime().format(formatter));
        }
        if (orderInfo.getCompleteTime() != null) {
            orderVO.setCompleteTime(orderInfo.getCompleteTime().format(formatter));
        }
        ApiReview review = apiReviewMapper.selectOne(new LambdaQueryWrapper<ApiReview>()
                .eq(ApiReview::getOrderId, orderInfo.getId())
                .eq(ApiReview::getReplyType, 0)); // replyType=0表示原评论，仅查询原始评价，不包含回复
        if (review != null) {
            orderVO.setReviewContent(review.getContent());
            orderVO.setReviewId(review.getId());
        }
        return orderVO;
    }

}
