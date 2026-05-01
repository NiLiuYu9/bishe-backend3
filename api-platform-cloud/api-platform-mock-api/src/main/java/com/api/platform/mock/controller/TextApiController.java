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
 * 文本处理模拟API —— 模拟文本分析、情感识别等接口
 *
 * 路由前缀：/api/v1/text
 * 网关通过 targetUrl 将 /invoke/text 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/text")
public class TextApiController {

    @PostMapping("/sentiment")
    public Result<SentimentResult> sentiment(@RequestBody Map<String, Object> params) {
        log.info("情感分析请求: {}", params);
        SentimentResult result = new SentimentResult();
        String[] sentiments = {"positive", "negative", "neutral"};
        result.setSentiment(sentiments[RandomUtil.randomInt(sentiments.length)]);
        result.setScore(RandomUtil.randomFloat(60, 99) / 100f);
        log.info("情感分析结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/keywords")
    public Result<KeywordsResult> keywords(@RequestBody Map<String, Object> params) {
        log.info("关键词提取请求: {}", params);
        KeywordsResult result = new KeywordsResult();
        String[] words = {"人工智能", "大数据", "云计算", "区块链", "物联网", "机器学习", "深度学习", "自然语言处理", "计算机视觉", "数据挖掘"};
        List<KeywordsResult.KeywordItem> keywords = new ArrayList<>();
        int count = RandomUtil.randomInt(3, 7);
        for (int i = 0; i < count; i++) {
            KeywordsResult.KeywordItem item = new KeywordsResult.KeywordItem();
            item.setWord(words[RandomUtil.randomInt(words.length)]);
            item.setWeight(RandomUtil.randomFloat(30, 99) / 100f);
            keywords.add(item);
        }
        result.setKeywords(keywords);
        log.info("关键词提取结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/summary")
    public Result<SummaryResult> summary(@RequestBody Map<String, Object> params) {
        log.info("文本摘要请求: {}", params);
        SummaryResult result = new SummaryResult();
        String[] summaries = {
                "本文主要讨论了人工智能技术在各行业的应用前景与挑战",
                "该研究提出了一种基于深度学习的文本分类方法，在多个数据集上取得了显著效果",
                "报告分析了当前市场环境下企业数字化转型的关键策略与实施路径",
                "文章从技术架构和业务价值两个维度阐述了微服务治理的最佳实践"
        };
        result.setSummary(summaries[RandomUtil.randomInt(summaries.length)]);
        log.info("文本摘要结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/classify")
    public Result<ClassifyResult> classify(@RequestBody Map<String, Object> params) {
        log.info("文本分类请求: {}", params);
        ClassifyResult result = new ClassifyResult();
        String[] categories = {"科技", "财经", "体育", "娱乐", "教育", "健康", "政治", "社会"};
        result.setCategory(categories[RandomUtil.randomInt(categories.length)]);
        result.setConfidence(RandomUtil.randomFloat(70, 99) / 100f);
        log.info("文本分类结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/ner")
    public Result<NerResult> ner(@RequestBody Map<String, Object> params) {
        log.info("命名实体识别请求: {}", params);
        NerResult result = new NerResult();
        String[] texts = {"北京", "阿里巴巴", "张三", "2024年", "清华大学", "长江"};
        String[] types = {"LOC", "ORG", "PER", "DATE", "ORG", "LOC"};
        List<NerResult.EntityItem> entities = new ArrayList<>();
        int count = RandomUtil.randomInt(2, 5);
        for (int i = 0; i < count; i++) {
            NerResult.EntityItem item = new NerResult.EntityItem();
            int idx = RandomUtil.randomInt(texts.length);
            item.setText(texts[idx]);
            item.setType(types[idx]);
            item.setStart(RandomUtil.randomInt(0, 50));
            item.setEnd(item.getStart() + texts[idx].length());
            entities.add(item);
        }
        result.setEntities(entities);
        log.info("命名实体识别结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/correct")
    public Result<CorrectResult> correct(@RequestBody Map<String, Object> params) {
        log.info("文本纠错请求: {}", params);
        CorrectResult result = new CorrectResult();
        String[] correctedTexts = {
                "今天天气很好，适合出门散步",
                "人工智能技术正在快速发展",
                "这个项目的实施方案需要进一步优化"
        };
        result.setCorrected(correctedTexts[RandomUtil.randomInt(correctedTexts.length)]);
        String[] originals = {"天汽", "技数", "实旋"};
        String[] corrections = {"天气", "技术", "实施"};
        List<CorrectResult.ErrorItem> errors = new ArrayList<>();
        int errorCount = RandomUtil.randomInt(1, 4);
        for (int i = 0; i < Math.min(errorCount, originals.length); i++) {
            CorrectResult.ErrorItem item = new CorrectResult.ErrorItem();
            item.setOriginal(originals[i]);
            item.setCorrection(corrections[i]);
            item.setPosition(RandomUtil.randomInt(0, 30));
            errors.add(item);
        }
        result.setErrors(errors);
        log.info("文本纠错结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/similarity")
    public Result<SimilarityResult> similarity(@RequestBody Map<String, Object> params) {
        log.info("文本相似度请求: {}", params);
        SimilarityResult result = new SimilarityResult();
        result.setScore(RandomUtil.randomFloat(20, 99) / 100f);
        log.info("文本相似度结果: {}", result);
        return Result.success(result);
    }

    @PostMapping("/filter")
    public Result<FilterResult> filter(@RequestBody Map<String, Object> params) {
        log.info("文本过滤请求: {}", params);
        FilterResult result = new FilterResult();
        String[] filteredTexts = {
                "这是一段经过过滤的干净文本内容",
                "该评论已通过内容安全审核",
                "文本中的敏感信息已被过滤处理"
        };
        result.setFiltered(filteredTexts[RandomUtil.randomInt(filteredTexts.length)]);
        String[] sensitiveWords = {"违规词1", "敏感词2", "不当内容3"};
        List<String> foundWords = new ArrayList<>();
        int wordCount = RandomUtil.randomInt(0, 3);
        for (int i = 0; i < wordCount; i++) {
            foundWords.add(sensitiveWords[RandomUtil.randomInt(sensitiveWords.length)]);
        }
        result.setSensitiveWords(foundWords);
        log.info("文本过滤结果: {}", result);
        return Result.success(result);
    }

    @Data
    public static class SentimentResult {
        private String sentiment;
        private Float score;
    }

    @Data
    public static class KeywordsResult {
        private List<KeywordItem> keywords;

        @Data
        public static class KeywordItem {
            private String word;
            private Float weight;
        }
    }

    @Data
    public static class SummaryResult {
        private String summary;
    }

    @Data
    public static class ClassifyResult {
        private String category;
        private Float confidence;
    }

    @Data
    public static class NerResult {
        private List<EntityItem> entities;

        @Data
        public static class EntityItem {
            private String text;
            private String type;
            private Integer start;
            private Integer end;
        }
    }

    @Data
    public static class CorrectResult {
        private String corrected;
        private List<ErrorItem> errors;

        @Data
        public static class ErrorItem {
            private String original;
            private String correction;
            private Integer position;
        }
    }

    @Data
    public static class SimilarityResult {
        private Float score;
    }

    @Data
    public static class FilterResult {
        private String filtered;
        private List<String> sensitiveWords;
    }
}
