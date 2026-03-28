package com.api.platform.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.api.platform.common.Result;
import com.api.platform.dto.ApiQueryDTO;
import com.api.platform.dto.ApiTypeDTO;
import com.api.platform.dto.ApiTypeQueryDTO;
import com.api.platform.dto.AuditApiDTO;
import com.api.platform.dto.FreezeUserDTO;
import com.api.platform.dto.OrderQueryDTO;
import com.api.platform.dto.RequirementQueryDTO;
import com.api.platform.dto.UpdateStatusDTO;
import com.api.platform.dto.UserQueryDTO;
import com.api.platform.vo.ApiTypeVO;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.OrderVO;
import com.api.platform.vo.PageResultVO;
import com.api.platform.vo.RequirementVO;
import com.api.platform.vo.UserVO;
import com.api.platform.entity.ApiType;
import com.api.platform.exception.BusinessException;
import com.api.platform.service.ApiInfoService;
import com.api.platform.service.ApiTypeService;
import com.api.platform.service.OrderInfoService;
import com.api.platform.service.RequirementService;
import com.api.platform.service.StatisticsSyncService;
import com.api.platform.service.UserService;
import com.api.platform.utils.SessionUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class ManagerController {

    @Autowired
    private ApiTypeService apiTypeService;

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private StatisticsSyncService statisticsSyncService;

    private void checkAdminPermission(HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            throw new BusinessException(403, "无权限访问，仅管理员可操作");
        }
    }

    @GetMapping("/users")
    public Result<PageResultVO<UserVO>> getUserList(UserQueryDTO userQueryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<UserVO> userPage = userService.pageUserList(userQueryDTO);
        return Result.success(PageResultVO.of(userPage.getRecords(), userPage.getTotal()));
    }

    @GetMapping("/api-types")
    public Result<PageResultVO<ApiTypeVO>> getApiTypes(ApiTypeQueryDTO queryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<ApiTypeVO> apiTypeVOPage = apiTypeService.pageApiTypes(queryDTO);
        return Result.success(PageResultVO.of(apiTypeVOPage.getRecords(), apiTypeVOPage.getTotal()));
    }

    @GetMapping("/api-types/all")
    public Result<List<ApiType>> getAllApiTypes(HttpSession session) {
        checkAdminPermission(session);
        List<ApiType> apiTypes = apiTypeService.getAllTypes();
        return Result.success(apiTypes);
    }

    @PostMapping("/api-types")
    public Result<ApiType> createApiType(@Validated @RequestBody ApiTypeDTO apiTypeDTO, HttpSession session) {
        checkAdminPermission(session);
        ApiType apiType = new ApiType();
        BeanUtils.copyProperties(apiTypeDTO, apiType);
        apiTypeService.createType(apiType);
        return Result.success(apiType);
    }

    @PutMapping("/api-types/{id}")
    public Result<Void> updateApiType(@PathVariable Long id, @Validated @RequestBody ApiTypeDTO apiTypeDTO, HttpSession session) {
        checkAdminPermission(session);
        ApiType apiType = new ApiType();
        apiType.setId(id);
        BeanUtils.copyProperties(apiTypeDTO, apiType);
        apiTypeService.updateType(apiType);
        return Result.success();
    }

    @PutMapping("/api-types/{id}/updateStatus")
    public Result<Void> updateApiTypeStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO, HttpSession session) {
        checkAdminPermission(session);
        apiTypeService.updateStatus(id, updateStatusDTO.getStatus());
        return Result.success();
    }

    @PutMapping("/users/{id}/freeze")
    public Result<Void> freeze(@PathVariable Long id, @RequestBody FreezeUserDTO freezeUserDTO, HttpSession session) {
        checkAdminPermission(session);
        userService.freezeUser(id, freezeUserDTO);
        return Result.success();
    }

    @PutMapping("/users/{id}/unfreeze")
    public Result<Void> unfreeze(@PathVariable Long id, HttpSession session) {
        checkAdminPermission(session);
        userService.unfreezeUser(id);
        return Result.success();
    }

    @GetMapping("/apis")
    public Result<PageResultVO<ApiVO>> getApis(ApiQueryDTO queryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    @PutMapping("/apis/{id}/updateStatus")
    public Result<Void> updateApiStatus(@PathVariable Long id, @Validated @RequestBody AuditApiDTO auditApiDTO, HttpSession session) {
        checkAdminPermission(session);
        apiInfoService.auditApi(id, auditApiDTO);
        return Result.success();
    }

    @GetMapping("/orders")
    public Result<PageResultVO<OrderVO>> getOrders(OrderQueryDTO queryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<OrderVO> orderVOPage = orderInfoService.pageOrderList(queryDTO);
        return Result.success(PageResultVO.of(orderVOPage.getRecords(), orderVOPage.getTotal()));
    }

    @GetMapping("/orders/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id, HttpSession session) {
        checkAdminPermission(session);
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.failed("订单不存在");
        }
        return Result.success(orderVO);
    }

    @PutMapping("/orders/{id}/updateStatus")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        checkAdminPermission(session);
        orderInfoService.updateOrderStatus(id, status);
        return Result.success();
    }

    @GetMapping("/requirements")
    public Result<PageResultVO<RequirementVO>> getRequirements(RequirementQueryDTO queryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<RequirementVO> page = requirementService.pageList(queryDTO, null);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/requirements/{id}")
    public Result<RequirementVO> getRequirementDetail(@PathVariable Long id, HttpSession session) {
        checkAdminPermission(session);
        RequirementVO vo = requirementService.getDetailById(id);
        if (vo == null) {
            return Result.failed("需求不存在");
        }
        return Result.success(vo);
    }

    @PutMapping("/requirements/{id}/updateStatus")
    public Result<Void> updateRequirementStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO, HttpSession session) {
        checkAdminPermission(session);
        requirementService.updateStatus(id, updateStatusDTO.getStatus());
        return Result.success();
    }

    @PostMapping("/statistics/sync")
    public Result<Void> syncStatistics(HttpSession session) {
        checkAdminPermission(session);
        statisticsSyncService.syncDailyStatisticsToApiInfo();
        return Result.success();
    }

    @GetMapping("/users/export")
    public void exportUsers(UserQueryDTO userQueryDTO, HttpSession session, HttpServletResponse response) throws IOException {
        checkAdminPermission(session);
        userQueryDTO.setPageNum(1);
        userQueryDTO.setPageSize(Integer.MAX_VALUE);
        IPage<UserVO> userPage = userService.pageUserList(userQueryDTO);
        List<UserVO> users = userPage.getRecords();

        List<Map<String, Object>> rows = new ArrayList<>();
        for (UserVO user : users) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", user.getId());
            row.put("用户名", user.getUsername());
            row.put("邮箱", user.getEmail());
            row.put("手机号", user.getPhone());
            row.put("状态", user.getStatus() == 1 ? "正常" : "冻结");
            row.put("冻结原因", user.getFreezeReason() != null ? user.getFreezeReason() : "");
            row.put("是否管理员", user.getIsAdmin() != null && user.getIsAdmin() == 1 ? "是" : "否");
            row.put("注册时间", user.getCreateTime() != null ? user.getCreateTime().toString() : "");
            rows.add(row);
        }

        String fileName = "users_export.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(rows, true);
        writer.flush(response.getOutputStream(), true);
        writer.close();
    }

}
