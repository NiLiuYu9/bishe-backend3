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

@RestController
@RequestMapping("/favorite")
public class ApiFavoriteController {

    @Autowired
    private ApiFavoriteService apiFavoriteService;

    @PostMapping("/add/{apiId}")
    public Result<Void> addFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiFavoriteService.addFavorite(userId, apiId);
        return Result.success();
    }

    @DeleteMapping("/remove/{apiId}")
    public Result<Void> removeFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        apiFavoriteService.removeFavorite(userId, apiId);
        return Result.success();
    }

    @GetMapping("/check/{apiId}")
    public Result<Boolean> checkFavorite(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserIdOrNull(session);
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
        Long userId = SessionUtils.getCurrentUserId(session);
        IPage<ApiVO> apiVOPage = apiFavoriteService.getUserFavorites(userId, pageNum, pageSize);
        return Result.success(PageResultVO.of(apiVOPage.getRecords(), apiVOPage.getTotal()));
    }

}
