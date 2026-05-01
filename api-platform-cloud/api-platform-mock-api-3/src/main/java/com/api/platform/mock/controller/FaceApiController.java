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
 * 人脸识别模拟API —— 模拟人脸检测、人脸比对等接口
 *
 * 路由前缀：/api/v1/face
 * 网关通过 targetUrl 将 /invoke/face 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/face")
public class FaceApiController {

    @PostMapping("/detect")
    public Result<FaceDetectResult> detect(@RequestBody Map<String, Object> params) {
        log.info("Face detect request: {}", params);
        FaceDetectResult result = new FaceDetectResult();
        int count = RandomUtil.randomInt(1, 6);
        List<FaceRect> faces = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            FaceRect rect = new FaceRect();
            rect.setX(RandomUtil.randomInt(0, 800));
            rect.setY(RandomUtil.randomInt(0, 600));
            rect.setWidth(RandomUtil.randomInt(50, 200));
            rect.setHeight(RandomUtil.randomInt(50, 200));
            rect.setConfidence(RandomUtil.randomDouble(0.85, 0.99));
            faces.add(rect);
        }
        result.setFaces(faces);
        result.setCount(count);
        log.info("Face detect result: {} faces detected", count);
        return Result.success(result);
    }

    @PostMapping("/compare")
    public Result<FaceCompareResult> compare(@RequestBody Map<String, Object> params) {
        log.info("Face compare request: {}", params);
        FaceCompareResult result = new FaceCompareResult();
        double score = RandomUtil.randomDouble(0.3, 0.99);
        result.setScore((float) score);
        result.setIs_same(score > 0.8);
        log.info("Face compare result: score={}, is_same={}", result.getScore(), result.getIs_same());
        return Result.success(result);
    }

    @PostMapping("/search")
    public Result<FaceSearchResult> search(@RequestBody Map<String, Object> params) {
        log.info("Face search request: {}", params);
        FaceSearchResult result = new FaceSearchResult();
        int matchCount = RandomUtil.randomInt(1, 6);
        List<FaceMatch> matches = new ArrayList<>();
        for (int i = 0; i < matchCount; i++) {
            FaceMatch match = new FaceMatch();
            match.setUser_id("user_" + RandomUtil.randomString(8));
            match.setScore((float) RandomUtil.randomDouble(0.6, 0.99));
            matches.add(match);
        }
        result.setResults(matches);
        log.info("Face search result: {} matches found", matchCount);
        return Result.success(result);
    }

    @PostMapping("/liveness")
    public Result<FaceLivenessResult> liveness(@RequestBody Map<String, Object> params) {
        log.info("Face liveness request: {}", params);
        FaceLivenessResult result = new FaceLivenessResult();
        boolean isLive = RandomUtil.randomBoolean();
        result.setIs_live(isLive);
        result.setScore((float) (isLive ? RandomUtil.randomDouble(0.85, 0.99) : RandomUtil.randomDouble(0.1, 0.4)));
        log.info("Face liveness result: is_live={}, score={}", result.getIs_live(), result.getScore());
        return Result.success(result);
    }

    @PostMapping("/attribute")
    public Result<FaceAttributeResult> attribute(@RequestBody Map<String, Object> params) {
        log.info("Face attribute request: {}", params);
        FaceAttributeResult result = new FaceAttributeResult();
        result.setAge(RandomUtil.randomInt(18, 65));
        result.setGender(RandomUtil.randomEle(new String[]{"male", "female"}));
        result.setEmotion(RandomUtil.randomEle(new String[]{"happy", "sad", "angry", "surprise", "neutral", "fear", "disgust"}));
        log.info("Face attribute result: age={}, gender={}, emotion={}", result.getAge(), result.getGender(), result.getEmotion());
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result<FaceRegisterResult> register(@RequestBody Map<String, Object> params) {
        log.info("Face register request: {}", params);
        FaceRegisterResult result = new FaceRegisterResult();
        result.setFace_id("face_" + RandomUtil.randomString(16));
        log.info("Face register result: face_id={}", result.getFace_id());
        return Result.success(result);
    }

    @Data
    public static class FaceRect {
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
        private Double confidence;
    }

    @Data
    public static class FaceDetectResult {
        private List<FaceRect> faces;
        private Integer count;
    }

    @Data
    public static class FaceCompareResult {
        private Float score;
        private Boolean is_same;
    }

    @Data
    public static class FaceMatch {
        private String user_id;
        private Float score;
    }

    @Data
    public static class FaceSearchResult {
        private List<FaceMatch> results;
    }

    @Data
    public static class FaceLivenessResult {
        private Boolean is_live;
        private Float score;
    }

    @Data
    public static class FaceAttributeResult {
        private Integer age;
        private String gender;
        private String emotion;
    }

    @Data
    public static class FaceRegisterResult {
        private String face_id;
    }
}
