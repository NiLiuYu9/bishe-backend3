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
 * 图像处理模拟API —— 模拟图像识别、图像审核等接口
 *
 * 路由前缀：/api/v1/image
 * 网关通过 targetUrl 将 /invoke/image 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/image")
public class ImageApiController {

    @PostMapping("/recognition")
    public Result<RecognitionResult> recognition(@RequestBody Map<String, Object> params) {
        log.info("图像识别请求: {}", params);
        RecognitionResult result = new RecognitionResult();
        String[] labels = {"猫", "狗", "汽车", "建筑", "人物", "花朵", "食物", "风景"};
        result.setLabel(labels[RandomUtil.randomInt(labels.length)]);
        result.setConfidence(RandomUtil.randomFloat(70, 99) / 100f);
        log.info("图像识别结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/classify")
    public Result<ClassifyResult> classify(@RequestBody Map<String, Object> params) {
        log.info("图像分类请求: {}", params);
        ClassifyResult result = new ClassifyResult();
        String[] categoryNames = {"动物", "植物", "交通工具", "建筑", "人物", "食物", "自然风光", "电子产品"};
        List<ClassifyResult.CategoryItem> categories = new ArrayList<>();
        int count = RandomUtil.randomInt(3, 6);
        for (int i = 0; i < count; i++) {
            ClassifyResult.CategoryItem item = new ClassifyResult.CategoryItem();
            item.setName(categoryNames[RandomUtil.randomInt(categoryNames.length)]);
            item.setScore(RandomUtil.randomFloat(50, 99) / 100f);
            categories.add(item);
        }
        result.setCategories(categories);
        log.info("图像分类结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/quality")
    public Result<QualityResult> quality(@RequestBody Map<String, Object> params) {
        log.info("图像质量评估请求: {}", params);
        QualityResult result = new QualityResult();
        result.setScore(RandomUtil.randomFloat(60, 99) / 100f);
        QualityResult.Metrics metrics = new QualityResult.Metrics();
        metrics.setSharpness(RandomUtil.randomFloat(50, 99) / 100f);
        metrics.setBrightness(RandomUtil.randomFloat(40, 95) / 100f);
        metrics.setContrast(RandomUtil.randomFloat(45, 98) / 100f);
        result.setMetrics(metrics);
        log.info("图像质量评估结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/style")
    public Result<StyleResult> style(@RequestBody Map<String, Object> params) {
        log.info("图像风格转换请求: {}", params);
        StyleResult result = new StyleResult();
        String[] styles = {"oil-painting", "watercolor", "sketch", "cartoon", "pixel-art", "impressionist"};
        result.setResultUrl("https://mock-api.example.com/images/style/" + styles[RandomUtil.randomInt(styles.length)] + "/" + RandomUtil.randomString(8) + ".png");
        log.info("图像风格转换结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/enhance")
    public Result<EnhanceResult> enhance(@RequestBody Map<String, Object> params) {
        log.info("图像增强请求: {}", params);
        EnhanceResult result = new EnhanceResult();
        result.setEnhancedUrl("https://mock-api.example.com/images/enhanced/" + RandomUtil.randomString(8) + ".png");
        log.info("图像增强结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/watermark")
    public Result<WatermarkResult> watermark(@RequestBody Map<String, Object> params) {
        log.info("图像水印请求: {}", params);
        WatermarkResult result = new WatermarkResult();
        result.setResultUrl("https://mock-api.example.com/images/watermark/" + RandomUtil.randomString(8) + ".png");
        log.info("图像水印结果: {}", result);
        return Result.success(result);
    }

    @Data
    public static class RecognitionResult {
        private String label;
        private Float confidence;
    }

    @Data
    public static class ClassifyResult {
        private List<CategoryItem> categories;

        @Data
        public static class CategoryItem {
            private String name;
            private Float score;
        }
    }

    @Data
    public static class QualityResult {
        private Float score;
        private Metrics metrics;

        @Data
        public static class Metrics {
            private Float sharpness;
            private Float brightness;
            private Float contrast;
        }
    }

    @Data
    public static class StyleResult {
        private String resultUrl;
    }

    @Data
    public static class EnhanceResult {
        private String enhancedUrl;
    }

    @Data
    public static class WatermarkResult {
        private String resultUrl;
    }
}
