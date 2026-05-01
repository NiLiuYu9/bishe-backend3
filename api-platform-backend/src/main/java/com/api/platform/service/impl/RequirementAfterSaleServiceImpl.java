package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.platform.constants.NotificationType;
import com.api.platform.dto.AfterSaleCreateDTO;
import com.api.platform.dto.AfterSaleDecideDTO;
import com.api.platform.dto.AfterSaleQueryDTO;
import com.api.platform.entity.Requirement;
import com.api.platform.entity.RequirementAfterSale;
import com.api.platform.entity.RequirementApplicant;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.RequirementAfterSaleMapper;
import com.api.platform.mapper.RequirementApplicantMapper;
import com.api.platform.mapper.RequirementMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.NotificationService;
import com.api.platform.service.RequirementAfterSaleService;
import com.api.platform.service.RequirementService;
import com.api.platform.vo.RequirementAfterSaleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 需求售后服务实现 —— 处理需求售后申请、管理员裁定、售后对话等核心业务逻辑
 *
 * 售后状态流转：pending(待处理) → resolved(已解决) / rejected(已驳回)
 * 裁定结果：completed(完成退款) / refunded(退款)
 *
 * 售后流程：
 * 1. 申请人发起售后（需提供原因和未实现功能列表）
 * 2. 管理员查看双方对话，做出裁定
 * 3. 裁定结果通知双方，如退款则同步处理
 */
@Service
public class RequirementAfterSaleServiceImpl extends ServiceImpl<RequirementAfterSaleMapper, RequirementAfterSale> implements RequirementAfterSaleService {

    @Autowired
    private RequirementMapper requirementMapper;

    @Autowired
    private RequirementApplicantMapper applicantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequirementAfterSaleVO createAfterSale(Long applicantId, AfterSaleCreateDTO createDTO) {
        Requirement requirement = requirementMapper.selectById(createDTO.getRequirementId());
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!requirement.getUserId().equals(applicantId)) {
            throw new BusinessException("无权限发起售后申请");
        }
        if (!"in_progress".equals(requirement.getStatus()) && !"completed".equals(requirement.getStatus()) && !"delivered".equals(requirement.getStatus())) {
            throw new BusinessException("只有进行中、已交付或已完成的需求才能发起售后申请");
        }
        Long existCount = this.baseMapper.selectCount(new LambdaQueryWrapper<RequirementAfterSale>()
                .eq(RequirementAfterSale::getRequirementId, createDTO.getRequirementId())
                .eq(RequirementAfterSale::getStatus, "pending"));
        if (existCount > 0) {
            throw new BusinessException("该需求已有待处理的售后申请");
        }
        RequirementApplicant acceptedApplicant = applicantMapper.selectOne(new LambdaQueryWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getRequirementId, createDTO.getRequirementId())
                .eq(RequirementApplicant::getStatus, "accepted"));
        if (acceptedApplicant == null) {
            throw new BusinessException("该需求尚未选择开发者");
        }
        RequirementAfterSale afterSale = new RequirementAfterSale();
        BeanUtils.copyProperties(createDTO, afterSale);
        afterSale.setApplicantId(applicantId);
        afterSale.setDeveloperId(acceptedApplicant.getUserId());
        afterSale.setStatus("pending");
        save(afterSale);
        requirementService.updateStatus(createDTO.getRequirementId(), "after_sale");
        return convertToVO(afterSale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decideAfterSale(Long adminId, Long afterSaleId, AfterSaleDecideDTO decideDTO) {
        RequirementAfterSale afterSale = getById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException("售后申请不存在");
        }
        if (!"pending".equals(afterSale.getStatus())) {
            throw new BusinessException("该售后申请已处理");
        }
        String decision = decideDTO.getAdminDecision();
        if (!"resolved".equals(decision) && !"rejected".equals(decision)) {
            throw new BusinessException("裁定决定无效，必须为resolved或rejected");
        }
        if ("resolved".equals(decision)) {
            String result = decideDTO.getResult();
            if (StrUtil.isBlank(result) || (!"completed".equals(result) && !"refunded".equals(result))) {
                throw new BusinessException("解决结果无效，必须为completed或refunded");
            }
            afterSale.setResult(result);
            Requirement requirement = requirementMapper.selectById(afterSale.getRequirementId());
            if (requirement != null) {
                if ("completed".equals(result)) {
                    requirementService.updateStatus(afterSale.getRequirementId(), "completed");
                } else if ("refunded".equals(result)) {
                    requirementService.updateStatus(afterSale.getRequirementId(), "refunded");
                }
            }
        } else {
            requirementService.updateStatus(afterSale.getRequirementId(), "in_progress");
        }
        afterSale.setAdminId(adminId);
        afterSale.setAdminDecision(decision);
        afterSale.setAdminDecisionTime(LocalDateTime.now());
        afterSale.setStatus(decision);
        updateById(afterSale);
        List<Long> userIds = Arrays.asList(afterSale.getApplicantId(), afterSale.getDeveloperId());
        notificationService.sendNotificationBatch(
            userIds,
            NotificationType.AFTER_SALE_STATUS_UPDATE.getCode(),
            "售后申请已裁定",
            "售后申请已由管理员裁定：" + ("resolved".equals(decision) ? "已解决" : "已驳回"),
            afterSaleId,
            "after_sale"
        );
    }

    @Override
    public RequirementAfterSaleVO getDetailById(Long afterSaleId) {
        RequirementAfterSale afterSale = getById(afterSaleId);
        if (afterSale == null) {
            return null;
        }
        return convertToVO(afterSale);
    }

    @Override
    public RequirementAfterSaleVO getDetailByIdWithPermission(Long afterSaleId, Long userId, boolean isAdmin) {
        RequirementAfterSale afterSale = getById(afterSaleId);
        if (afterSale == null) {
            return null;
        }
        if (!isAdmin && !afterSale.getApplicantId().equals(userId) && !afterSale.getDeveloperId().equals(userId)) {
            throw new BusinessException("无权限查看该售后申请详情");
        }
        return convertToVO(afterSale);
    }

    @Override
    public IPage<RequirementAfterSaleVO> pageList(AfterSaleQueryDTO queryDTO) {
        Page<RequirementAfterSale> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<RequirementAfterSale> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getRequirementId() != null, RequirementAfterSale::getRequirementId, queryDTO.getRequirementId())
                .eq(queryDTO.getApplicantId() != null, RequirementAfterSale::getApplicantId, queryDTO.getApplicantId())
                .eq(queryDTO.getDeveloperId() != null, RequirementAfterSale::getDeveloperId, queryDTO.getDeveloperId())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), RequirementAfterSale::getStatus, queryDTO.getStatus())
                .orderByDesc(RequirementAfterSale::getCreateTime);
        IPage<RequirementAfterSale> afterSalePage = page(page, queryWrapper);
        return convertToVOPage(afterSalePage);
    }

    @Override
    public IPage<RequirementAfterSaleVO> getMyAfterSales(Long userId, AfterSaleQueryDTO queryDTO) {
        Page<RequirementAfterSale> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<RequirementAfterSale> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RequirementAfterSale::getApplicantId, userId)
                .eq(queryDTO.getRequirementId() != null, RequirementAfterSale::getRequirementId, queryDTO.getRequirementId())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), RequirementAfterSale::getStatus, queryDTO.getStatus())
                .orderByDesc(RequirementAfterSale::getCreateTime);
        IPage<RequirementAfterSale> afterSalePage = page(page, queryWrapper);
        return convertToVOPage(afterSalePage);
    }

    @Override
    public IPage<RequirementAfterSaleVO> getDeveloperAfterSales(Long userId, AfterSaleQueryDTO queryDTO) {
        Page<RequirementAfterSale> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<RequirementAfterSale> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RequirementAfterSale::getDeveloperId, userId)
                .eq(queryDTO.getRequirementId() != null, RequirementAfterSale::getRequirementId, queryDTO.getRequirementId())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), RequirementAfterSale::getStatus, queryDTO.getStatus())
                .orderByDesc(RequirementAfterSale::getCreateTime);
        IPage<RequirementAfterSale> afterSalePage = page(page, queryWrapper);
        return convertToVOPage(afterSalePage);
    }

    private IPage<RequirementAfterSaleVO> convertToVOPage(IPage<RequirementAfterSale> afterSalePage) {
        if (afterSalePage.getRecords().isEmpty()) {
            IPage<RequirementAfterSaleVO> voPage = new Page<>(afterSalePage.getCurrent(), afterSalePage.getSize(), 0);
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }
        List<Long> requirementIds = afterSalePage.getRecords().stream()
                .map(RequirementAfterSale::getRequirementId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> userIds = afterSalePage.getRecords().stream()
                .flatMap(a -> java.util.stream.Stream.of(a.getApplicantId(), a.getDeveloperId(), a.getAdminId()))
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Requirement> requirementMap = Collections.emptyMap();
        if (!requirementIds.isEmpty()) {
            List<Requirement> requirements = requirementMapper.selectBatchIds(requirementIds);
            requirementMap = requirements.stream()
                    .collect(Collectors.toMap(Requirement::getId, r -> r));
        }
        Map<Long, User> userMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }
        Map<Long, Requirement> finalRequirementMap = requirementMap;
        Map<Long, User> finalUserMap = userMap;
        IPage<RequirementAfterSaleVO> voPage = new Page<>(afterSalePage.getCurrent(), afterSalePage.getSize(), afterSalePage.getTotal());
        List<RequirementAfterSaleVO> voList = afterSalePage.getRecords().stream()
                .map(afterSale -> {
                    RequirementAfterSaleVO vo = new RequirementAfterSaleVO();
                    BeanUtils.copyProperties(afterSale, vo);
                    Requirement requirement = finalRequirementMap.get(afterSale.getRequirementId());
                    if (requirement != null) {
                        vo.setRequirementTitle(requirement.getTitle());
                    }
                    User applicant = finalUserMap.get(afterSale.getApplicantId());
                    if (applicant != null) {
                        vo.setApplicantName(applicant.getUsername());
                    }
                    User developer = finalUserMap.get(afterSale.getDeveloperId());
                    if (developer != null) {
                        vo.setDeveloperName(developer.getUsername());
                    }
                    if (afterSale.getAdminId() != null) {
                        User admin = finalUserMap.get(afterSale.getAdminId());
                        if (admin != null) {
                            vo.setAdminName(admin.getUsername());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    private RequirementAfterSaleVO convertToVO(RequirementAfterSale afterSale) {
        RequirementAfterSaleVO vo = new RequirementAfterSaleVO();
        BeanUtils.copyProperties(afterSale, vo);
        Requirement requirement = requirementMapper.selectById(afterSale.getRequirementId());
        if (requirement != null) {
            vo.setRequirementTitle(requirement.getTitle());
        }
        User applicant = userMapper.selectById(afterSale.getApplicantId());
        if (applicant != null) {
            vo.setApplicantName(applicant.getUsername());
        }
        User developer = userMapper.selectById(afterSale.getDeveloperId());
        if (developer != null) {
            vo.setDeveloperName(developer.getUsername());
        }
        if (afterSale.getAdminId() != null) {
            User admin = userMapper.selectById(afterSale.getAdminId());
            if (admin != null) {
                vo.setAdminName(admin.getUsername());
            }
        }
        return vo;
    }

}
