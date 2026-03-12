package com.api.platform.service;

import com.api.platform.dto.QuotaQueryDTO;
import com.api.platform.entity.UserApiQuota;
import com.api.platform.vo.UserQuotaVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserApiQuotaService extends IService<UserApiQuota> {

    void addQuota(Long userId, Long apiId, Integer count);

    boolean deductQuota(Long userId, Long apiId);

    UserApiQuota getQuota(Long userId, Long apiId);

    List<UserApiQuota> getUserQuotas(Long userId);

    boolean hasQuota(Long userId, Long apiId);

    IPage<UserQuotaVO> pageUserQuotas(QuotaQueryDTO queryDTO);

}
