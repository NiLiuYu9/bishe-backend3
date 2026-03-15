package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.platform.dto.OrderCreateDTO;
import com.api.platform.dto.OrderQueryDTO;
import com.api.platform.dto.OrderRatingDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.OrderInfo;
import com.api.platform.entity.User;
import com.api.platform.mapper.ApiInfoMapper;
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

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserApiQuotaService userApiQuotaService;

    @Override
    public OrderVO createOrder(Long userId, String username, OrderCreateDTO createDTO) {
        ApiInfo apiInfo = apiInfoMapper.selectById(createDTO.getApiId());
        if (apiInfo == null) {
            throw new RuntimeException("API不存在");
        }
        if (!"approved".equals(apiInfo.getStatus())) {
            throw new RuntimeException("该API暂不可购买");
        }
        BigDecimal unitPrice = apiInfo.getPrice();
        int invokeCount = createDTO.getInvokeCount();
        BigDecimal discount = getDiscount(invokeCount);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(invokeCount))
                .multiply(discount)
                .setScale(2, RoundingMode.HALF_UP);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(generateOrderNo());
        orderInfo.setApiId(createDTO.getApiId());
        orderInfo.setApiName(apiInfo.getName());
        orderInfo.setBuyerId(userId);
        orderInfo.setBuyerName(username);
        orderInfo.setInvokeCount(invokeCount);
        orderInfo.setPrice(totalPrice);
        orderInfo.setStatus("pending");
        save(orderInfo);
        return convertToVO(orderInfo);
    }

    @Override
    public IPage<OrderVO> pageOrderList(OrderQueryDTO queryDTO) {
        Page<OrderInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(queryDTO.getOrderNo()), OrderInfo::getOrderNo, queryDTO.getOrderNo())
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

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderInfo orderInfo = getById(orderId);
        if (orderInfo == null) {
            return null;
        }
        return convertToVO(orderInfo);
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        OrderInfo orderInfo = getById(orderId);
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        String oldStatus = orderInfo.getStatus();
        orderInfo.setStatus(status);
        if ("completed".equals(status)) {
            if (!"paid".equals(oldStatus) && !"completed".equals(oldStatus)) {
                orderInfo.setPayTime(LocalDateTime.now());
                userApiQuotaService.addQuota(orderInfo.getBuyerId(), orderInfo.getApiId(), orderInfo.getInvokeCount());
            }
            orderInfo.setCompleteTime(LocalDateTime.now());
        } else if ("paid".equals(status)) {
            orderInfo.setPayTime(LocalDateTime.now());
            if (!"paid".equals(oldStatus)) {
                userApiQuotaService.addQuota(orderInfo.getBuyerId(), orderInfo.getApiId(), orderInfo.getInvokeCount());
            }
        }
        updateById(orderInfo);
    }

    @Override
    public void deleteOrder(Long orderId) {
        removeById(orderId);
    }

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
    }

    @Override
    public BigDecimal getAverageRatingByApiId(Long apiId) {
        BigDecimal avgRating = baseMapper.getAverageRatingByApiId(apiId);
        if (avgRating == null) {
            return BigDecimal.ZERO;
        }
        return avgRating.setScale(1, RoundingMode.HALF_UP);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return "ORD" + timestamp + String.format("%04d", random);
    }

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
        return orderVO;
    }

}
