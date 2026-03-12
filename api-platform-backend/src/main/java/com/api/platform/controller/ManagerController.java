package com.api.platform.controller;

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
import com.api.platform.service.ApiInfoService;
import com.api.platform.service.ApiTypeService;
import com.api.platform.service.OrderInfoService;
import com.api.platform.service.RequirementService;
import com.api.platform.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/users")
    public Result<PageResultVO<UserVO>> getUserList(UserQueryDTO userQueryDTO) {
        IPage<UserVO> userPage = userService.pageUserList(userQueryDTO);
        return Result.success(PageResultVO.of(userPage.getRecords(), userPage.getTotal()));
    }

    @GetMapping("/api-types")
    public Result<PageResultVO<ApiTypeVO>> getApiTypes(ApiTypeQueryDTO queryDTO) {
        IPage<ApiTypeVO> apiTypeVOPage = apiTypeService.pageApiTypes(queryDTO);
        return Result.success(PageResultVO.of(apiTypeVOPage.getRecords(), apiTypeVOPage.getTotal()));
    }

    @GetMapping("/api-types/all")
    public Result<List<ApiType>> getAllApiTypes() {
        List<ApiType> apiTypes = apiTypeService.getAllTypes();
        return Result.success(apiTypes);
    }

    @PostMapping("/api-types")
    public Result<ApiType> createApiType(@Validated @RequestBody ApiTypeDTO apiTypeDTO) {
        ApiType apiType = new ApiType();
        BeanUtils.copyProperties(apiTypeDTO, apiType);
        apiTypeService.createType(apiType);
        return Result.success(apiType);
    }

    @PutMapping("/api-types/{id}")
    public Result<Void> updateApiType(@PathVariable Long id, @Validated @RequestBody ApiTypeDTO apiTypeDTO) {
        ApiType apiType = new ApiType();
        apiType.setId(id);
        BeanUtils.copyProperties(apiTypeDTO, apiType);
        apiTypeService.updateType(apiType);
        return Result.success();
    }

    @PutMapping("/api-types/{id}/updateStatus")
    public Result<Void> updateApiTypeStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        apiTypeService.updateStatus(id, updateStatusDTO.getStatus());
        return Result.success();
    }

    @PutMapping("/users/{id}/freeze")
    public Result<Void> freeze(@PathVariable Long id, @RequestBody FreezeUserDTO freezeUserDTO) {
        userService.freezeUser(id, freezeUserDTO);
        return Result.success();
    }

    @PutMapping("/users/{id}/unfreeze")
    public Result<Void> unfreeze(@PathVariable Long id) {
        userService.unfreezeUser(id);
        return Result.success();
    }

    @GetMapping("/apis")
    public Result<PageResultVO<ApiVO>> getApis(ApiQueryDTO queryDTO) {
        IPage<ApiVO> apiVOPage = apiInfoService.getApis(queryDTO);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

    @PutMapping("/apis/{id}/updateStatus")
    public Result<Void> updateApiStatus(@PathVariable Long id, @Validated @RequestBody AuditApiDTO auditApiDTO) {
        apiInfoService.auditApi(id, auditApiDTO);
        return Result.success();
    }

    @GetMapping("/orders")
    public Result<PageResultVO<OrderVO>> getOrders(OrderQueryDTO queryDTO) {
        IPage<OrderVO> orderVOPage = orderInfoService.pageOrderList(queryDTO);
        return Result.success(PageResultVO.of(orderVOPage.getRecords(), orderVOPage.getTotal()));
    }

    @GetMapping("/orders/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        OrderVO orderVO = orderInfoService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.failed("订单不存在");
        }
        return Result.success(orderVO);
    }

    @PutMapping("/orders/{id}/updateStatus")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderInfoService.updateOrderStatus(id, status);
        return Result.success();
    }

    @GetMapping("/requirements")
    public Result<PageResultVO<RequirementVO>> getRequirements(RequirementQueryDTO queryDTO) {
        IPage<RequirementVO> page = requirementService.pageList(queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/requirements/{id}")
    public Result<RequirementVO> getRequirementDetail(@PathVariable Long id) {
        RequirementVO vo = requirementService.getDetailById(id);
        if (vo == null) {
            return Result.failed("需求不存在");
        }
        return Result.success(vo);
    }

    @PutMapping("/requirements/{id}/updateStatus")
    public Result<Void> updateRequirementStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        requirementService.updateStatus(id, updateStatusDTO.getStatus());
        return Result.success();
    }

}
