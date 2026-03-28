package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.service.UserTagService;
import com.api.platform.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/user-tag")
@Validated
public class UserTagController {

    @Autowired
    private UserTagService userTagService;

    @GetMapping("/list")
    public Result<List<String>> getUserTags(HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        List<String> tags = userTagService.getTagsByUserId(userId);
        return Result.success(tags);
    }

    @PostMapping("/save")
    public Result<Void> saveUserTags(@RequestBody List<String> tags, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        userTagService.saveUserTags(userId, tags);
        return Result.success();
    }

    @PostMapping("/add")
    public Result<Void> addUserTag(@RequestBody TagRequest request, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        userTagService.addUserTag(userId, request.getTagName());
        return Result.success();
    }

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
