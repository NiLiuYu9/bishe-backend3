package com.api.platform.mock.controller;

import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping
public class MockApiController {

    @GetMapping("/weather")
    public Result<WeatherData> getWeather() {
        log.info("MockAPI-1 收到天气查询请求");
        WeatherData data = new WeatherData();
        data.setCity("北京");
        data.setTemperature(RandomUtil.randomInt(-10, 35));
        data.setWeather(new String[]{"晴", "多云", "阴", "小雨", "大雨", "雪"}[RandomUtil.randomInt(6)]);
        data.setHumidity(RandomUtil.randomInt(30, 90));
        data.setUpdateTime(LocalDateTime.now());
        data.setSource("mock-api-1:8081");
        log.info("天气查询结果: {}", data);
        return Result.success(data);
    }

    @Data
    public static class WeatherData {
        private String city;
        private Integer temperature;
        private String weather;
        private Integer humidity;
        private LocalDateTime updateTime;
        private String source;
    }
}
