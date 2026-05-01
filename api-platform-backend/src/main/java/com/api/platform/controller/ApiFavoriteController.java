package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.service.ApiFavoriteService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.PageResultVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * API收藏控制器 —— 处理API收藏、取消收藏及收藏列表查询请求
 *
 * 路由前缀：/favorite
 * 所有接口返回统一格式 Result&lt;T&gt;，由 GlobalExceptionHandler 统一处理异常
 */
@RestController
@RequestMapping("/favorite")
public class ApiFavoriteController {

    @Autowired
    private ApiFavoriteService apiFavoriteService;

    /**
     * 收藏API
     *
     * 同一用户对同一API只能收藏一次，重复收藏会抛出异常
     *
     * @param apiId   API ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 收藏成功无返回数据
     */
    @PostMapping("/add/{apiId}")
    public Result<Void> addFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiFavoriteService.addFavorite(userId, apiId);
        return Result.success();
    }

    /**
     * 取消收藏API
     *
     * @param apiId   API ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 取消成功无返回数据
     */
    @DeleteMapping("/remove/{apiId}")
    public Result<Void> removeFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiFavoriteService.removeFavorite(userId, apiId);
        return Result.success();
    }

    /**
     * 检查当前用户是否已收藏指定API
     *
     * 未登录用户返回 false
     *
     * @param apiId   API ID
     * @param session HttpSession，用于获取当前登录用户ID（可为空）
     * @return Result&lt;Boolean&gt; 是否已收藏
     */
    @GetMapping("/check/{apiId}")
    public Result<Boolean> checkFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserIdOrNull(session);
        if (userId == null) {
            return Result.success(false);
        }
        boolean isFavorited = apiFavoriteService.isFavorited(userId, apiId);
        return Result.success(isFavorited);
    }

    /**
     * 获取当前用户的收藏列表
     *
     * @param pageNum  页码，默认1
     * @param pageSize 每页数量，默认10
     * @param session  HttpSession，用于获取当前登录用户ID
     * @return Result&lt;PageResultVO&lt;ApiVO&gt;&gt; 分页的收藏API列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<ApiVO>> getFavoriteList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<ApiVO> apiVOPage = apiFavoriteService.getUserFavorites(userId, pageNum, pageSize);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

}
