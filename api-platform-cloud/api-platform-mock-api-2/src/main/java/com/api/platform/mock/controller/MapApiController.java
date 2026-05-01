package com.api.platform.mock.controller;

import cn.hutool.core.util.RandomUtil;
import com.api.platform.common.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
/**
 * 地图服务模拟API —— 模拟地理编码、逆地理编码等接口
 *
 * 路由前缀：/api/v1/map
 * 网关通过 targetUrl 将 /invoke/map 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/map")
public class MapApiController {

    private static final String[] DISTRICT_NAMES = {"海淀区", "朝阳区", "浦东新区", "天河区", "南山区", "西湖区", "玄武区", "武侯区", "洪山区", "历下区"};
    private static final String[] POI_NAMES = {"星巴克", "肯德基", "麦当劳", "沃尔玛", "家乐福", "万达广场", "银泰百货", "永辉超市", "便利蜂", "全家便利店"};
    private static final String[] STREETS = {"中山路", "解放路", "人民路", "建设路", "和平路", "长安路", "文化路", "科技路", "创新路", "发展路"};
    private static final String[] LEVELS = {"country", "province", "city", "district", "street"};
    private static final String[] INSTRUCTIONS = {"沿当前道路直行", "左转进入主路", "右转进入辅路", "前方路口调头", "进入环岛", "驶出环岛", "靠左行驶", "靠右行驶", "进入高速", "驶出高速"};

    @GetMapping("/geocode")
    public Result<GeocodeData> geocode(@RequestParam Map<String, String> params) {
        log.info("MapAPI - geocode query, params: {}", params);
        LocationInfo location = new LocationInfo();
        location.setLng(Math.round(RandomUtil.randomDouble(73.0, 135.0) * 1000000.0) / 1000000.0);
        location.setLat(Math.round(RandomUtil.randomDouble(18.0, 53.0) * 1000000.0) / 1000000.0);
        GeocodeData data = new GeocodeData();
        data.setLocation(location);
        log.info("MapAPI - geocode result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/reverse")
    public Result<ReverseData> reverseGeocode(@RequestParam Map<String, String> params) {
        log.info("MapAPI - reverse geocode query, params: {}", params);
        ReverseData data = new ReverseData();
        data.setAddress(RandomUtil.randomEle(PROVINCES) + "市" + RandomUtil.randomEle(DISTRICT_NAMES) + RandomUtil.randomEle(STREETS) + RandomUtil.randomInt(1, 200) + "号");
        data.setProvince(RandomUtil.randomEle(PROVINCES) + "市");
        data.setCity(RandomUtil.randomEle(PROVINCES) + "市");
        data.setDistrict(RandomUtil.randomEle(DISTRICT_NAMES));
        log.info("MapAPI - reverse geocode result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/route")
    public Result<RouteData> route(@RequestParam Map<String, String> params) {
        log.info("MapAPI - route query, params: {}", params);
        int stepCount = RandomUtil.randomInt(3, 8);
        List<RouteStep> steps = new ArrayList<>();
        for (int i = 0; i < stepCount; i++) {
            RouteStep step = new RouteStep();
            step.setInstruction(RandomUtil.randomEle(INSTRUCTIONS));
            step.setDistance(RandomUtil.randomInt(100, 5000));
            steps.add(step);
        }
        RouteInfo route = new RouteInfo();
        route.setDistance(RandomUtil.randomInt(1000, 50000));
        route.setDuration(RandomUtil.randomInt(300, 7200));
        route.setSteps(steps);
        RouteData data = new RouteData();
        data.setRoute(route);
        log.info("MapAPI - route result: {}", data);
        return Result.success(data);
    }

    @GetMapping("/search")
    public Result<SearchData> search(@RequestParam Map<String, String> params) {
        log.info("MapAPI - search query, params: {}", params);
        int poiCount = RandomUtil.randomInt(3, 10);
        List<PoiInfo> pois = new ArrayList<>();
        for (int i = 0; i < poiCount; i++) {
            PoiInfo poi = new PoiInfo();
            poi.setName(RandomUtil.randomEle(POI_NAMES) + "(" + RandomUtil.randomEle(DISTRICT_NAMES) + "店)");
            poi.setAddress(RandomUtil.randomEle(DISTRICT_NAMES) + RandomUtil.randomEle(STREETS) + RandomUtil.randomInt(1, 300) + "号");
            poi.setDistance(RandomUtil.randomInt(50, 5000));
            LocationInfo location = new LocationInfo();
            location.setLng(Math.round(RandomUtil.randomDouble(73.0, 135.0) * 1000000.0) / 1000000.0);
            location.setLat(Math.round(RandomUtil.randomDouble(18.0, 53.0) * 1000000.0) / 1000000.0);
            poi.setLocation(location);
            pois.add(poi);
        }
        SearchData data = new SearchData();
        data.setPois(pois);
        log.info("MapAPI - search result, poi count: {}", pois.size());
        return Result.success(data);
    }

    @GetMapping("/district")
    public Result<DistrictData> district(@RequestParam Map<String, String> params) {
        log.info("MapAPI - district query, params: {}", params);
        int districtCount = RandomUtil.randomInt(3, 8);
        List<DistrictInfo> districts = new ArrayList<>();
        for (int i = 0; i < districtCount; i++) {
            DistrictInfo district = new DistrictInfo();
            district.setName(RandomUtil.randomEle(DISTRICT_NAMES));
            district.setCode(String.valueOf(RandomUtil.randomInt(100000, 999999)));
            district.setLevel(RandomUtil.randomEle(LEVELS));
            districts.add(district);
        }
        DistrictData data = new DistrictData();
        data.setDistricts(districts);
        log.info("MapAPI - district result, count: {}", districts.size());
        return Result.success(data);
    }

    private static final String[] PROVINCES = {"北京", "上海", "广东", "浙江", "江苏", "四川", "湖北", "山东", "河南", "福建"};

    @Data
    public static class LocationInfo {
        private Double lng;
        private Double lat;
    }

    @Data
    public static class GeocodeData {
        private LocationInfo location;
    }

    @Data
    public static class ReverseData {
        private String address;
        private String province;
        private String city;
        private String district;
    }

    @Data
    public static class RouteStep {
        private String instruction;
        private Integer distance;
    }

    @Data
    public static class RouteInfo {
        private Integer distance;
        private Integer duration;
        private List<RouteStep> steps;
    }

    @Data
    public static class RouteData {
        private RouteInfo route;
    }

    @Data
    public static class PoiInfo {
        private String name;
        private String address;
        private Integer distance;
        private LocationInfo location;
    }

    @Data
    public static class SearchData {
        private List<PoiInfo> pois;
    }

    @Data
    public static class DistrictInfo {
        private String name;
        private String code;
        private String level;
    }

    @Data
    public static class DistrictData {
        private List<DistrictInfo> districts;
    }
}
