package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.QuotaQueryDTO;
import com.api.platform.service.UserApiQuotaService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.PageResultVO;
import com.api.platform.vo.UserQuotaVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 配额查询控制器 —— 处理用户API调用配额列表查询请求
 *
 * 路由前缀：/quota
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 */
@RestController
@RequestMapping("/quota")
public class QuotaController {

    @Autowired
    private UserApiQuotaService userApiQuotaService;

    /**
     * 获取当前用户的配额列表
     *
     * 返回用户所有已购买API的配额使用情况，支持按API名称筛选
     *
     * @param pageNum  页码，默认1
     * @param pageSize 每页数量，默认10
     * @param apiName  API名称（模糊搜索，可选）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;UserQuotaVO&gt;&gt; 分页的配额列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<UserQuotaVO>> getQuotaList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String apiName,
            HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        QuotaQueryDTO queryDTO = new QuotaQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setUserId(userId);
        queryDTO.setApiName(apiName);
        IPage<UserQuotaVO> page = userApiQuotaService.pageUserQuotas(queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

}
