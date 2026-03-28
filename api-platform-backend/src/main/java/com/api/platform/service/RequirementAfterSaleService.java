package com.api.platform.service;

import com.api.platform.dto.AfterSaleCreateDTO;
import com.api.platform.dto.AfterSaleDecideDTO;
import com.api.platform.dto.AfterSaleQueryDTO;
import com.api.platform.dto.AfterSaleRespondDTO;
import com.api.platform.entity.RequirementAfterSale;
import com.api.platform.vo.RequirementAfterSaleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RequirementAfterSaleService extends IService<RequirementAfterSale> {

    RequirementAfterSaleVO createAfterSale(Long applicantId, AfterSaleCreateDTO createDTO);

    void respondAfterSale(Long developerId, Long afterSaleId, AfterSaleRespondDTO respondDTO);

    void decideAfterSale(Long adminId, Long afterSaleId, AfterSaleDecideDTO decideDTO);

    RequirementAfterSaleVO getDetailById(Long afterSaleId);

    RequirementAfterSaleVO getDetailByIdWithPermission(Long afterSaleId, Long userId, boolean isAdmin);

    IPage<RequirementAfterSaleVO> pageList(AfterSaleQueryDTO queryDTO);

    IPage<RequirementAfterSaleVO> getMyAfterSales(Long userId, AfterSaleQueryDTO queryDTO);

    IPage<RequirementAfterSaleVO> getDeveloperAfterSales(Long userId, AfterSaleQueryDTO queryDTO);

}
