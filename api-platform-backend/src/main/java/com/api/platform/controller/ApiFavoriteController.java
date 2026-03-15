package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.constants.SessionConstants;
import com.api.platform.service.ApiFavoriteService;
import com.api.platform.vo.ApiVO;
import com.api.platform.vo.PageResultVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/favorite")
public class ApiFavoriteController {

    @Autowired
    private ApiFavoriteService apiFavoriteService;

    @PostMapping("/add/{apiId}")
    public Result<Void> addFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        apiFavoriteService.addFavorite(userId, apiId);
        return Result.success();
    }

    @DeleteMapping("/remove/{apiId}")
    public Result<Void> removeFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        apiFavoriteService.removeFavorite(userId, apiId);
        return Result.success();
    }

    @GetMapping("/check/{apiId}")
    public Result<Boolean> checkFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.success(false);
        }
        boolean isFavorited = apiFavoriteService.isFavorited(userId, apiId);
        return Result.success(isFavorited);
    }

    @GetMapping("/list")
    public Result<PageResultVO<ApiVO>> getFavoriteList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (userId == null) {
            return Result.failed("请先登录");
        }
        IPage<ApiVO> apiVOPage = apiFavoriteService.getUserFavorites(userId, pageNum, pageSize);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

}
