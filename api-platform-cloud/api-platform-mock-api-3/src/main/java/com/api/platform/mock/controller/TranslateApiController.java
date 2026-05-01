package com.api.platform.mock.controller;

import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
/**
 * 翻译服务模拟API —— 模拟多语言翻译接口
 *
 * 路由前缀：/api/v1/translate
 * 网关通过 targetUrl 将 /invoke/translate 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/translate")
public class TranslateApiController {

    @PostMapping("/general")
    public Result<TranslateGeneralResult> general(@RequestBody Map<String, Object> params) {
        log.info("Translate general request: {}", params);
        TranslateGeneralResult result = new TranslateGeneralResult();
        result.setTranslated(RandomUtil.randomEle(new String[]{"Hello World", "Good morning", "Thank you", "Welcome", "How are you"}));
        result.setFrom(RandomUtil.randomEle(new String[]{"zh", "en", "ja", "ko", "fr", "de"}));
        result.setTo(RandomUtil.randomEle(new String[]{"en", "zh", "ja", "ko", "fr", "de"}));
        log.info("Translate general result: from={}, to={}", result.getFrom(), result.getTo());
        return Result.success(result);
    }

    @PostMapping("/document")
    public Result<TranslateDocumentResult> document(@RequestBody Map<String, Object> params) {
        log.info("Translate document request: {}", params);
        TranslateDocumentResult result = new TranslateDocumentResult();
        result.setTranslated_url("https://translate.example.com/docs/" + RandomUtil.randomString(12) + ".pdf");
        log.info("Translate document result: translated_url={}", result.getTranslated_url());
        return Result.success(result);
    }

    @PostMapping("/image")
    public Result<TranslateImageResult> image(@RequestBody Map<String, Object> params) {
        log.info("Translate image request: {}", params);
        TranslateImageResult result = new TranslateImageResult();
        result.setTranslated_url("https://translate.example.com/images/" + RandomUtil.randomString(12) + ".png");
        log.info("Translate image result: translated_url={}", result.getTranslated_url());
        return Result.success(result);
    }

    @PostMapping("/voice")
    public Result<TranslateVoiceResult> voice(@RequestBody Map<String, Object> params) {
        log.info("Translate voice request: {}", params);
        TranslateVoiceResult result = new TranslateVoiceResult();
        result.setTranslated_audio_url("https://translate.example.com/audio/" + RandomUtil.randomString(12) + ".mp3");
        log.info("Translate voice result: translated_audio_url={}", result.getTranslated_audio_url());
        return Result.success(result);
    }

    @PostMapping("/professional")
    public Result<TranslateProfessionalResult> professional(@RequestBody Map<String, Object> params) {
        log.info("Translate professional request: {}", params);
        TranslateProfessionalResult result = new TranslateProfessionalResult();
        result.setTranslated(RandomUtil.randomEle(new String[]{"Professional translation result", "Technical document translated", "Medical report translation", "Legal document translation"}));
        result.setField(RandomUtil.randomEle(new String[]{"medical", "legal", "technical", "financial", "scientific"}));
        log.info("Translate professional result: field={}", result.getField());
        return Result.success(result);
    }

    @PostMapping("/batch")
    public Result<TranslateBatchResult> batch(@RequestBody Map<String, Object> params) {
        log.info("Translate batch request: {}", params);
        TranslateBatchResult result = new TranslateBatchResult();
        int batchSize = RandomUtil.randomInt(2, 6);
        List<String> translated = new ArrayList<>();
        String[] sampleTranslations = {"Hello", "Goodbye", "Thank you", "Please", "Sorry", "Welcome", "Good morning", "Good night"};
        for (int i = 0; i < batchSize; i++) {
            translated.add(sampleTranslations[RandomUtil.randomInt(sampleTranslations.length)]);
        }
        result.setTranslated(translated);
        log.info("Translate batch result: {} items translated", batchSize);
        return Result.success(result);
    }

    @PostMapping("/realtime")
    public Result<TranslateRealtimeResult> realtime(@RequestBody Map<String, Object> params) {
        log.info("Translate realtime request: {}", params);
        TranslateRealtimeResult result = new TranslateRealtimeResult();
        result.setTranslated(RandomUtil.randomEle(new String[]{"Real-time translation", "Live translation result", "Instant translation", "Streaming translation"}));
        log.info("Translate realtime result: translated={}", result.getTranslated());
        return Result.success(result);
    }

    @Data
    public static class TranslateGeneralResult {
        private String translated;
        private String from;
        private String to;
    }

    @Data
    public static class TranslateDocumentResult {
        private String translated_url;
    }

    @Data
    public static class TranslateImageResult {
        private String translated_url;
    }

    @Data
    public static class TranslateVoiceResult {
        private String translated_audio_url;
    }

    @Data
    public static class TranslateProfessionalResult {
        private String translated;
        private String field;
    }

    @Data
    public static class TranslateBatchResult {
        private List<String> translated;
    }

    @Data
    public static class TranslateRealtimeResult {
        private String translated;
    }
}
