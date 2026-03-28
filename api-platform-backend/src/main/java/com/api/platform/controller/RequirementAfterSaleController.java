package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.AfterSaleCreateDTO;
import com.api.platform.dto.AfterSaleDecideDTO;
import com.api.platform.dto.AfterSaleMessageDTO;
import com.api.platform.dto.AfterSaleQueryDTO;
import com.api.platform.dto.AfterSaleRespondDTO;
import com.api.platform.service.AfterSaleMessageService;
import com.api.platform.service.RequirementAfterSaleService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.AfterSaleMessageVO;
import com.api.platform.vo.PageResultVO;
import com.api.platform.vo.RequirementAfterSaleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/requirement/after-sale")
public class RequirementAfterSaleController {

    @Autowired
    private RequirementAfterSaleService afterSaleService;

    @Autowired
    private AfterSaleMessageService messageService;

    @PostMapping("/create")
    public Result<RequirementAfterSaleVO> create(@Validated @RequestBody AfterSaleCreateDTO createDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        RequirementAfterSaleVO vo = afterSaleService.createAfterSale(userId, createDTO);
        return Result.success(vo);
    }

    @PostMapping("/respond/{id}")
    public Result<Void> respond(@PathVariable Long id, @Validated @RequestBody AfterSaleRespondDTO respondDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        afterSaleService.respondAfterSale(userId, id, respondDTO);
        return Result.success();
    }

    @PostMapping("/decide/{id}")
    public Result<Void> decide(@PathVariable Long id, @Validated @RequestBody AfterSaleDecideDTO decideDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        if (!SessionUtils.isAdmin(session)) {
            return Result.failed("无权限操作，仅管理员可裁定");
        }
        afterSaleService.decideAfterSale(userId, id, decideDTO);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result<RequirementAfterSaleVO> getDetail(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        boolean isAdmin = SessionUtils.isAdmin(session);
        RequirementAfterSaleVO vo = afterSaleService.getDetailByIdWithPermission(id, userId, isAdmin);
        if (vo == null) {
            return Result.failed("售后申请不存在");
        }
        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<PageResultVO<RequirementAfterSaleVO>> getList(AfterSaleQueryDTO queryDTO, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.failed("无权限访问，仅管理员可查看所有售后列表");
        }
        IPage<RequirementAfterSaleVO> page = afterSaleService.pageList(queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/my-after-sales")
    public Result<PageResultVO<RequirementAfterSaleVO>> getMyAfterSales(AfterSaleQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<RequirementAfterSaleVO> page = afterSaleService.getMyAfterSales(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/developer-after-sales")
    public Result<PageResultVO<RequirementAfterSaleVO>> getDeveloperAfterSales(AfterSaleQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<RequirementAfterSaleVO> page = afterSaleService.getDeveloperAfterSales(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/messages/{afterSaleId}")
    public Result<List<AfterSaleMessageVO>> getMessages(@PathVariable Long afterSaleId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        boolean isAdmin = SessionUtils.isAdmin(session);
        List<AfterSaleMessageVO> messages = messageService.getMessagesWithPermissionCheck(afterSaleId, userId, isAdmin);
        return Result.success(messages);
    }

    @PostMapping("/message/send/{afterSaleId}")
    public Result<AfterSaleMessageVO> sendMessage(@PathVariable Long afterSaleId, 
                                                @Validated @RequestBody AfterSaleMessageDTO messageDTO, 
                                                HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        boolean isAdmin = SessionUtils.isAdmin(session);
        AfterSaleMessageVO message = messageService.sendMessageWithPermissionCheck(
            afterSaleId, userId, isAdmin, messageDTO.getContent());
        return Result.success(message);
    }

}
