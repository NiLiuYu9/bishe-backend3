# Tasks

- [x] Task 1: 重写 mock-api (8081) Controller，实现图像识别(6) + 文本处理(8) + 语音处理(5) 共 19 个接口
  - [x] SubTask 1.1: 实现 ImageApiController - 图像识别/分类/质量检测/风格转换/增强/水印 6 个 POST 接口
  - [x] SubTask 1.2: 实现 TextApiController - 情感分析/关键词提取/摘要生成/分类/NER/纠错/相似度/过滤 8 个 POST 接口
  - [x] SubTask 1.3: 实现 VoiceApiController - 语音转文字/语音合成/声纹识别/情感识别/降噪 5 个 POST 接口
  - [x] SubTask 1.4: 删除原有 MockApiController 中的 /weather 端点

- [x] Task 2: 重写 mock-api-2 (8082) Controller，实现数据服务(6) + 地图服务(5) + 支付服务(4) + 短信服务(5) 共 20 个接口
  - [x] SubTask 2.1: 实现 DataApiController - 企业查询/天气查询/IP查询/手机号归属/银行卡验证/身份证验证 6 个 GET 接口
  - [x] SubTask 2.2: 实现 MapApiController - 地理编码/逆地理编码/路径规划/周边搜索/行政区划 5 个 GET 接口
  - [x] SubTask 2.3: 实现 PayApiController - 支付宝/微信/银联支付 3 个 POST + 订单查询 1 个 GET 接口
  - [x] SubTask 2.4: 实现 SmsApiController - 验证码/通知/营销/语音/国际短信 5 个 POST 接口
  - [x] SubTask 2.5: 删除原有 MockApiController 中的 /weather 端点

- [x] Task 3: 重写 mock-api-3 (8083) Controller，实现人脸识别(6) + OCR识别(8) + 翻译服务(7) 共 21 个接口
  - [x] SubTask 3.1: 实现 FaceApiController - 人脸检测/比对/搜索/活体检测/属性分析/注册 6 个 POST 接口
  - [x] SubTask 3.2: 实现 OcrApiController - 通用/身份证/银行卡/驾驶证/行驶证/营业执照/车牌/发票 OCR 8 个 POST 接口
  - [x] SubTask 3.3: 实现 TranslateApiController - 通用/文档/图片/语音/专业领域/批量/实时翻译 7 个 POST 接口
  - [x] SubTask 3.4: 删除原有 MockApiController 中的 /weather 端点

- [x] Task 4: 生成 SQL 更新脚本，修正 api_info 表的 target_url
  - [x] SubTask 4.1: 生成 UPDATE 语句将 id 1-19 的 target_url 改为 http://localhost:8081
  - [x] SubTask 4.2: 生成 UPDATE 语句将 id 20-39 的 target_url 改为 http://localhost:8082
  - [x] SubTask 4.3: 生成 UPDATE 语句将 id 40-60 的 target_url 改为 http://localhost:8083

- [x] Task 5: 更新项目记忆文件，补充 api_platform.sql 表结构信息

- [x] Task 6: 编译验证 - 对 3 个 mock-api 项目执行 mvn clean compile 确保编译通过

# Task Dependencies

- Task 1, Task 2, Task 3 可并行执行
- Task 4 依赖 Task 1, 2, 3 的端口分配确认
- Task 5 独立执行
- Task 6 依赖 Task 1, 2, 3 完成
