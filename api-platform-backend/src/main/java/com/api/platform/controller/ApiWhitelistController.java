package com.api.platform.controller;

import com.api.platform.common.Result;
import com.api.platform.dto.WhitelistAddDTO;
import com.api.platform.entity.ApiInfo;
import com.api.platform.entity.ApiWhitelist;
import com.api.platform.entity.User;
import com.api.platform.exception.BusinessException;
import com.api.platform.mapper.ApiInfoMapper;
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

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @PostMapping("/add/{apiId}")
    public Result<Void> addWhitelist(@PathVariable Long apiId, 
                                     @Validated @RequestBody WhitelistAddDTO addDTO, 
                                     HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        whitelistService.addWhitelistUsers(apiId, userId, addDTO.getUsernames());
        return Result.success();
    }

    /**
     * 移除白名单用户
     *
     * 仅API创建者可操作
     *
     * @param apiId      API ID
     * @param userId     待移除的用户ID
     * @param session    HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 移除成功无返回数据
     */
    @DeleteMapping("/remove/{apiId}/{userId}")
    public Result<Void> removeWhitelist(@PathVariable Long apiId, 
                                        @PathVariable Long userId, 
                                        HttpSession session) {
        Long operatorId = SessionUtils.getCurrentUserId(session);
        whitelistService.removeWhitelistUser(apiId, userId, operatorId);
        return Result.success();
    }

    /**
     * 获取API白名单列表
     *
     * API创建者和管理员可查看
     *
     * @param apiId    API ID
     * @param pageNum  页码，默认1
     * @param pageSize 每页数量，默认10
     * @param session  HttpSession，用于获取当前用户ID和管理员标识
     * @return Result&lt;PageResultVO&lt;WhitelistUserVO&gt;&gt; 分页的白名单用户列表
     */
    @GetMapping("/list/{apiId}")
    public Result<PageResultVO<WhitelistUserVO>> getWhitelistList(
            @PathVariable Long apiId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        boolean isAdmin = SessionUtils.isAdmin(session);
        ApiInfo apiInfo = apiInfoMapper.selectById(apiId);
        if (apiInfo == null) {
            throw new BusinessException("API不存在");
        }
        if (!isAdmin && !apiInfo.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看该API的白名单");
        }
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

    /**
     * 启用API白名单
     *
     * 仅API创建者可操作，启用后仅白名单中的用户可调用该API
     *
     * @param apiId   API ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 启用成功无返回数据
     */
    @PostMapping("/enable/{apiId}")
    public Result<Void> enableWhitelist(@PathVariable Long apiId, HttpSession session) {
        Long userId = SessionUtils.getCurrentUserId(session);
        whitelistService.enableWhitelist(apiId, userId);
        return Result.success();
    }

    /**
     * 禁用API白名单
     *
     * 仅API创建者可操作，禁用后所有用户均可调用该API
     *
     * @param apiId   API ID
     * @param session HttpSession，用于获取当前登录用户ID
     * @return Result&lt;Void&gt; 禁用成功无返回数据
     */
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
