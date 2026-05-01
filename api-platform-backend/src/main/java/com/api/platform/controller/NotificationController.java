package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.NotificationQueryDTO;
import com.api.platform.service.NotificationService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.NotificationVO;
import com.api.platform.vo.PageResultVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 通知消息控制器 —— 处理站内通知消息的查询与标记已读请求
 *
 * 路由前缀：/notification
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 通知消息由后端业务事件触发创建，通过 WebSocket 实时推送未读计数到前端
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取未读通知列表
     *
     * @param queryDTO 查询条件（分页参数）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;NotificationVO&gt;&gt; 分页的未读通知列表
     */
    @GetMapping("/unread")
    public Result<PageResultVO<NotificationVO>> getUnreadList(NotificationQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<NotificationVO> page = notificationService.getUnreadList(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    /**
     * 获取所有通知列表（含已读和未读）
     *
     * @param queryDTO 查询条件（分页参数）
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;NotificationVO&gt;&gt; 分页的通知列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<NotificationVO>> getAllList(NotificationQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<NotificationVO> page = notificationService.getAllList(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    /**
     * 获取未读通知数量
     *
     * 用于前端导航栏通知铃铛角标显示
     *
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Long&gt; 未读通知数量
     */
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount(HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单条通知为已读
     *
     * @param id      通知ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 标记成功无返回数据
     */
    @PostMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        notificationService.markAsRead(userId, id);
        return Result.success();
    }

    /**
     * 标记所有通知为已读
     *
     * 可按通知类型批量标记，不传 type 则标记全部
     *
     * @param type    通知类型（可选），不传则标记全部已读
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 标记成功无返回数据
     */
    @PostMapping("/read/all")
    public Result<Void> markAllAsRead(@RequestParam(required = false) String type, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        notificationService.markAllAsRead(userId, type);
        return Result.success();
    }

}
