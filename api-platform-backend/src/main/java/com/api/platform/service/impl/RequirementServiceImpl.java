package com.api.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.api.platform.dto.ApiParamDTO;
import com.api.platform.dto.RequirementApplyDTO;
import com.api.platform.dto.RequirementApplicantSelectDTO;
import com.api.platform.dto.RequirementCreateDTO;
import com.api.platform.dto.RequirementDeliverDTO;
import com.api.platform.dto.RequirementQueryDTO;
import com.api.platform.entity.Requirement;
import com.api.platform.entity.RequirementApplicant;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.RequirementApplicantMapper;
import com.api.platform.mapper.RequirementMapper;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.RequirementService;
import com.api.platform.vo.ApplicantVO;
import com.api.platform.vo.RequirementVO;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RequirementServiceImpl extends ServiceImpl<RequirementMapper, Requirement> implements RequirementService {

    @Autowired
    private RequirementApplicantMapper applicantMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<RequirementVO> pageList(RequirementQueryDTO queryDTO) {
        Page<Requirement> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Requirement> queryWrapper = buildQueryWrapper(queryDTO);
        queryWrapper.notIn(Requirement::getStatus, "completed", "after_sale", "delivered");
        IPage<Requirement> requirementPage = page(page, queryWrapper);
        return convertToVOPage(requirementPage);
    }

    @Override
    public RequirementVO getDetailById(Long id) {
        Requirement requirement = getById(id);
        if (requirement == null) {
            return null;
        }
        User publisher = userMapper.selectById(requirement.getUserId());
        List<RequirementApplicant> applicants = applicantMapper.selectList(
                new LambdaQueryWrapper<RequirementApplicant>()
                        .eq(RequirementApplicant::getRequirementId, id)
                        .orderByDesc(RequirementApplicant::getApplyTime)
        );
        Map<Long, String> usernameMap = new java.util.HashMap<>();
        usernameMap.put(requirement.getUserId(), publisher != null ? publisher.getUsername() : "");
        if (!applicants.isEmpty()) {
            List<Long> userIds = applicants.stream()
                    .map(RequirementApplicant::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            List<User> users = userMapper.selectBatchIds(userIds);
            users.forEach(user -> usernameMap.put(user.getId(), user.getUsername()));
        }
        List<ApplicantVO> applicantVOs = applicants.stream()
                .map(applicant -> {
                    ApplicantVO vo = new ApplicantVO();
                    BeanUtils.copyProperties(applicant, vo);
                    vo.setUsername(usernameMap.get(applicant.getUserId()));
                    return vo;
                })
                .collect(Collectors.toList());
        RequirementVO vo = convertToVO(requirement);
        vo.setUsername(usernameMap.get(requirement.getUserId()));
        vo.setApplicants(applicantVOs);
        ApplicantVO selectedApplicant = applicantVOs.stream()
                .filter(a -> "accepted".equals(a.getStatus()))
                .findFirst()
                .orElse(null);
        vo.setSelectedApplicant(selectedApplicant);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequirementVO create(Long userId, RequirementCreateDTO createDTO) {
        if (createDTO.getDeadline() != null && createDTO.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("截止日期不能早于当前时间");
        }
        Requirement requirement = new Requirement();
        BeanUtils.copyProperties(createDTO, requirement);
        if (createDTO.getDeadline() != null) {
            requirement.setDeadline(createDTO.getDeadline().atStartOfDay());
        }
        requirement.setUserId(userId);
        requirement.setRequestParams(createDTO.getRequestParamsJson());
        requirement.setResponseParams(createDTO.getResponseParamsJson());
        requirement.setStatus("open");
        save(requirement);
        return convertToVO(requirement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequirementVO update(Long userId, Long id, RequirementCreateDTO updateDTO) {
        Requirement requirement = getById(id);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!requirement.getUserId().equals(userId)) {
            throw new BusinessException("无权限编辑该需求");
        }
        if (!"open".equals(requirement.getStatus())) {
            throw new BusinessException("只有开放中的需求才能编辑");
        }
        if (updateDTO.getDeadline() != null && updateDTO.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("截止日期不能早于当前时间");
        }
        BeanUtils.copyProperties(updateDTO, requirement);
        if (updateDTO.getDeadline() != null) {
            requirement.setDeadline(updateDTO.getDeadline().atStartOfDay());
        }
        requirement.setRequestParams(updateDTO.getRequestParamsJson());
        requirement.setResponseParams(updateDTO.getResponseParamsJson());
        updateById(requirement);
        return convertToVO(requirement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Requirement requirement = getById(id);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!requirement.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该需求");
        }
        if ("in_progress".equals(requirement.getStatus())) {
            throw new BusinessException("正在进行中的需求不能删除");
        }
        removeById(id);
        applicantMapper.delete(new LambdaQueryWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getRequirementId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(Long userId, Long requirementId, RequirementApplyDTO applyDTO) {
        Requirement requirement = getById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!"open".equals(requirement.getStatus())) {
            throw new BusinessException("该需求不在开放状态");
        }
        if (requirement.getUserId().equals(userId)) {
            throw new BusinessException("不能申请自己发布的需求");
        }
        Long existCount = applicantMapper.selectCount(new LambdaQueryWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getRequirementId, requirementId)
                .eq(RequirementApplicant::getUserId, userId));
        if (existCount > 0) {
            throw new BusinessException("您已经申请过该需求");
        }
        RequirementApplicant applicant = new RequirementApplicant();
        applicant.setRequirementId(requirementId);
        applicant.setUserId(userId);
        applicant.setDescription(applyDTO.getDescription());
        applicant.setStatus("pending");
        applicantMapper.insert(applicant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void selectApplicant(Long userId, Long requirementId, RequirementApplicantSelectDTO selectDTO) {
        Requirement requirement = getById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!requirement.getUserId().equals(userId)) {
            throw new BusinessException("无权限选择申请者");
        }
        if (!"open".equals(requirement.getStatus())) {
            throw new BusinessException("只有开放中的需求才能选择申请者");
        }
        RequirementApplicant applicant = applicantMapper.selectById(selectDTO.getApplicantId());
        if (applicant == null || !applicant.getRequirementId().equals(requirementId)) {
            throw new BusinessException("申请者不存在");
        }
        if (!"pending".equals(applicant.getStatus())) {
            throw new BusinessException("该申请者已被处理");
        }
        applicantMapper.update(null, new LambdaUpdateWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getRequirementId, requirementId)
                .eq(RequirementApplicant::getStatus, "pending")
                .set(RequirementApplicant::getStatus, "rejected"));
        applicantMapper.update(null, new LambdaUpdateWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getId, selectDTO.getApplicantId())
                .set(RequirementApplicant::getStatus, "accepted"));
        requirement.setStatus("in_progress");
        updateById(requirement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawApply(Long userId, Long requirementId) {
        Requirement requirement = getById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!"open".equals(requirement.getStatus())) {
            throw new BusinessException("该需求不在开放状态，无法撤回申请");
        }
        RequirementApplicant applicant = applicantMapper.selectOne(new LambdaQueryWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getRequirementId, requirementId)
                .eq(RequirementApplicant::getUserId, userId));
        if (applicant == null) {
            throw new BusinessException("您未申请过该需求");
        }
        if (!"pending".equals(applicant.getStatus())) {
            throw new BusinessException("申请已被处理，无法撤回");
        }
        applicantMapper.deleteById(applicant.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long userId, Long requirementId) {
        Requirement requirement = getById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!requirement.getUserId().equals(userId)) {
            throw new BusinessException("无权限完成该需求");
        }
        if (!"in_progress".equals(requirement.getStatus())) {
            throw new BusinessException("只有进行中的需求才能标记完成");
        }
        Long acceptedCount = applicantMapper.selectCount(new LambdaQueryWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getRequirementId, requirementId)
                .eq(RequirementApplicant::getStatus, "accepted"));
        if (acceptedCount == 0) {
            throw new BusinessException("该需求尚未选择开发者");
        }
        requirement.setStatus("completed");
        updateById(requirement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long requirementId) {
        Requirement requirement = getById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!requirement.getUserId().equals(userId)) {
            throw new BusinessException("无权限取消该需求");
        }
        if ("completed".equals(requirement.getStatus())) {
            throw new BusinessException("已完成的需求不能取消");
        }
        if ("cancelled".equals(requirement.getStatus())) {
            throw new BusinessException("需求已取消");
        }
        requirement.setStatus("cancelled");
        updateById(requirement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(Long userId, Long requirementId, RequirementDeliverDTO deliverDTO) {
        Requirement requirement = getById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!"in_progress".equals(requirement.getStatus())) {
            throw new BusinessException("只有进行中的需求才能交付");
        }
        RequirementApplicant acceptedApplicant = applicantMapper.selectOne(new LambdaQueryWrapper<RequirementApplicant>()
                .eq(RequirementApplicant::getRequirementId, requirementId)
                .eq(RequirementApplicant::getStatus, "accepted"));
        if (acceptedApplicant == null) {
            throw new BusinessException("该需求尚未选择接单者");
        }
        if (!acceptedApplicant.getUserId().equals(userId)) {
            throw new BusinessException("无权限交付该需求");
        }
        requirement.setDeliveryUrl(deliverDTO.getDeliveryUrl());
        requirement.setStatus("delivered");
        updateById(requirement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmDelivery(Long userId, Long requirementId) {
        Requirement requirement = getById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        if (!requirement.getUserId().equals(userId)) {
            throw new BusinessException("无权限确认交付");
        }
        if (!"delivered".equals(requirement.getStatus())) {
            throw new BusinessException("只有已交付的需求才能确认");
        }
        requirement.setStatus("completed");
        updateById(requirement);
    }

    @Override
    public IPage<RequirementVO> getMyPublished(Long userId, RequirementQueryDTO queryDTO) {
        Page<Requirement> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Requirement> queryWrapper = buildQueryWrapper(queryDTO);
        queryWrapper.eq(Requirement::getUserId, userId);
        IPage<Requirement> requirementPage = page(page, queryWrapper);
        return convertToVOPage(requirementPage);
    }

    @Override
    public IPage<RequirementVO> getMyApplied(Long userId, RequirementQueryDTO queryDTO) {
        Page<RequirementApplicant> applicantPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<RequirementApplicant> applicantWrapper = new LambdaQueryWrapper<>();
        applicantWrapper.eq(RequirementApplicant::getUserId, userId);
        if (StrUtil.isNotBlank(queryDTO.getStatus())) {
            applicantWrapper.eq(RequirementApplicant::getStatus, queryDTO.getStatus());
        }
        applicantWrapper.orderByDesc(RequirementApplicant::getApplyTime);
        IPage<RequirementApplicant> applicantIPage = applicantMapper.selectPage(applicantPage, applicantWrapper);
        if (applicantIPage.getRecords().isEmpty()) {
            IPage<RequirementVO> emptyPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), 0);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }
        List<Long> requirementIds = applicantIPage.getRecords().stream()
                .map(RequirementApplicant::getRequirementId)
                .distinct()
                .collect(Collectors.toList());
        List<Requirement> requirements = listByIds(requirementIds);
        Map<Long, Requirement> requirementMap = requirements.stream()
                .collect(Collectors.toMap(Requirement::getId, r -> r));
        List<Long> publisherIds = requirements.stream()
                .map(Requirement::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> usernameMap = Collections.emptyMap();
        if (!publisherIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(publisherIds);
            usernameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }
        Map<Long, String> finalUsernameMap = usernameMap;
        IPage<RequirementVO> voPage = new Page<>(applicantIPage.getCurrent(), applicantIPage.getSize(), applicantIPage.getTotal());
        List<RequirementVO> voList = applicantIPage.getRecords().stream()
                .map(applicant -> {
                    Requirement requirement = requirementMap.get(applicant.getRequirementId());
                    if (requirement == null) return null;
                    RequirementVO vo = convertToVO(requirement);
                    vo.setUsername(finalUsernameMap.get(requirement.getUserId()));
                    vo.setMyApplyStatus(applicant.getStatus());
                    return vo;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    private LambdaQueryWrapper<Requirement> buildQueryWrapper(RequirementQueryDTO queryDTO) {
        LambdaQueryWrapper<Requirement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(StrUtil.isNotBlank(queryDTO.getKeyword()), wrapper ->
                wrapper.like(Requirement::getTitle, queryDTO.getKeyword())
                        .or()
                        .like(Requirement::getDescription, queryDTO.getKeyword())
        );
        queryWrapper.ge(queryDTO.getMinBudget() != null, Requirement::getBudget, queryDTO.getMinBudget())
                .le(queryDTO.getMaxBudget() != null, Requirement::getBudget, queryDTO.getMaxBudget())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), Requirement::getStatus, queryDTO.getStatus());
        if (StrUtil.isNotBlank(queryDTO.getSortBy())) {
            String sortBy = queryDTO.getSortBy();
            String sortOrder = StrUtil.isBlank(queryDTO.getSortOrder()) ? "asc" : queryDTO.getSortOrder();
            boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
            switch (sortBy) {
                case "budget":
                    queryWrapper.orderBy(true, isAsc, Requirement::getBudget);
                    break;
                case "deadline":
                    queryWrapper.orderBy(true, isAsc, Requirement::getDeadline);
                    break;
                case "createTime":
                    queryWrapper.orderBy(true, isAsc, Requirement::getCreateTime);
                    break;
                default:
                    queryWrapper.orderByDesc(Requirement::getCreateTime);
            }
        } else {
            queryWrapper.orderByDesc(Requirement::getCreateTime);
        }
        return queryWrapper;
    }

    private IPage<RequirementVO> convertToVOPage(IPage<Requirement> requirementPage) {
        List<Long> userIds = requirementPage.getRecords().stream()
                .map(Requirement::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> usernameMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            usernameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }
        Map<Long, String> finalUsernameMap = usernameMap;
        IPage<RequirementVO> voPage = new Page<>(requirementPage.getCurrent(), requirementPage.getSize(), requirementPage.getTotal());
        List<RequirementVO> voList = requirementPage.getRecords().stream()
                .map(requirement -> {
                    RequirementVO vo = convertToVO(requirement);
                    vo.setUsername(finalUsernameMap.get(requirement.getUserId()));
                    return vo;
                })
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    private RequirementVO convertToVO(Requirement requirement) {
        RequirementVO vo = new RequirementVO();
        BeanUtils.copyProperties(requirement, vo);
        if (StrUtil.isNotBlank(requirement.getRequestParams())) {
            vo.setRequestParams(JSONUtil.toList(requirement.getRequestParams(), ApiParamDTO.class));
        }
        if (StrUtil.isNotBlank(requirement.getResponseParams())) {
            vo.setResponseParams(JSONUtil.toList(requirement.getResponseParams(), ApiParamDTO.class));
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        Requirement requirement = getById(id);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        requirement.setStatus(status);
        updateById(requirement);
    }

}
