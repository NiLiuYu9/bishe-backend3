package com.api.platform.mock.controller;

import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
/**
 * 数据服务模拟API —— 模拟数据查询、数据统计等接口
 *
 * 路由前缀：/api/v1/data
 * 网关通过 targetUrl 将 /invoke/data 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/data")
public class DataApiController {

    private static final String[] COMPANY_NAMES = {"华为技术有限公司", "阿里巴巴集团", "腾讯科技", "百度在线", "京东集团", "字节跳动", "美团点评", "拼多多", "小米科技", "网易公司"};
    private static final String[] LEGAL_PERSONS = {"张伟", "王芳", "李强", "刘洋", "陈明", "杨华", "赵军", "黄丽", "周杰", "吴敏"};
    private static final String[] STATUSES = {"在营", "注销", "迁出", "吊销"};
    private static final String[] WEATHER_CONDITIONS = {"晴", "多云", "阴", "小雨", "大雨", "雷阵雨", "雪", "雾"};
    private static final String[] WIND_DIRECTIONS = {"北风", "东北风", "东风", "东南风", "南风", "西南风", "西风", "西北风"};
    private static final String[] ISPS = {"电信", "联通", "移动"};
    private static final String[] PROVINCES = {"北京", "上海", "广东", "浙江", "江苏", "四川", "湖北", "山东", "河南", "福建"};
    private static final String[] CITIES = {"北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "武汉", "济南", "郑州"};
    private static final String[] BANKS = {"工商银行", "建设银行", "农业银行", "中国银行", "交通银行", "招商银行", "浦发银行", "民生银行", "兴业银行", "中信银行"};
    private static final String[] CARD_TYPES = {"借记卡", "信用卡", "准贷记卡"};
    private static final String[] GENDERS = {"男", "女"};

    @GetMapping("/company")
    public Result<CompanyData> getCompanyInfo(@RequestParam Map<String, String> params) {
        log.info("DataAPI - company query, params: {}", params);
        CompanyInfo info = new CompanyInfo();
        info.setName(RandomUtil.randomEle(COMPANY_NAMES));
        info.setCreditCode(RandomUtil.randomString(18).toUpperCase());
        info.setLegalPerson(RandomUtil.randomEle(LEGAL_PERSONS));
        info.setRegisteredCapital(RandomUtil.randomInt(100, 50000) + "万元");
        info.setStatus(RandomUtil.randomEle(STATUSES));
        CompanyData data = new CompanyData();
        data.setCompanyInfo(info);
        log.info("DataAPI - company result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/weather")
    public Result<WeatherData> getWeather(@RequestParam Map<String, String> params) {
        log.info("DataAPI - weather query, params: {}", params);
        WeatherData data = new WeatherData();
        data.setCity(params.getOrDefault("city", RandomUtil.randomEle(CITIES)));
        data.setTemperature(RandomUtil.randomInt(-10, 40));
        data.setCondition(RandomUtil.randomEle(WEATHER_CONDITIONS));
        data.setHumidity(RandomUtil.randomInt(20, 95));
        data.setWindDirection(RandomUtil.randomEle(WIND_DIRECTIONS));
        data.setWindSpeed(RandomUtil.randomInt(1, 30));
        log.info("DataAPI - weather result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/ip")
    public Result<IpData> getIpLocation(@RequestParam Map<String, String> params) {
        log.info("DataAPI - ip location query, params: {}", params);
        LocationInfo location = new LocationInfo();
        location.setIp(params.getOrDefault("ip", RandomUtil.randomInt(1, 255) + "." + RandomUtil.randomInt(0, 255) + "." + RandomUtil.randomInt(0, 255) + "." + RandomUtil.randomInt(1, 254)));
        location.setCountry("中国");
        location.setProvince(RandomUtil.randomEle(PROVINCES));
        location.setCity(RandomUtil.randomEle(CITIES));
        location.setIsp(RandomUtil.randomEle(ISPS));
        IpData data = new IpData();
        data.setLocation(location);
        log.info("DataAPI - ip location result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/phone")
    public Result<PhoneData> getPhoneLocation(@RequestParam Map<String, String> params) {
        log.info("DataAPI - phone location query, params: {}", params);
        PhoneLocationInfo location = new PhoneLocationInfo();
        location.setProvince(RandomUtil.randomEle(PROVINCES));
        location.setCity(RandomUtil.randomEle(CITIES));
        PhoneData data = new PhoneData();
        data.setLocation(location);
        data.setCarrier(RandomUtil.randomEle(ISPS));
        log.info("DataAPI - phone location result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/bankcard")
    public Result<BankCardData> getBankCardInfo(@RequestParam Map<String, String> params) {
        log.info("DataAPI - bankcard query, params: {}", params);
        BankCardData data = new BankCardData();
        data.setValid(RandomUtil.randomBoolean());
        data.setBank(RandomUtil.randomEle(BANKS));
        data.setCardType(RandomUtil.randomEle(CARD_TYPES));
        log.info("DataAPI - bankcard result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/idcard")
    public Result<IdCardData> getIdCardInfo(@RequestParam Map<String, String> params) {
        log.info("DataAPI - idcard query, params: {}", params);
        IdCardInfo info = new IdCardInfo();
        info.setProvince(RandomUtil.randomEle(PROVINCES));
        info.setBirthday("19" + RandomUtil.randomInt(50, 99) + "-" + String.format("%02d", RandomUtil.randomInt(1, 13)) + "-" + String.format("%02d", RandomUtil.randomInt(1, 29)));
        info.setGender(RandomUtil.randomEle(GENDERS));
        IdCardData data = new IdCardData();
        data.setValid(RandomUtil.randomBoolean());
        data.setInfo(info);
        log.info("DataAPI - idcard result: {}", data);
        return Result.success(data);
    }

    @Data
    public static class CompanyInfo {
        private String name;
        private String creditCode;
        private String legalPerson;
        private String registeredCapital;
        private String status;
    }

    @Data
    public static class CompanyData {
        private CompanyInfo companyInfo;
    }

    @Data
    public static class WeatherData {
        private String city;
        private Integer temperature;
        private String condition;
        private Integer humidity;
        private String windDirection;
        private Integer windSpeed;
    }

    @Data
    public static class LocationInfo {
        private String ip;
        private String country;
        private String province;
        private String city;
        private String isp;
    }

    @Data
    public static class IpData {
        private LocationInfo location;
    }

    @Data
    public static class PhoneLocationInfo {
        private String province;
        private String city;
    }

    @Data
    public static class PhoneData {
        private PhoneLocationInfo location;
        private String carrier;
    }

    @Data
    public static class BankCardData {
        private Boolean valid;
        private String bank;
        private String cardType;
    }

    @Data
    public static class IdCardInfo {
        private String province;
        private String birthday;
        private String gender;
    }

    @Data
    public static class IdCardData {
        private Boolean valid;
        private IdCardInfo info;
    }
}
