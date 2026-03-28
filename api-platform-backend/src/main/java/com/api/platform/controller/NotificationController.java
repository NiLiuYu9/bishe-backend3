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

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/unread")
    public Result<PageResultVO<NotificationVO>> getUnreadList(NotificationQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<NotificationVO> page = notificationService.getUnreadList(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/list")
    public Result<PageResultVO<NotificationVO>> getAllList(NotificationQueryDTO queryDTO, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<NotificationVO> page = notificationService.getAllList(userId, queryDTO);
        return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount(HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PostMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        notificationService.markAsRead(userId, id);
        return Result.success();
    }

    @PostMapping("/read/all")
    public Result<Void> markAllAsRead(@RequestParam(required = false) String type, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        notificationService.markAllAsRead(userId, type);
        return Result.success();
    }

}
