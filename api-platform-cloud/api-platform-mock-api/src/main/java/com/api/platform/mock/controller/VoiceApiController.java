package com.api.platform.mock.controller;

import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
/**
 * 语音处理模拟API —— 模拟语音识别、语音合成等接口
 *
 * 路由前缀：/api/v1/voice
 * 网关通过 targetUrl 将 /invoke/voice 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/voice")
public class VoiceApiController {

    @PostMapping("/transcribe")
    public Result<TranscribeResult> transcribe(@RequestBody Map<String, Object> params) {
        log.info("语音转文字请求: {}", params);
        TranscribeResult result = new TranscribeResult();
        String[] texts = {
                "今天天气真不错",
                "请帮我查询一下最近的餐厅",
                "明天下午三点有个会议",
                "这首歌真好听",
                "我想设置一个闹钟"
        };
        result.setText(texts[RandomUtil.randomInt(texts.length)]);
        result.setConfidence(RandomUtil.randomFloat(75, 99) / 100f);
        log.info("语音转文字结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/synthesize")
    public Result<SynthesizeResult> synthesize(@RequestBody Map<String, Object> params) {
        log.info("语音合成请求: {}", params);
        SynthesizeResult result = new SynthesizeResult();
        String[] voices = {"xiaoyun", "xiaogang", "ruoxi", "siqi", "sijia"};
        result.setAudioUrl("https://mock-api.example.com/voice/synthesize/" + voices[RandomUtil.randomInt(voices.length)] + "/" + RandomUtil.randomString(8) + ".mp3");
        log.info("语音合成结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/speaker")
    public Result<SpeakerResult> speaker(@RequestBody Map<String, Object> params) {
        log.info("声纹识别请求: {}", params);
        SpeakerResult result = new SpeakerResult();
        result.setSpeakerId("speaker_" + RandomUtil.randomString(6));
        result.setConfidence(RandomUtil.randomFloat(70, 99) / 100f);
        log.info("声纹识别结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/emotion")
    public Result<EmotionResult> emotion(@RequestBody Map<String, Object> params) {
        log.info("语音情感识别请求: {}", params);
        EmotionResult result = new EmotionResult();
        String[] emotions = {"happy", "sad", "angry", "neutral", "surprised", "fearful"};
        result.setEmotion(emotions[RandomUtil.randomInt(emotions.length)]);
        result.setScore(RandomUtil.randomFloat(60, 99) / 100f);
        log.info("语音情感识别结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/denoise")
    public Result<DenoiseResult> denoise(@RequestBody Map<String, Object> params) {
        log.info("语音降噪请求: {}", params);
        DenoiseResult result = new DenoiseResult();
        result.setCleanAudioUrl("https://mock-api.example.com/voice/denoise/" + RandomUtil.randomString(8) + ".wav");
        log.info("语音降噪结果: {}", result);
        return Result.success(result);
    }

    @Data
    public static class TranscribeResult {
        private String text;
        private Float confidence;
    }

    @Data
    public static class SynthesizeResult {
        private String audioUrl;
    }

    @Data
    public static class SpeakerResult {
        private String speakerId;
        private Float confidence;
    }

    @Data
    public static class EmotionResult {
        private String emotion;
        private Float score;
    }

    @Data
    public static class DenoiseResult {
        private String cleanAudioUrl;
    }
}
