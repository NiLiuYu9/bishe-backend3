package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.AfterSaleCreateDTO;
import com.api.platform.dto.AfterSaleDecideDTO;
import com.api.platform.dto.AfterSaleMessageDTO;
import com.api.platform.dto.AfterSaleQueryDTO;
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

    @PostMapping("/decide/{id}")
    public Result<Void> decide(@PathVariable Long id, @Validated @RequestBody AfterSaleDecideDTO decideDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        if (!SessionUtils.isAdmin(session)) {
            return Result.failed("无权限操作，仅管理员可裁定");
        }
        afterSaleService.decideAfterSale(userId, id, decideDTO);
        return Result.success();
    }

    /**
     * 获取售后详情
     *
     * 需求方、开发者或管理员可查看，其他用户无权限
     *
     * @param id      售后ID
     * @param session HttpSession，用于获取当前用户ID和管理员标识
     * @return Result&lt;RequirementAfterSaleVO&gt; 售后详情
     */
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

    /**
     * 获取所有售后列表（管理员）
     *
     * 仅管理员可查看全平台售后列表
     *
     * @param queryDTO 查询条件（状态、分页参数）
     * @param session  HttpSession，用于验证管理员权限
     * @return Result&lt;PageResultVO&lt;RequirementAfterSaleVO&gt;&gt; 分页的售后列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<RequirementAfterSaleVO>> getList(AfterSaleQueryDTO queryDTO, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.failed("无权限访问，仅管理员可查看所有售后列表");
        }
        IPage<RequirementAfterSaleVO> page = afterSaleService.pageList(queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    /**
     * 获取当前用户作为需求方的售后列表
     *
     * @param queryDTO 查询条件（状态、分页参数）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;RequirementAfterSaleVO&gt;&gt; 分页的售后列表
     */
    @GetMapping("/my-after-sales")
    public Result<PageResultVO<RequirementAfterSaleVO>> getMyAfterSales(AfterSaleQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<RequirementAfterSaleVO> page = afterSaleService.getMyAfterSales(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    /**
     * 获取当前用户作为开发者的售后列表
     *
     * @param queryDTO 查询条件（状态、分页参数）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;RequirementAfterSaleVO&gt;&gt; 分页的售后列表
     */
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

    /**
     * 发送售后对话消息
     *
     * 需求方、开发者或管理员可在售后对话中发送消息，
     * senderType 自动识别：0=需求方, 1=开发者, 2=管理员
     *
     * @param afterSaleId 售后ID
     * @param messageDTO  消息表单（content 消息内容）
     * @param session     HttpSession，用于获取当前用户ID和管理员标识
     * @return Result&lt;AfterSaleMessageVO&gt; 发送成功的消息信息
     */
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
