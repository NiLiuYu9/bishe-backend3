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
 * OCR识别模拟API —— 模拟文字识别、证件识别等接口
 *
 * 路由前缀：/api/v1/ocr
 * 网关通过 targetUrl 将 /invoke/ocr 相关请求路由到此控制器
 */
@RequestMapping("/api/v1/ocr")
public class OcrApiController {

    @PostMapping("/general")
    public Result<OcrGeneralResult> general(@RequestBody Map<String, Object> params) {
        log.info("OCR general request: {}", params);
        OcrGeneralResult result = new OcrGeneralResult();
        int wordCount = RandomUtil.randomInt(3, 10);
        StringBuilder sb = new StringBuilder();
        List<OcrWord> words = new ArrayList<>();
        String[] sampleWords = {"hello", "world", "text", "recognition", "document", "scan", "image", "processing", "data", "extract"};
        for (int i = 0; i < wordCount; i++) {
            OcrWord word = new OcrWord();
            String w = sampleWords[RandomUtil.randomInt(sampleWords.length)];
            word.setWord(w);
            word.setConfidence((float) RandomUtil.randomDouble(0.9, 0.99));
            OcrPosition pos = new OcrPosition();
            pos.setX(RandomUtil.randomInt(0, 1000));
            pos.setY(RandomUtil.randomInt(0, 800));
            word.setPosition(pos);
            words.add(word);
            if (i > 0) sb.append(" ");
            sb.append(w);
        }
        result.setText(sb.toString());
        result.setWords(words);
        log.info("OCR general result: {} words recognized", wordCount);
        return Result.success(result);
    }

    @PostMapping("/idcard")
    public Result<OcrIdcardResult> idcard(@RequestBody Map<String, Object> params) {
        log.info("OCR idcard request: {}", params);
        OcrIdcardResult result = new OcrIdcardResult();
        result.setName(RandomUtil.randomEle(new String[]{"张三", "李四", "王五", "赵六", "陈七"}));
        result.setId_number(String.valueOf(RandomUtil.randomLong(110000000000000000L, 460000000000000000L)));
        result.setGender(RandomUtil.randomEle(new String[]{"男", "女"}));
        result.setAddress(RandomUtil.randomEle(new String[]{"北京市朝阳区建国路100号", "上海市浦东新区陆家嘴路50号", "广州市天河区天河路200号", "深圳市南山区科技路88号"}));
        log.info("OCR idcard result: name={}", result.getName());
        return Result.success(result);
    }

    @PostMapping("/bankcard")
    public Result<OcrBankcardResult> bankcard(@RequestBody Map<String, Object> params) {
        log.info("OCR bankcard request: {}", params);
        OcrBankcardResult result = new OcrBankcardResult();
        result.setCard_number(String.valueOf(RandomUtil.randomLong(6222000000000000L, 6222999999999999L)));
        result.setBank(RandomUtil.randomEle(new String[]{"中国工商银行", "中国建设银行", "中国农业银行", "中国银行", "招商银行", "交通银行"}));
        result.setCard_type(RandomUtil.randomEle(new String[]{"debit", "credit"}));
        log.info("OCR bankcard result: bank={}", result.getBank());
        return Result.success(result);
    }

    @PostMapping("/driver_license")
    public Result<OcrDriverLicenseResult> driverLicense(@RequestBody Map<String, Object> params) {
        log.info("OCR driver_license request: {}", params);
        OcrDriverLicenseResult result = new OcrDriverLicenseResult();
        result.setName(RandomUtil.randomEle(new String[]{"张三", "李四", "王五", "赵六"}));
        result.setLicense_no(String.valueOf(RandomUtil.randomLong(100000000000L, 999999999999L)));
        result.setValid_date("202" + RandomUtil.randomInt(0, 9) + "-01-01至203" + RandomUtil.randomInt(0, 9) + "-12-31");
        log.info("OCR driver_license result: name={}", result.getName());
        return Result.success(result);
    }

    @PostMapping("/vehicle_license")
    public Result<OcrVehicleLicenseResult> vehicleLicense(@RequestBody Map<String, Object> params) {
        log.info("OCR vehicle_license request: {}", params);
        OcrVehicleLicenseResult result = new OcrVehicleLicenseResult();
        String province = RandomUtil.randomEle(new String[]{"京", "沪", "粤", "浙", "苏", "鲁", "川"});
        String letter = RandomUtil.randomString(1).toUpperCase();
        String plateSuffix = RandomUtil.randomString(5).toUpperCase();
        result.setPlate_no(province + letter + "·" + plateSuffix);
        result.setOwner(RandomUtil.randomEle(new String[]{"张三", "李四", "王五", "赵六"}));
        result.setVehicle_type(RandomUtil.randomEle(new String[]{"小型轿车", "中型客车", "大型货车", "轻型厢式货车"}));
        log.info("OCR vehicle_license result: plate_no={}", result.getPlate_no());
        return Result.success(result);
    }

    @PostMapping("/business_license")
    public Result<OcrBusinessLicenseResult> businessLicense(@RequestBody Map<String, Object> params) {
        log.info("OCR business_license request: {}", params);
        OcrBusinessLicenseResult result = new OcrBusinessLicenseResult();
        result.setCompany_name(RandomUtil.randomEle(new String[]{"北京科技有限公司", "上海网络科技有限公司", "广州数据科技有限公司", "深圳智能科技有限公司"}));
        result.setCredit_code(String.valueOf(RandomUtil.randomLong(91110000000000000L, 91999999999999999L)));
        result.setLegal_person(RandomUtil.randomEle(new String[]{"张三", "李四", "王五", "赵六"}));
        log.info("OCR business_license result: company_name={}", result.getCompany_name());
        return Result.success(result);
    }

    @PostMapping("/plate")
    public Result<OcrPlateResult> plate(@RequestBody Map<String, Object> params) {
        log.info("OCR plate request: {}", params);
        OcrPlateResult result = new OcrPlateResult();
        String province = RandomUtil.randomEle(new String[]{"京", "沪", "粤", "浙", "苏", "鲁", "川"});
        String letter = RandomUtil.randomString(1).toUpperCase();
        String plateSuffix = RandomUtil.randomString(5).toUpperCase();
        result.setPlate_number(province + letter + "·" + plateSuffix);
        result.setColor(RandomUtil.randomEle(new String[]{"blue", "yellow", "green", "white", "black"}));
        result.setConfidence((float) RandomUtil.randomDouble(0.9, 0.99));
        log.info("OCR plate result: plate_number={}", result.getPlate_number());
        return Result.success(result);
    }

    @PostMapping("/invoice")
    public Result<OcrInvoiceResult> invoice(@RequestBody Map<String, Object> params) {
        log.info("OCR invoice request: {}", params);
        OcrInvoiceResult result = new OcrInvoiceResult();
        result.setInvoice_code(String.valueOf(RandomUtil.randomLong(100000000000L, 999999999999L)));
        result.setInvoice_number(String.valueOf(RandomUtil.randomLong(10000000L, 99999999L)));
        result.setAmount((float) RandomUtil.randomDouble(10.0, 99999.99));
        result.setDate("202" + RandomUtil.randomInt(3, 6) + "-" + String.format("%02d", RandomUtil.randomInt(1, 13)) + "-" + String.format("%02d", RandomUtil.randomInt(1, 29)));
        log.info("OCR invoice result: invoice_number={}", result.getInvoice_number());
        return Result.success(result);
    }

    @Data
    public static class OcrPosition {
        private Integer x;
        private Integer y;
    }

    @Data
    public static class OcrWord {
        private String word;
        private Float confidence;
        private OcrPosition position;
    }

    @Data
    public static class OcrGeneralResult {
        private String text;
        private List<OcrWord> words;
    }

    @Data
    public static class OcrIdcardResult {
        private String name;
        private String id_number;
        private String gender;
        private String address;
    }

    @Data
    public static class OcrBankcardResult {
        private String card_number;
        private String bank;
        private String card_type;
    }

    @Data
    public static class OcrDriverLicenseResult {
        private String name;
        private String license_no;
        private String valid_date;
    }

    @Data
    public static class OcrVehicleLicenseResult {
        private String plate_no;
        private String owner;
        private String vehicle_type;
    }

    @Data
    public static class OcrBusinessLicenseResult {
        private String company_name;
        private String credit_code;
        private String legal_person;
    }

    @Data
    public static class OcrPlateResult {
        private String plate_number;
        private String color;
        private Float confidence;
    }

    @Data
    public static class OcrInvoiceResult {
        private String invoice_code;
        private String invoice_number;
        private Float amount;
        private String date;
    }
}
