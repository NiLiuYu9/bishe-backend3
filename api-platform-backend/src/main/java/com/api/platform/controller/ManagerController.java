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

/**
 * 管理后台控制器 —— 处理用户管理、API审核、订单管理、需求管理、分类管理等管理员操作请求
 *
 * 路由前缀：/admin
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 所有接口均需管理员权限，通过 checkAdminPermission 校验
 */
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

    /**
     * 校验管理员权限，非管理员抛出 BusinessException(403)
     *
     * @param session HttpSession，用于获取管理员标识
     */
    private void checkAdminPermission(HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            throw new BusinessException(403, "无权限访问，仅管理员可操作");
        }
    }

    /**
     * 获取用户列表（管理员）
     *
     * @param userQueryDTO 查询条件（用户名、状态、分页参数）
     * @param session      HttpSession，用于验证管理员权限
     * @return Result&lt;PageResultVO&lt;UserVO&gt;&gt; 分页的用户列表
     */
    @GetMapping("/users")
    public Result<PageResultVO<UserVO>> getUserList(UserQueryDTO userQueryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<UserVO> userPage = userService.pageUserList(userQueryDTO);
        return Result.success(PageResultVO.of(userPage.getRecords(), userPage.getTotal()));
    }

    /**
     * 获取API分类列表（管理员，含所有状态）
     *
     * @param queryDTO 查询条件（状态、分页参数）
     * @param session  HttpSession，用于验证管理员权限
     * @return Result&lt;PageResultVO&lt;ApiTypeVO&gt;&gt; 分页的分类列表
     */
    @GetMapping("/api-types")
    public Result<PageResultVO<ApiTypeVO>> getApiTypes(ApiTypeQueryDTO queryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<ApiTypeVO> apiTypeVOPage = apiTypeService.pageApiTypes(queryDTO);
        return Result.success(PageResultVO.of(apiTypeVOPage.getRecords(), apiTypeVOPage.getTotal()));
    }

    /**
     * 获取所有API分类（不分页，用于下拉选择）
     *
     * @param session HttpSession，用于验证管理员权限
     * @return Result&lt;List&lt;ApiType&gt;&gt; 所有分类列表
     */
    @GetMapping("/api-types/all")
    public Result<List<ApiType>> getAllApiTypes(HttpSession session) {
        checkAdminPermission(session);
        List<ApiType> apiTypes = apiTypeService.getAllTypes();
        return Result.success(apiTypes);
    }

    /**
     * 创建API分类
     *
     * @param apiTypeDTO 分类创建表单（名称、描述）
     * @param session    HttpSession，用于验证管理员权限
     * @return Result&lt;ApiType&gt; 创建成功的分类信息
     */
    @PostMapping("/api-types")
    public Result<ApiType> createApiType(@Validated @RequestBody ApiTypeDTO apiTypeDTO, HttpSession session) {
        checkAdminPermission(session);
        ApiType apiType = new ApiType();
        BeanUtils.copyProperties(apiTypeDTO, apiType);
        apiTypeService.createType(apiType);
        return Result.success(apiType);
    }

    /**
     * 更新API分类
     *
     * @param id         分类ID
     * @param apiTypeDTO 分类更新表单（名称、描述）
     * @param session    HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 更新成功无返回数据
     */
    @PutMapping("/api-types/{id}")
    public Result<Void> updateApiType(@PathVariable Long id, @Validated @RequestBody ApiTypeDTO apiTypeDTO, HttpSession session) {
        checkAdminPermission(session);
        ApiType apiType = new ApiType();
        apiType.setId(id);
        BeanUtils.copyProperties(apiTypeDTO, apiType);
        apiTypeService.updateType(apiType);
        return Result.success();
    }

    /**
     * 更新API分类状态（启用/禁用）
     *
     * @param id               分类ID
     * @param updateStatusDTO  状态变更表单（status: active/inactive）
     * @param session          HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 操作成功无返回数据
     */
    @PutMapping("/api-types/{id}/updateStatus")
    public Result<Void> updateApiTypeStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO, HttpSession session) {
        checkAdminPermission(session);
        apiTypeService.updateStatus(id, updateStatusDTO.getStatus());
        return Result.success();
    }

    /**
     * 冻结用户
     *
     * @param id            用户ID
     * @param freezeUserDTO 冻结表单（freezeReason 冻结原因）
     * @param session       HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 冻结成功无返回数据
     */
    @PutMapping("/users/{id}/freeze")
    public Result<Void> freeze(@PathVariable Long id, @RequestBody FreezeUserDTO freezeUserDTO, HttpSession session) {
        checkAdminPermission(session);
        userService.freezeUser(id, freezeUserDTO);
        return Result.success();
    }

    /**
     * 解冻用户
     *
     * @param id      用户ID
     * @param session HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 解冻成功无返回数据
     */
    @PutMapping("/users/{id}/unfreeze")
    public Result<Void> unfreeze(@PathVariable Long id, HttpSession session) {
        checkAdminPermission(session);
        userService.unfreezeUser(id);
        return Result.success();
    }

    /**
     * 获取API列表（管理员，含所有状态）
     *
     * @param queryDTO 查询条件（状态、关键词、分页参数）
     * @param session  HttpSession，用于验证管理员权限
     * @return Result&lt;PageResultVO&lt;ApiVO&gt;&gt; 分页的API列表
     */
    @GetMapping("/apis")
    public Result<PageResultVO<ApiVO>> getApis(ApiQueryDTO queryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    /**
     * 审核API（通过/拒绝）
     *
     * @param id          API ID
     * @param auditApiDTO 审核表单（status: approved/rejected、审核意见）
     * @param session     HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 审核成功无返回数据
     */
    @PutMapping("/apis/{id}/updateStatus")
    public Result<Void> updateApiStatus(@PathVariable Long id, @Validated @RequestBody AuditApiDTO auditApiDTO, HttpSession session) {
        checkAdminPermission(session);
        apiInfoService.auditApi(id, auditApiDTO);
        return Result.success();
    }

    /**
     * 获取订单列表（管理员）
     *
     * @param queryDTO 查询条件（状态、分页参数）
     * @param session  HttpSession，用于验证管理员权限
     * @return Result&lt;PageResultVO&lt;OrderVO&gt;&gt; 分页的订单列表
     */
    @GetMapping("/orders")
    public Result<PageResultVO<OrderVO>> getOrders(OrderQueryDTO queryDTO, HttpSession session) {
        checkAdminPermission(session);
        IPage<OrderVO> orderVOPage = orderInfoService.pageOrderList(queryDTO);
        return Result.success(PageResultVO.of(orderVOPage.getRecords(), orderVOPage.getTotal()));
    }

    /**
     * 获取订单详情（管理员）
     *
     * @param id      订单ID
     * @param session HttpSession，用于验证管理员权限
     * @return Result&lt;OrderVO&gt; 订单详情
     */
    @GetMapping("/orders/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id, HttpSession session) {
        checkAdminPermission(session);
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.failed("订单不存在");
        }
        return Result.success(orderVO);
    }

    /**
     * 更新订单状态（管理员）
     *
     * @param id      订单ID
     * @param status  目标状态
     * @param session HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 操作成功无返回数据
     */
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

    /**
     * 获取需求详情（管理员）
     *
     * @param id      需求ID
     * @param session HttpSession，用于验证管理员权限
     * @return Result&lt;RequirementVO&gt; 需求详情
     */
    @GetMapping("/requirements/{id}")
    public Result<RequirementVO> getRequirementDetail(@PathVariable Long id, HttpSession session) {
        checkAdminPermission(session);
        RequirementVO vo = requirementService.getDetailById(id);
        if (vo == null) {
            return Result.failed("需求不存在");
        }
        return Result.success(vo);
    }

    /**
     * 更新需求状态（管理员）
     *
     * @param id               需求ID
     * @param updateStatusDTO  状态变更表单（status）
     * @param session          HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 操作成功无返回数据
     */
    @PutMapping("/requirements/{id}/updateStatus")
    public Result<Void> updateRequirementStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO, HttpSession session) {
        checkAdminPermission(session);
        requirementService.updateStatus(id, updateStatusDTO.getStatus());
        return Result.success();
    }

    /**
     * 手动同步统计数据（Redis → MySQL）
     *
     * 将 Redis 中缓存的每日调用统计同步到 api_info 表的调用次数字段
     *
     * @param session HttpSession，用于验证管理员权限
     * @return Result&lt;Void&gt; 同步成功无返回数据
     */
    @PostMapping("/statistics/sync")
    public Result<Void> syncStatistics(HttpSession session) {
        checkAdminPermission(session);
        statisticsSyncService.syncDailyStatisticsToApiInfo();
        return Result.success();
    }

    /**
     * 导出用户列表为Excel文件
     *
     * 使用 Apache POI (Hutool封装) 生成 xlsx 文件并写入响应流
     *
     * @param userQueryDTO 查询条件（自动设置全量查询）
     * @param session      HttpSession，用于验证管理员权限
     * @param response     HttpServletResponse，用于写入Excel文件流
     * @throws IOException 写入流异常
     */
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
