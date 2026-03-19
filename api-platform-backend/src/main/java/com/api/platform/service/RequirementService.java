package com.api.platform.service;

import com.api.platform.dto.RequirementApplyDTO;
import com.api.platform.dto.RequirementApplicantSelectDTO;
import com.api.platform.dto.RequirementCreateDTO;
import com.api.platform.dto.RequirementDeliverDTO;
import com.api.platform.dto.RequirementQueryDTO;
import com.api.platform.entity.Requirement;
import com.api.platform.vo.RequirementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RequirementService extends IService<Requirement> {

    IPage<RequirementVO> pageList(RequirementQueryDTO queryDTO);

    RequirementVO getDetailById(Long id);

    RequirementVO create(Long userId, RequirementCreateDTO createDTO);

    RequirementVO update(Long userId, Long id, RequirementCreateDTO updateDTO);

    void delete(Long userId, Long id);

    void apply(Long userId, Long requirementId, RequirementApplyDTO applyDTO);

    void selectApplicant(Long userId, Long requirementId, RequirementApplicantSelectDTO selectDTO);

    void withdrawApply(Long userId, Long requirementId);

    void complete(Long userId, Long requirementId);

    void cancel(Long userId, Long requirementId);

    void deliver(Long userId, Long requirementId, RequirementDeliverDTO deliverDTO);

    void confirmDelivery(Long userId, Long requirementId);

    IPage<RequirementVO> getMyPublished(Long userId, RequirementQueryDTO queryDTO);

    IPage<RequirementVO> getMyApplied(Long userId, RequirementQueryDTO queryDTO);

    void updateStatus(Long id, String status);

}
