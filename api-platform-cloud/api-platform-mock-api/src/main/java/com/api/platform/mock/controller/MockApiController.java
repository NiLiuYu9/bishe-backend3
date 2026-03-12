package com.api.platform.mock.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import com.api.platform.common.constant.AuthConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
public class MockApiController {

    @GetMapping("/weather")
    public Result<WeatherData> getWeather(@RequestHeader(value = AuthConstants.USER_ID_HEADER, required = false) Long userId) {
        log.info("收到天气查询请求, userId: {}", userId);
        WeatherData data = new WeatherData();
        data.setCity("北京");
        data.setTemperature(RandomUtil.randomInt(-10, 35));
        data.setWeather(new String[]{"晴", "多云", "阴", "小雨", "大雨", "雪"}[RandomUtil.randomInt(6)]);
        data.setHumidity(RandomUtil.randomInt(30, 90));
        data.setUpdateTime(LocalDateTime.now());
        data.setUserId(userId);
        log.info("天气查询结果: {}", data);
        return Result.success(data);
    }

    @GetMapping("/random")
    public Result<RandomData> getRandom(@RequestHeader(value = AuthConstants.USER_ID_HEADER, required = false) Long userId) {
        log.info("收到随机数据请求, userId: {}", userId);
        RandomData data = new RandomData();
        data.setUuid(IdUtil.fastSimpleUUID());
        data.setRandomNumber(RandomUtil.randomLong());
        data.setRandomString(RandomUtil.randomString(16));
        data.setTimestamp(System.currentTimeMillis());
        data.setUserId(userId);
        log.info("随机数据结果: {}", data);
        return Result.success(data);
    }

    @GetMapping("/user/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader(value = AuthConstants.USER_ID_HEADER, required = false) Long userId) {
        log.info("收到用户信息请求, userId: {}", userId);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", "mock_user_" + userId);
        data.put("nickname", "模拟用户");
        data.put("email", "mock@example.com");
        data.put("createTime", LocalDateTime.now());
        log.info("用户信息结果: {}", data);
        return Result.success(data);
    }

    @Data
    public static class WeatherData {
        private String city;
        private Integer temperature;
        private String weather;
        private Integer humidity;
        private LocalDateTime updateTime;
        private Long userId;
    }

    @Data
    public static class RandomData {
        private String uuid;
        private Long randomNumber;
        private String randomString;
        private Long timestamp;
        private Long userId;
    }
}
