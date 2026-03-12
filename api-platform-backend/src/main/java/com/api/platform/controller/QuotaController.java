package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.constants.SessionConstants;
import com.api.platform.dto.QuotaQueryDTO;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.vo.PageResultVO;
import com.api.platform.vo.UserQuotaVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/quota")
public class QuotaController {

    @Autowired
    private UserApiQuotaService userApiQuotaService;

    @GetMapping("/list")
    public Result<PageResultVO<UserQuotaVO>> getQuotaList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String apiName,
            HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.unauthorized();
        }
        QuotaQueryDTO queryDTO = new QuotaQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setUserId(userId);
        queryDTO.setApiName(apiName);
        IPage<UserQuotaVO> page = userApiQuotaService.pageUserQuotas(queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

}
