package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.WhitelistAddDTO;
import com.api.platform.entity.ApiWhitelist;
import com.api.platform.entity.User;
import com.api.platform.mapper.UserMapper;
import com.api.platform.service.ApiWhitelistService;
import com.api.platform.utils.SessionUtils;
import com.api.platform.vo.PageResultVO;
import com.api.platform.vo.WhitelistUserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/whitelist")
public class ApiWhitelistController {

    @Autowired
    private ApiWhitelistService whitelistService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/add/{apiId}")
    public Result<Void> addWhitelist(@PathVariable Long apiId, 
                                     @Validated @RequestBody WhitelistAddDTO addDTO, 
                                     HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        whitelistService.addWhitelistUsers(apiId, userId, addDTO.getUsernames());
        return Result.success();
    }

    @DeleteMapping("/remove/{apiId}/{userId}")
    public Result<Void> removeWhitelist(@PathVariable Long apiId, 
                                        @PathVariable Long userId, 
                                        HttpSession session) {
        Long operatorId = SessionUtils.getCurrentUserId(session);
        whitelistService.removeWhitelistUser(apiId, userId, operatorId);
        return Result.success();
    }

    @GetMapping("/list/{apiId}")
    public Result<PageResultVO<WhitelistUserVO>> getWhitelistList(
            @PathVariable Long apiId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<ApiWhitelist> page = whitelistService.getWhitelistPage(apiId, pageNum, pageSize);
        List<ApiWhitelist> records = page.getRecords();
        
        Set<Long> userIds = records.stream()
                .map(ApiWhitelist::getUserId)
                .collect(Collectors.toSet());
        
        Map<Long, String> usernameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            usernameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }
        
        Map<Long, String> finalUsernameMap = usernameMap;
        List<WhitelistUserVO> voList = records.stream()
                .map(whitelist -> convertToVO(whitelist, finalUsernameMap.get(whitelist.getUserId())))
                .collect(Collectors.toList());
        return Result.success(PageResultVO.of(voList, page.getTotal()));
    }

    @PostMapping("/enable/{apiId}")
    public Result<Void> enableWhitelist(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        whitelistService.enableWhitelist(apiId, userId);
        return Result.success();
    }

    @PostMapping("/disable/{apiId}")
    public Result<Void> disableWhitelist(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        whitelistService.disableWhitelist(apiId, userId);
        return Result.success();
    }

    private WhitelistUserVO convertToVO(ApiWhitelist whitelist, String username) {
        WhitelistUserVO vo = new WhitelistUserVO();
        vo.setId(whitelist.getId());
        vo.setUserId(whitelist.getUserId());
        vo.setUsername(username);
        vo.setCreateTime(whitelist.getCreateTime());
        return vo;
    }

}
