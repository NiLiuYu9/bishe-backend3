package com.api.platform.mock.controller;

import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
/**
 * 短信服务模拟API —— 模拟短信发送、短信验证码等接口
 *
 * 路由前缀：/api/v1/sms
 * 网关通过 targetUrl 将 /invoke/sms 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/sms")
public class SmsApiController {

    @PostMapping("/code")
    public Result<SmsCodeData> sendCode(@RequestBody Map<String, Object> params) {
        log.info("SmsAPI - send code request, params: {}", params);
        SmsCodeData data = new SmsCodeData();
        data.setCode(String.valueOf(RandomUtil.randomInt(100000, 999999)));
        data.setExpire(RandomUtil.randomInt(60, 600));
        log.info("SmsAPI - send code result: code={}, expire={}", data.getCode(), data.getExpire());
        return Result.success(data);
    }

    @PostMapping("/notify")
    public Result<SmsNotifyData> sendNotify(@RequestBody Map<String, Object> params) {
        log.info("SmsAPI - send notify request, params: {}", params);
        SmsNotifyData data = new SmsNotifyData();
        data.setMsgId("MSG" + RandomUtil.randomNumbers(16));
        log.info("SmsAPI - send notify result: msgId={}", data.getMsgId());
        return Result.success(data);
    }

    @PostMapping("/marketing")
    public Result<SmsMarketingData> sendMarketing(@RequestBody Map<String, Object> params) {
        log.info("SmsAPI - send marketing request, params: {}", params);
        SmsMarketingData data = new SmsMarketingData();
        int total = RandomUtil.randomInt(100, 10000);
        data.setSuccessCount(RandomUtil.randomInt(total / 2, total));
        data.setFailCount(total - data.getSuccessCount());
        log.info("SmsAPI - send marketing result: success={}, fail={}", data.getSuccessCount(), data.getFailCount());
        return Result.success(data);
    }

    @PostMapping("/voice")
    public Result<SmsVoiceData> sendVoice(@RequestBody Map<String, Object> params) {
        log.info("SmsAPI - send voice request, params: {}", params);
        SmsVoiceData data = new SmsVoiceData();
        data.setCallId("CALL" + RandomUtil.randomNumbers(16));
        log.info("SmsAPI - send voice result: callId={}", data.getCallId());
        return Result.success(data);
    }

    @PostMapping("/international")
    public Result<SmsInternationalData> sendInternational(@RequestBody Map<String, Object> params) {
        log.info("SmsAPI - send international request, params: {}", params);
        SmsInternationalData data = new SmsInternationalData();
        data.setMsgId("INTL" + RandomUtil.randomNumbers(16));
        log.info("SmsAPI - send international result: msgId={}", data.getMsgId());
        return Result.success(data);
    }

    @Data
    public static class SmsCodeData {
        private String code;
        private Integer expire;
    }

    @Data
    public static class SmsNotifyData {
        private String msgId;
    }

    @Data
    public static class SmsMarketingData {
        private Integer successCount;
        private Integer failCount;
    }

    @Data
    public static class SmsVoiceData {
        private String callId;
    }

    @Data
    public static class SmsInternationalData {
        private String msgId;
    }
}
