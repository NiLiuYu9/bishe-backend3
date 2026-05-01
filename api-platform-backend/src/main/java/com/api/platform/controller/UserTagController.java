package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.service.UserTagService;
import com.api.platform.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户标签控制器 —— 处理用户技能标签的增删查及批量保存请求
 *
 * 路由前缀：/user-tag
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 *
 * 用户标签用于智能匹配推荐，标签越精准推荐越准确
 */
@RestController
@RequestMapping("/user-tag")
@Validated
public class UserTagController {

    @Autowired
    private UserTagService userTagService;

    /**
     * 获取当前用户的标签列表
     *
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;List&lt;String&gt;&gt; 标签名称列表
     */
    @GetMapping("/list")
    public Result<List<String>> getUserTags(HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        List<String> tags = userTagService.getTagsByUserId(userId);
        return Result.success(tags);
    }

    /**
     * 批量保存用户标签（全量覆盖）
     *
     * 传入的标签列表会完全替换现有标签
     *
     * @param tags    标签名称列表
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 保存成功无返回数据
     */
    @PostMapping("/save")
    public Result<Void> saveUserTags(@RequestBody List<String> tags, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        userTagService.saveUserTags(userId, tags);
        return Result.success();
    }

    /**
     * 添加单个用户标签
     *
     * @param request 标签请求（tagName 标签名称）
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 添加成功无返回数据
     */
    @PostMapping("/add")
    public Result<Void> addUserTag(@RequestBody TagRequest request, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        userTagService.addUserTag(userId, request.getTagName());
        return Result.success();
    }

    /**
     * 移除单个用户标签
     *
     * @param tagName 标签名称
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 移除成功无返回数据
     */
    @DeleteMapping("/remove")
    public Result<Void> removeUserTag(@RequestParam String tagName, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        userTagService.removeUserTag(userId, tagName);
        return Result.success();
    }

    @lombok.Data
    public static class TagRequest {
        private String tagName;
    }

}
