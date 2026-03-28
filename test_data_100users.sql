-- ================================================================================
--                    API平台测试数据 - 100用户规模
-- ================================================================================
-- 生成日期: 2026-03-27
-- 数据规模: 100用户 (2管理员 + 30开发者 + 68普通用户)
-- ================================================================================

-- 清空所有表数据（按外键依赖逆序删除）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE after_sale_message;
TRUNCATE TABLE requirement_after_sale;
TRUNCATE TABLE requirement_applicant;
TRUNCATE TABLE requirement_tag;
TRUNCATE TABLE requirement;
TRUNCATE TABLE notification_message;
TRUNCATE TABLE api_invoke_daily;
TRUNCATE TABLE api_review;
TRUNCATE TABLE user_api_quota;
TRUNCATE TABLE order_info;
TRUNCATE TABLE api_test_record;
TRUNCATE TABLE api_whitelist;
TRUNCATE TABLE api_favorite;
TRUNCATE TABLE api_info;
TRUNCATE TABLE user_tag;
TRUNCATE TABLE api_type;
TRUNCATE TABLE sys_user;
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================================================
-- 第1层：无依赖表
-- ================================================================================

-- -------------------------------------------------------------------------------
-- 1. sys_user 用户表 (100条)
-- -------------------------------------------------------------------------------
-- ID 1-2: 管理员
-- ID 3-32: 开发者 (30人)
-- ID 33-100: 普通用户 (68人)
INSERT INTO sys_user (id, username, password, email, phone, is_admin, access_key, secret_key, status, freeze_reason, create_time, update_time, deleted) VALUES
-- 管理员
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@api-platform.com', '13800000001', 1, 'AK_ADMIN001', 'SK_ADMIN001', 1, NULL, '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(2, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'superadmin@api-platform.com', '13800000002', 1, 'AK_ADMIN002', 'SK_ADMIN002', 1, NULL, '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
-- 开发者 (ID 3-32)
(3, 'developer_zhang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'zhangsan@api-platform.com', '13800000003', 0, 'AK_DEV00003', 'SK_DEV00003', 1, NULL, '2025-12-02 09:00:00', '2025-12-02 09:00:00', 0),
(4, 'developer_li', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'lisi@api-platform.com', '13800000004', 0, 'AK_DEV00004', 'SK_DEV00004', 1, NULL, '2025-12-02 09:30:00', '2025-12-02 09:30:00', 0),
(5, 'developer_wang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'wangwu@api-platform.com', '13800000005', 0, 'AK_DEV00005', 'SK_DEV00005', 1, NULL, '2025-12-02 10:00:00', '2025-12-02 10:00:00', 0),
(6, 'developer_zhao', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'zhaoliu@api-platform.com', '13800000006', 0, 'AK_DEV00006', 'SK_DEV00006', 1, NULL, '2025-12-02 10:30:00', '2025-12-02 10:30:00', 0),
(7, 'developer_chen', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'chenqi@api-platform.com', '13800000007', 0, 'AK_DEV00007', 'SK_DEV00007', 1, NULL, '2025-12-03 08:00:00', '2025-12-03 08:00:00', 0),
(8, 'developer_liu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'liuba@api-platform.com', '13800000008', 0, 'AK_DEV00008', 'SK_DEV00008', 1, NULL, '2025-12-03 08:30:00', '2025-12-03 08:30:00', 0),
(9, 'developer_sun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'sunjiu@api-platform.com', '13800000009', 0, 'AK_DEV00009', 'SK_DEV00009', 1, NULL, '2025-12-03 09:00:00', '2025-12-03 09:00:00', 0),
(10, 'developer_zhou', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'zhoushi@api-platform.com', '13800000010', 0, 'AK_DEV00010', 'SK_DEV00010', 1, NULL, '2025-12-03 09:30:00', '2025-12-03 09:30:00', 0),
(11, 'developer_wu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'wu11@api-platform.com', '13800000011', 0, 'AK_DEV00011', 'SK_DEV00011', 1, NULL, '2025-12-04 08:00:00', '2025-12-04 08:00:00', 0),
(12, 'developer_zheng', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'zheng12@api-platform.com', '13800000012', 0, 'AK_DEV00012', 'SK_DEV00012', 1, NULL, '2025-12-04 08:30:00', '2025-12-04 08:30:00', 0),
(13, 'developer_feng', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'feng13@api-platform.com', '13800000013', 0, 'AK_DEV00013', 'SK_DEV00013', 1, NULL, '2025-12-04 09:00:00', '2025-12-04 09:00:00', 0),
(14, 'developer_he', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'he14@api-platform.com', '13800000014', 0, 'AK_DEV00014', 'SK_DEV00014', 1, NULL, '2025-12-04 09:30:00', '2025-12-04 09:30:00', 0),
(15, 'developer_ma', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'ma15@api-platform.com', '13800000015', 0, 'AK_DEV00015', 'SK_DEV00015', 1, NULL, '2025-12-05 08:00:00', '2025-12-05 08:00:00', 0),
(16, 'developer_han', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'han16@api-platform.com', '13800000016', 0, 'AK_DEV00016', 'SK_DEV00016', 1, NULL, '2025-12-05 08:30:00', '2025-12-05 08:30:00', 0),
(17, 'developer_tang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'tang17@api-platform.com', '13800000017', 0, 'AK_DEV00017', 'SK_DEV00017', 1, NULL, '2025-12-05 09:00:00', '2025-12-05 09:00:00', 0),
(18, 'developer_qin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'qin18@api-platform.com', '13800000018', 0, 'AK_DEV00018', 'SK_DEV00018', 1, NULL, '2025-12-05 09:30:00', '2025-12-05 09:30:00', 0),
(19, 'developer_xu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xu19@api-platform.com', '13800000019', 0, 'AK_DEV00019', 'SK_DEV00019', 1, NULL, '2025-12-06 08:00:00', '2025-12-06 08:00:00', 0),
(20, 'developer_yan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'yan20@api-platform.com', '13800000020', 0, 'AK_DEV00020', 'SK_DEV00020', 1, NULL, '2025-12-06 08:30:00', '2025-12-06 08:30:00', 0),
(21, 'developer_huang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'huang21@api-platform.com', '13800000021', 0, 'AK_DEV00021', 'SK_DEV00021', 1, NULL, '2025-12-06 09:00:00', '2025-12-06 09:00:00', 0),
(22, 'developer_lin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'lin22@api-platform.com', '13800000022', 0, 'AK_DEV00022', 'SK_DEV00022', 1, NULL, '2025-12-06 09:30:00', '2025-12-06 09:30:00', 0),
(23, 'developer_guo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'guo23@api-platform.com', '13800000023', 0, 'AK_DEV00023', 'SK_DEV00023', 1, NULL, '2025-12-07 08:00:00', '2025-12-07 08:00:00', 0),
(24, 'developer_luo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'luo24@api-platform.com', '13800000024', 0, 'AK_DEV00024', 'SK_DEV00024', 1, NULL, '2025-12-07 08:30:00', '2025-12-07 08:30:00', 0),
(25, 'developer_cao', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'cao25@api-platform.com', '13800000025', 0, 'AK_DEV00025', 'SK_DEV00025', 1, NULL, '2025-12-07 09:00:00', '2025-12-07 09:00:00', 0),
(26, 'developer_deng', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'deng26@api-platform.com', '13800000026', 0, 'AK_DEV00026', 'SK_DEV00026', 1, NULL, '2025-12-07 09:30:00', '2025-12-07 09:30:00', 0),
(27, 'developer_xie', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xie27@api-platform.com', '13800000027', 0, 'AK_DEV00027', 'SK_DEV00027', 1, NULL, '2025-12-08 08:00:00', '2025-12-08 08:00:00', 0),
(28, 'developer_lu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'lu28@api-platform.com', '13800000028', 0, 'AK_DEV00028', 'SK_DEV00028', 1, NULL, '2025-12-08 08:30:00', '2025-12-08 08:30:00', 0),
(29, 'developer_jiang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'jiang29@api-platform.com', '13800000029', 0, 'AK_DEV00029', 'SK_DEV00029', 1, NULL, '2025-12-08 09:00:00', '2025-12-08 09:00:00', 0),
(30, 'developer_shen', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'shen30@api-platform.com', '13800000030', 0, 'AK_DEV00030', 'SK_DEV00030', 1, NULL, '2025-12-08 09:30:00', '2025-12-08 09:30:00', 0),
(31, 'developer_yang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'yang31@api-platform.com', '13800000031', 0, 'AK_DEV00031', 'SK_DEV00031', 1, NULL, '2025-12-09 08:00:00', '2025-12-09 08:00:00', 0),
(32, 'developer_zhu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'zhu32@api-platform.com', '13800000032', 0, 'AK_DEV00032', 'SK_DEV00032', 1, NULL, '2025-12-09 08:30:00', '2025-12-09 08:30:00', 0),
-- 普通用户 (ID 33-100)
(33, 'user_xiaoming', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaoming@api-platform.com', '13800000033', 0, 'AK_USER00033', 'SK_USER00033', 1, NULL, '2025-12-10 08:00:00', '2025-12-10 08:00:00', 0),
(34, 'user_xiaohong', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaohong@api-platform.com', '13800000034', 0, 'AK_USER00034', 'SK_USER00034', 1, NULL, '2025-12-10 08:30:00', '2025-12-10 08:30:00', 0),
(35, 'user_xiaogang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaogang@api-platform.com', '13800000035', 0, 'AK_USER00035', 'SK_USER00035', 1, NULL, '2025-12-10 09:00:00', '2025-12-10 09:00:00', 0),
(36, 'user_xiaoli', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaoli@api-platform.com', '13800000036', 0, 'AK_USER00036', 'SK_USER00036', 1, NULL, '2025-12-10 09:30:00', '2025-12-10 09:30:00', 0),
(37, 'user_xiaowei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaowei@api-platform.com', '13800000037', 0, 'AK_USER00037', 'SK_USER00037', 1, NULL, '2025-12-11 08:00:00', '2025-12-11 08:00:00', 0),
(38, 'user_xiaofang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaofang@api-platform.com', '13800000038', 0, 'AK_USER00038', 'SK_USER00038', 1, NULL, '2025-12-11 08:30:00', '2025-12-11 08:30:00', 0),
(39, 'user_xiaojun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaojun@api-platform.com', '13800000039', 0, 'AK_USER00039', 'SK_USER00039', 1, NULL, '2025-12-11 09:00:00', '2025-12-11 09:00:00', 0),
(40, 'user_xiaoyun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaoyun@api-platform.com', '13800000040', 0, 'AK_USER00040', 'SK_USER00040', 1, NULL, '2025-12-11 09:30:00', '2025-12-11 09:30:00', 0),
(41, 'user_test01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test01@api-platform.com', '13800000041', 0, 'AK_USER00041', 'SK_USER00041', 1, NULL, '2025-12-12 08:00:00', '2025-12-12 08:00:00', 0),
(42, 'user_test02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test02@api-platform.com', '13800000042', 0, 'AK_USER00042', 'SK_USER00042', 1, NULL, '2025-12-12 08:30:00', '2025-12-12 08:30:00', 0),
(43, 'user_test03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test03@api-platform.com', '13800000043', 0, 'AK_USER00043', 'SK_USER00043', 1, NULL, '2025-12-12 09:00:00', '2025-12-12 09:00:00', 0),
(44, 'user_test04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test04@api-platform.com', '13800000044', 0, 'AK_USER00044', 'SK_USER00044', 1, NULL, '2025-12-12 09:30:00', '2025-12-12 09:30:00', 0),
(45, 'user_test05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test05@api-platform.com', '13800000045', 0, 'AK_USER00045', 'SK_USER00045', 1, NULL, '2025-12-13 08:00:00', '2025-12-13 08:00:00', 0),
(46, 'user_test06', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test06@api-platform.com', '13800000046', 0, 'AK_USER00046', 'SK_USER00046', 1, NULL, '2025-12-13 08:30:00', '2025-12-13 08:30:00', 0),
(47, 'user_test07', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test07@api-platform.com', '13800000047', 0, 'AK_USER00047', 'SK_USER00047', 1, NULL, '2025-12-13 09:00:00', '2025-12-13 09:00:00', 0),
(48, 'user_test08', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test08@api-platform.com', '13800000048', 0, 'AK_USER00048', 'SK_USER00048', 1, NULL, '2025-12-13 09:30:00', '2025-12-13 09:30:00', 0),
(49, 'user_test09', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test09@api-platform.com', '13800000049', 0, 'AK_USER00049', 'SK_USER00049', 1, NULL, '2025-12-14 08:00:00', '2025-12-14 08:00:00', 0),
(50, 'user_test10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test10@api-platform.com', '13800000050', 0, 'AK_USER00050', 'SK_USER00050', 1, NULL, '2025-12-14 08:30:00', '2025-12-14 08:30:00', 0),
(51, 'user_test11', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test11@api-platform.com', '13800000051', 0, 'AK_USER00051', 'SK_USER00051', 1, NULL, '2025-12-14 09:00:00', '2025-12-14 09:00:00', 0),
(52, 'user_test12', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test12@api-platform.com', '13800000052', 0, 'AK_USER00052', 'SK_USER00052', 1, NULL, '2025-12-14 09:30:00', '2025-12-14 09:30:00', 0),
(53, 'user_test13', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test13@api-platform.com', '13800000053', 0, 'AK_USER00053', 'SK_USER00053', 1, NULL, '2025-12-15 08:00:00', '2025-12-15 08:00:00', 0),
(54, 'user_test14', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test14@api-platform.com', '13800000054', 0, 'AK_USER00054', 'SK_USER00054', 1, NULL, '2025-12-15 08:30:00', '2025-12-15 08:30:00', 0),
(55, 'user_test15', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test15@api-platform.com', '13800000055', 0, 'AK_USER00055', 'SK_USER00055', 1, NULL, '2025-12-15 09:00:00', '2025-12-15 09:00:00', 0),
(56, 'user_test16', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test16@api-platform.com', '13800000056', 0, 'AK_USER00056', 'SK_USER00056', 1, NULL, '2025-12-15 09:30:00', '2025-12-15 09:30:00', 0),
(57, 'user_test17', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test17@api-platform.com', '13800000057', 0, 'AK_USER00057', 'SK_USER00057', 1, NULL, '2025-12-16 08:00:00', '2025-12-16 08:00:00', 0),
(58, 'user_test18', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test18@api-platform.com', '13800000058', 0, 'AK_USER00058', 'SK_USER00058', 1, NULL, '2025-12-16 08:30:00', '2025-12-16 08:30:00', 0),
(59, 'user_test19', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test19@api-platform.com', '13800000059', 0, 'AK_USER00059', 'SK_USER00059', 1, NULL, '2025-12-16 09:00:00', '2025-12-16 09:00:00', 0),
(60, 'user_test20', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test20@api-platform.com', '13800000060', 0, 'AK_USER00060', 'SK_USER00060', 1, NULL, '2025-12-16 09:30:00', '2025-12-16 09:30:00', 0),
(61, 'user_test21', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test21@api-platform.com', '13800000061', 0, 'AK_USER00061', 'SK_USER00061', 1, NULL, '2025-12-17 08:00:00', '2025-12-17 08:00:00', 0),
(62, 'user_test22', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test22@api-platform.com', '13800000062', 0, 'AK_USER00062', 'SK_USER00062', 1, NULL, '2025-12-17 08:30:00', '2025-12-17 08:30:00', 0),
(63, 'user_test23', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test23@api-platform.com', '13800000063', 0, 'AK_USER00063', 'SK_USER00063', 1, NULL, '2025-12-17 09:00:00', '2025-12-17 09:00:00', 0),
(64, 'user_test24', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test24@api-platform.com', '13800000064', 0, 'AK_USER00064', 'SK_USER00064', 1, NULL, '2025-12-17 09:30:00', '2025-12-17 09:30:00', 0),
(65, 'user_test25', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test25@api-platform.com', '13800000065', 0, 'AK_USER00065', 'SK_USER00065', 1, NULL, '2025-12-18 08:00:00', '2025-12-18 08:00:00', 0),
(66, 'user_test26', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test26@api-platform.com', '13800000066', 0, 'AK_USER00066', 'SK_USER00066', 1, NULL, '2025-12-18 08:30:00', '2025-12-18 08:30:00', 0),
(67, 'user_test27', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test27@api-platform.com', '13800000067', 0, 'AK_USER00067', 'SK_USER00067', 1, NULL, '2025-12-18 09:00:00', '2025-12-18 09:00:00', 0),
(68, 'user_test28', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test28@api-platform.com', '13800000068', 0, 'AK_USER00068', 'SK_USER00068', 1, NULL, '2025-12-18 09:30:00', '2025-12-18 09:30:00', 0),
(69, 'user_test29', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test29@api-platform.com', '13800000069', 0, 'AK_USER00069', 'SK_USER00069', 1, NULL, '2025-12-19 08:00:00', '2025-12-19 08:00:00', 0),
(70, 'user_test30', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test30@api-platform.com', '13800000070', 0, 'AK_USER00070', 'SK_USER00070', 1, NULL, '2025-12-19 08:30:00', '2025-12-19 08:30:00', 0),
(71, 'user_test31', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test31@api-platform.com', '13800000071', 0, 'AK_USER00071', 'SK_USER00071', 1, NULL, '2025-12-19 09:00:00', '2025-12-19 09:00:00', 0),
(72, 'user_test32', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test32@api-platform.com', '13800000072', 0, 'AK_USER00072', 'SK_USER00072', 1, NULL, '2025-12-19 09:30:00', '2025-12-19 09:30:00', 0),
(73, 'user_test33', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test33@api-platform.com', '13800000073', 0, 'AK_USER00073', 'SK_USER00073', 1, NULL, '2025-12-20 08:00:00', '2025-12-20 08:00:00', 0),
(74, 'user_test34', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test34@api-platform.com', '13800000074', 0, 'AK_USER00074', 'SK_USER00074', 1, NULL, '2025-12-20 08:30:00', '2025-12-20 08:30:00', 0),
(75, 'user_test35', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test35@api-platform.com', '13800000075', 0, 'AK_USER00075', 'SK_USER00075', 1, NULL, '2025-12-20 09:00:00', '2025-12-20 09:00:00', 0),
(76, 'user_test36', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test36@api-platform.com', '13800000076', 0, 'AK_USER00076', 'SK_USER00076', 1, NULL, '2025-12-20 09:30:00', '2025-12-20 09:30:00', 0),
(77, 'user_test37', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test37@api-platform.com', '13800000077', 0, 'AK_USER00077', 'SK_USER00077', 1, NULL, '2025-12-21 08:00:00', '2025-12-21 08:00:00', 0),
(78, 'user_test38', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test38@api-platform.com', '13800000078', 0, 'AK_USER00078', 'SK_USER00078', 1, NULL, '2025-12-21 08:30:00', '2025-12-21 08:30:00', 0),
(79, 'user_test39', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test39@api-platform.com', '13800000079', 0, 'AK_USER00079', 'SK_USER00079', 1, NULL, '2025-12-21 09:00:00', '2025-12-21 09:00:00', 0),
(80, 'user_test40', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test40@api-platform.com', '13800000080', 0, 'AK_USER00080', 'SK_USER00080', 1, NULL, '2025-12-21 09:30:00', '2025-12-21 09:30:00', 0),
(81, 'user_test41', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test41@api-platform.com', '13800000081', 0, 'AK_USER00081', 'SK_USER00081', 1, NULL, '2025-12-22 08:00:00', '2025-12-22 08:00:00', 0),
(82, 'user_test42', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test42@api-platform.com', '13800000082', 0, 'AK_USER00082', 'SK_USER00082', 1, NULL, '2025-12-22 08:30:00', '2025-12-22 08:30:00', 0),
(83, 'user_test43', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test43@api-platform.com', '13800000083', 0, 'AK_USER00083', 'SK_USER00083', 1, NULL, '2025-12-22 09:00:00', '2025-12-22 09:00:00', 0),
(84, 'user_test44', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test44@api-platform.com', '13800000084', 0, 'AK_USER00084', 'SK_USER00084', 1, NULL, '2025-12-22 09:30:00', '2025-12-22 09:30:00', 0),
(85, 'user_test45', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test45@api-platform.com', '13800000085', 0, 'AK_USER00085', 'SK_USER00085', 1, NULL, '2025-12-23 08:00:00', '2025-12-23 08:00:00', 0),
(86, 'user_test46', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test46@api-platform.com', '13800000086', 0, 'AK_USER00086', 'SK_USER00086', 1, NULL, '2025-12-23 08:30:00', '2025-12-23 08:30:00', 0),
(87, 'user_test47', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test47@api-platform.com', '13800000087', 0, 'AK_USER00087', 'SK_USER00087', 1, NULL, '2025-12-23 09:00:00', '2025-12-23 09:00:00', 0),
(88, 'user_test48', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test48@api-platform.com', '13800000088', 0, 'AK_USER00088', 'SK_USER00088', 1, NULL, '2025-12-23 09:30:00', '2025-12-23 09:30:00', 0),
(89, 'user_test49', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test49@api-platform.com', '13800000089', 0, 'AK_USER00089', 'SK_USER00089', 1, NULL, '2025-12-24 08:00:00', '2025-12-24 08:00:00', 0),
(90, 'user_test50', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test50@api-platform.com', '13800000090', 0, 'AK_USER00090', 'SK_USER00090', 1, NULL, '2025-12-24 08:30:00', '2025-12-24 08:30:00', 0),
(91, 'user_test51', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test51@api-platform.com', '13800000091', 0, 'AK_USER00091', 'SK_USER00091', 1, NULL, '2025-12-24 09:00:00', '2025-12-24 09:00:00', 0),
(92, 'user_test52', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test52@api-platform.com', '13800000092', 0, 'AK_USER00092', 'SK_USER00092', 1, NULL, '2025-12-24 09:30:00', '2025-12-24 09:30:00', 0),
(93, 'user_test53', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test53@api-platform.com', '13800000093', 0, 'AK_USER00093', 'SK_USER00093', 1, NULL, '2025-12-25 08:00:00', '2025-12-25 08:00:00', 0),
(94, 'user_test54', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test54@api-platform.com', '13800000094', 0, 'AK_USER00094', 'SK_USER00094', 1, NULL, '2025-12-25 08:30:00', '2025-12-25 08:30:00', 0),
(95, 'user_test55', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test55@api-platform.com', '13800000095', 0, 'AK_USER00095', 'SK_USER00095', 1, NULL, '2025-12-25 09:00:00', '2025-12-25 09:00:00', 0),
(96, 'user_test56', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test56@api-platform.com', '13800000096', 0, 'AK_USER00096', 'SK_USER00096', 1, NULL, '2025-12-25 09:30:00', '2025-12-25 09:30:00', 0),
(97, 'user_test57', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test57@api-platform.com', '13800000097', 0, 'AK_USER00097', 'SK_USER00097', 1, NULL, '2025-12-26 08:00:00', '2025-12-26 08:00:00', 0),
(98, 'user_test58', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test58@api-platform.com', '13800000098', 0, 'AK_USER00098', 'SK_USER00098', 1, NULL, '2025-12-26 08:30:00', '2025-12-26 08:30:00', 0),
(99, 'user_test59', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test59@api-platform.com', '13800000099', 0, 'AK_USER00099', 'SK_USER00099', 1, NULL, '2025-12-26 09:00:00', '2025-12-26 09:00:00', 0),
(100, 'user_test60', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test60@api-platform.com', '13800000100', 0, 'AK_USER00100', 'SK_USER00100', 1, NULL, '2025-12-26 09:30:00', '2025-12-26 09:30:00', 0);

-- -------------------------------------------------------------------------------
-- 2. api_type API类型表 (10条)
-- -------------------------------------------------------------------------------
INSERT INTO api_type (id, name, description, create_time, update_time, deleted) VALUES
(1, '图像识别', '基于深度学习的图像识别与分析服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(2, '文本处理', '自然语言处理与文本分析服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(3, '语音识别', '语音转文字及语音分析服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(4, '数据查询', '各类数据查询与检索服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(5, '地图服务', '地图定位与导航服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(6, '支付接口', '在线支付与交易服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(7, '短信服务', '短信发送与验证服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(8, '人脸识别', '人脸检测与识别服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(9, 'OCR识别', '文字识别与提取服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0),
(10, '翻译服务', '多语言翻译服务', '2025-12-01 08:00:00', '2025-12-01 08:00:00', 0);

-- ================================================================================
-- 第2层：依赖第1层
-- ================================================================================

-- -------------------------------------------------------------------------------
-- 3. user_tag 用户技能标签表 (150条)
-- -------------------------------------------------------------------------------
INSERT INTO user_tag (id, user_id, tag_name, create_time) VALUES
-- 开发者标签 (ID 3-32)
(1, 3, 'Java', '2025-12-02 09:00:00'),
(2, 3, 'Spring Boot', '2025-12-02 09:00:00'),
(3, 4, 'Python', '2025-12-02 09:30:00'),
(4, 4, 'Django', '2025-12-02 09:30:00'),
(5, 5, 'JavaScript', '2025-12-02 10:00:00'),
(6, 5, 'Vue', '2025-12-02 10:00:00'),
(7, 6, 'Java', '2025-12-02 10:30:00'),
(8, 6, 'MySQL', '2025-12-02 10:30:00'),
(9, 7, 'Python', '2025-12-03 08:00:00'),
(10, 7, 'TensorFlow', '2025-12-03 08:00:00'),
(11, 8, 'Java', '2025-12-03 08:30:00'),
(12, 8, 'Redis', '2025-12-03 08:30:00'),
(13, 9, 'JavaScript', '2025-12-03 09:00:00'),
(14, 9, 'React', '2025-12-03 09:00:00'),
(15, 10, 'Python', '2025-12-03 09:30:00'),
(16, 10, 'Flask', '2025-12-03 09:30:00'),
(17, 11, 'Java', '2025-12-04 08:00:00'),
(18, 11, '微服务', '2025-12-04 08:00:00'),
(19, 12, 'Python', '2025-12-04 08:30:00'),
(20, 12, '数据分析', '2025-12-04 08:30:00'),
(21, 13, 'JavaScript', '2025-12-04 09:00:00'),
(22, 13, 'Node.js', '2025-12-04 09:00:00'),
(23, 14, 'Java', '2025-12-04 09:30:00'),
(24, 14, 'Spring Cloud', '2025-12-04 09:30:00'),
(25, 15, 'Python', '2025-12-05 08:00:00'),
(26, 15, '机器学习', '2025-12-05 08:00:00'),
(27, 16, 'Go', '2025-12-05 08:30:00'),
(28, 16, '后端开发', '2025-12-05 08:30:00'),
(29, 17, 'JavaScript', '2025-12-05 09:00:00'),
(30, 17, '前端开发', '2025-12-05 09:00:00'),
(31, 18, 'Java', '2025-12-05 09:30:00'),
(32, 18, 'MyBatis', '2025-12-05 09:30:00'),
(33, 19, 'Python', '2025-12-06 08:00:00'),
(34, 19, '爬虫开发', '2025-12-06 08:00:00'),
(35, 20, 'JavaScript', '2025-12-06 08:30:00'),
(36, 20, 'TypeScript', '2025-12-06 08:30:00'),
(37, 21, 'Java', '2025-12-06 09:00:00'),
(38, 21, '分布式系统', '2025-12-06 09:00:00'),
(39, 22, 'Python', '2025-12-06 09:30:00'),
(40, 22, '深度学习', '2025-12-06 09:30:00'),
(41, 23, 'JavaScript', '2025-12-07 08:00:00'),
(42, 23, 'Angular', '2025-12-07 08:00:00'),
(43, 24, 'Java', '2025-12-07 08:30:00'),
(44, 24, '消息队列', '2025-12-07 08:30:00'),
(45, 25, 'Python', '2025-12-07 09:00:00'),
(46, 25, '自然语言处理', '2025-12-07 09:00:00'),
(47, 26, 'C++', '2025-12-07 09:30:00'),
(48, 26, '高性能计算', '2025-12-07 09:30:00'),
(49, 27, 'Java', '2025-12-08 08:00:00'),
(50, 27, '数据库优化', '2025-12-08 08:00:00'),
(51, 28, 'Python', '2025-12-08 08:30:00'),
(52, 28, '计算机视觉', '2025-12-08 08:30:00'),
(53, 29, 'JavaScript', '2025-12-08 09:00:00'),
(54, 29, '移动开发', '2025-12-08 09:00:00'),
(55, 30, 'Java', '2025-12-08 09:30:00'),
(56, 30, '全栈开发', '2025-12-08 09:30:00'),
(57, 31, 'Python', '2025-12-09 08:00:00'),
(58, 31, '大数据', '2025-12-09 08:00:00'),
(59, 32, 'JavaScript', '2025-12-09 08:30:00'),
(60, 32, '小程序开发', '2025-12-09 08:30:00'),
-- 普通用户标签 (部分用户)
(61, 33, 'Java', '2025-12-10 08:00:00'),
(62, 34, 'Python', '2025-12-10 08:30:00'),
(63, 35, 'JavaScript', '2025-12-10 09:00:00'),
(64, 36, '前端开发', '2025-12-10 09:30:00'),
(65, 37, '后端开发', '2025-12-11 08:00:00'),
(66, 38, '数据库', '2025-12-11 08:30:00'),
(67, 39, '移动开发', '2025-12-11 09:00:00'),
(68, 40, '全栈开发', '2025-12-11 09:30:00'),
(69, 41, 'Java', '2025-12-12 08:00:00'),
(70, 42, 'Python', '2025-12-12 08:30:00'),
(71, 43, 'JavaScript', '2025-12-12 09:00:00'),
(72, 44, 'Vue', '2025-12-12 09:30:00'),
(73, 45, 'React', '2025-12-13 08:00:00'),
(74, 46, 'Spring Boot', '2025-12-13 08:30:00'),
(75, 47, 'MySQL', '2025-12-13 09:00:00'),
(76, 48, 'Redis', '2025-12-13 09:30:00'),
(77, 49, '微服务', '2025-12-14 08:00:00'),
(78, 50, '机器学习', '2025-12-14 08:30:00'),
(79, 51, '深度学习', '2025-12-14 09:00:00'),
(80, 52, '数据分析', '2025-12-14 09:30:00'),
(81, 53, '爬虫开发', '2025-12-15 08:00:00'),
(82, 54, '自然语言处理', '2025-12-15 08:30:00'),
(83, 55, '计算机视觉', '2025-12-15 09:00:00'),
(84, 56, '大数据', '2025-12-15 09:30:00'),
(85, 57, 'Go', '2025-12-16 08:00:00'),
(86, 58, 'Node.js', '2025-12-16 08:30:00'),
(87, 59, 'TypeScript', '2025-12-16 09:00:00'),
(88, 60, 'Angular', '2025-12-16 09:30:00'),
(89, 61, 'Django', '2025-12-17 08:00:00'),
(90, 62, 'Flask', '2025-12-17 08:30:00'),
(91, 63, 'TensorFlow', '2025-12-17 09:00:00'),
(92, 64, '消息队列', '2025-12-17 09:30:00'),
(93, 65, '分布式系统', '2025-12-18 08:00:00'),
(94, 66, '高性能计算', '2025-12-18 08:30:00'),
(95, 67, '数据库优化', '2025-12-18 09:00:00'),
(96, 68, '小程序开发', '2025-12-18 09:30:00'),
(97, 69, 'Java', '2025-12-19 08:00:00'),
(98, 70, 'Python', '2025-12-19 08:30:00'),
(99, 71, 'JavaScript', '2025-12-19 09:00:00'),
(100, 72, '前端开发', '2025-12-19 09:30:00'),
(101, 73, '后端开发', '2025-12-20 08:00:00'),
(102, 74, '数据库', '2025-12-20 08:30:00'),
(103, 75, '移动开发', '2025-12-20 09:00:00'),
(104, 76, '全栈开发', '2025-12-20 09:30:00'),
(105, 77, 'Java', '2025-12-21 08:00:00'),
(106, 78, 'Python', '2025-12-21 08:30:00'),
(107, 79, 'JavaScript', '2025-12-21 09:00:00'),
(108, 80, 'Vue', '2025-12-21 09:30:00'),
(109, 81, 'React', '2025-12-22 08:00:00'),
(110, 82, 'Spring Boot', '2025-12-22 08:30:00'),
(111, 83, 'MySQL', '2025-12-22 09:00:00'),
(112, 84, 'Redis', '2025-12-22 09:30:00'),
(113, 85, '微服务', '2025-12-23 08:00:00'),
(114, 86, '机器学习', '2025-12-23 08:30:00'),
(115, 87, '深度学习', '2025-12-23 09:00:00'),
(116, 88, '数据分析', '2025-12-23 09:30:00'),
(117, 89, '爬虫开发', '2025-12-24 08:00:00'),
(118, 90, '自然语言处理', '2025-12-24 08:30:00'),
(119, 91, '计算机视觉', '2025-12-24 09:00:00'),
(120, 92, '大数据', '2025-12-24 09:30:00'),
(121, 93, 'Go', '2025-12-25 08:00:00'),
(122, 94, 'Node.js', '2025-12-25 08:30:00'),
(123, 95, 'TypeScript', '2025-12-25 09:00:00'),
(124, 96, 'Angular', '2025-12-25 09:30:00'),
(125, 97, 'Django', '2025-12-26 08:00:00'),
(126, 98, 'Flask', '2025-12-26 08:30:00'),
(127, 99, 'TensorFlow', '2025-12-26 09:00:00'),
(128, 100, '消息队列', '2025-12-26 09:30:00'),
-- 额外标签（部分用户有多个标签）
(129, 3, '后端开发', '2025-12-02 09:00:00'),
(130, 4, '数据分析', '2025-12-02 09:30:00'),
(131, 5, '前端开发', '2025-12-02 10:00:00'),
(132, 6, '数据库', '2025-12-02 10:30:00'),
(133, 7, '深度学习', '2025-12-03 08:00:00'),
(134, 8, '缓存', '2025-12-03 08:30:00'),
(135, 9, '移动开发', '2025-12-03 09:00:00'),
(136, 10, 'Web开发', '2025-12-03 09:30:00'),
(137, 11, '架构设计', '2025-12-04 08:00:00'),
(138, 12, '数据科学', '2025-12-04 08:30:00'),
(139, 13, '全栈开发', '2025-12-04 09:00:00'),
(140, 14, '云原生', '2025-12-04 09:30:00'),
(141, 15, 'AI开发', '2025-12-05 08:00:00'),
(142, 16, '高并发', '2025-12-05 08:30:00'),
(143, 17, 'UI开发', '2025-12-05 09:00:00'),
(144, 18, 'ORM框架', '2025-12-05 09:30:00'),
(145, 19, '数据采集', '2025-12-06 08:00:00'),
(146, 20, '前端架构', '2025-12-06 08:30:00'),
(147, 21, '系统架构', '2025-12-06 09:00:00'),
(148, 22, '神经网络', '2025-12-06 09:30:00'),
(149, 23, '企业前端', '2025-12-07 08:00:00'),
(150, 24, '中间件', '2025-12-07 08:30:00');

-- -------------------------------------------------------------------------------
-- 4. api_info API信息表 (60条)
-- 状态分布: approved 42条(70%), pending 9条(15%), rejected 6条(10%), offline 3条(5%)
-- -------------------------------------------------------------------------------
INSERT INTO api_info (id, type_id, user_id, name, description, method, endpoint, target_url, request_params, response_params, price, price_unit, call_limit, whitelist_enabled, status, doc_url, rating, invoke_count, success_count, fail_count, create_time, update_time, deleted) VALUES
-- 图像识别类 (type_id=1) - 6条
(1, 1, 3, '通用图像识别API', '支持10000+物体识别的通用图像识别服务', 'POST', '/api/v1/image/recognition', 'http://localhost:8001', '[{"name":"image","type":"string","required":true,"description":"图片URL或Base64"}]', '[{"name":"label","type":"string","description":"识别标签"},{"name":"confidence","type":"float","description":"置信度"}]', 0.01, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/image-recognition', 4.5, 15000, 14800, 200, '2025-12-05 10:00:00', '2026-03-20 15:30:00', 0),
(2, 1, 4, '图像分类API', '基于深度学习的图像分类服务', 'POST', '/api/v1/image/classify', 'http://localhost:8002', '[{"name":"image","type":"string","required":true,"description":"图片数据"}]', '[{"name":"categories","type":"array","description":"分类结果"}]', 0.02, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/image-classify', 4.3, 8500, 8200, 300, '2025-12-06 09:00:00', '2026-03-18 11:20:00', 0),
(3, 1, 5, '图像质量检测API', '检测图片清晰度、亮度等质量指标', 'POST', '/api/v1/image/quality', 'http://localhost:8003', '[{"name":"image","type":"string","required":true,"description":"图片URL"}]', '[{"name":"score","type":"float","description":"质量评分"},{"name":"metrics","type":"object","description":"详细指标"}]', 0.005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/image-quality', 4.0, 5200, 5000, 200, '2025-12-07 14:00:00', '2026-03-15 09:45:00', 0),
(4, 1, 6, '图像风格转换API', '将图片转换为不同艺术风格', 'POST', '/api/v1/image/style', 'http://localhost:8004', '[{"name":"image","type":"string","required":true},{"name":"style","type":"string","required":true}]', '[{"name":"result_url","type":"string","description":"转换后图片URL"}]', 0.05, 'per_call', 100, 0, 'approved', 'http://docs.api-platform.com/image-style', 4.2, 3200, 3100, 100, '2025-12-08 11:00:00', '2026-03-12 16:00:00', 0),
(5, 1, 7, '图像增强API', '自动增强图片质量', 'POST', '/api/v1/image/enhance', 'http://localhost:8005', '[{"name":"image","type":"string","required":true}]', '[{"name":"enhanced_url","type":"string","description":"增强后图片URL"}]', 0.03, 'per_call', 0, 0, 'pending', 'http://docs.api-platform.com/image-enhance', 0.0, 0, 0, 0, '2026-03-25 10:00:00', '2026-03-25 10:00:00', 0),
(6, 1, 8, '图像水印API', '为图片添加水印', 'POST', '/api/v1/image/watermark', 'http://localhost:8006', '[{"name":"image","type":"string","required":true},{"name":"watermark","type":"string","required":true}]', '[{"name":"result_url","type":"string","description":"添加水印后图片URL"}]', 0.01, 'per_call', 0, 0, 'rejected', 'http://docs.api-platform.com/image-watermark', 0.0, 0, 0, 0, '2026-03-20 09:00:00', '2026-03-22 14:00:00', 0),
-- 文本处理类 (type_id=2) - 8条
(7, 2, 9, '文本情感分析API', '分析文本的情感倾向', 'POST', '/api/v1/text/sentiment', 'http://localhost:8010', '[{"name":"text","type":"string","required":true,"description":"待分析文本"}]', '[{"name":"sentiment","type":"string","description":"情感类型"},{"name":"score","type":"float","description":"情感得分"}]', 0.001, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/text-sentiment', 4.6, 25000, 24500, 500, '2025-12-10 08:00:00', '2026-03-22 10:30:00', 0),
(8, 2, 10, '关键词提取API', '从文本中提取关键词', 'POST', '/api/v1/text/keywords', 'http://localhost:8011', '[{"name":"text","type":"string","required":true},{"name":"count","type":"int","required":false}]', '[{"name":"keywords","type":"array","description":"关键词列表"}]', 0.0005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/text-keywords', 4.4, 18000, 17500, 500, '2025-12-11 09:00:00', '2026-03-20 14:20:00', 0),
(9, 2, 11, '文本摘要生成API', '自动生成文本摘要', 'POST', '/api/v1/text/summary', 'http://localhost:8012', '[{"name":"text","type":"string","required":true},{"name":"length","type":"int","required":false}]', '[{"name":"summary","type":"string","description":"摘要内容"}]', 0.002, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/text-summary', 4.1, 12000, 11800, 200, '2025-12-12 10:00:00', '2026-03-18 16:45:00', 0),
(10, 2, 12, '文本分类API', '对文本进行自动分类', 'POST', '/api/v1/text/classify', 'http://localhost:8013', '[{"name":"text","type":"string","required":true},{"name":"categories","type":"array","required":false}]', '[{"name":"category","type":"string","description":"分类结果"}]', 0.001, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/text-classify', 4.3, 9500, 9300, 200, '2025-12-13 11:00:00', '2026-03-15 11:30:00', 0),
(11, 2, 13, '命名实体识别API', '识别文本中的命名实体', 'POST', '/api/v1/text/ner', 'http://localhost:8014', '[{"name":"text","type":"string","required":true}]', '[{"name":"entities","type":"array","description":"实体列表"}]', 0.002, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/text-ner', 4.2, 7800, 7600, 200, '2025-12-14 08:00:00', '2026-03-12 09:00:00', 0),
(12, 2, 14, '文本纠错API', '自动检测并纠正文本错误', 'POST', '/api/v1/text/correct', 'http://localhost:8015', '[{"name":"text","type":"string","required":true}]', '[{"name":"corrected","type":"string","description":"纠正后文本"},{"name":"errors","type":"array","description":"错误列表"}]', 0.001, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/text-correct', 4.0, 6500, 6300, 200, '2025-12-15 09:00:00', '2026-03-10 14:00:00', 0),
(13, 2, 15, '文本相似度API', '计算两段文本的相似度', 'POST', '/api/v1/text/similarity', 'http://localhost:8016', '[{"name":"text1","type":"string","required":true},{"name":"text2","type":"string","required":true}]', '[{"name":"score","type":"float","description":"相似度得分"}]', 0.001, 'per_call', 0, 0, 'pending', 'http://docs.api-platform.com/text-similarity', 0.0, 0, 0, 0, '2026-03-24 10:00:00', '2026-03-24 10:00:00', 0),
(14, 2, 16, '敏感词过滤API', '检测并过滤敏感词汇', 'POST', '/api/v1/text/filter', 'http://localhost:8017', '[{"name":"text","type":"string","required":true}]', '[{"name":"filtered","type":"string","description":"过滤后文本"},{"name":"sensitive_words","type":"array","description":"敏感词列表"}]', 0.0005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/text-filter', 4.5, 11000, 10800, 200, '2025-12-16 10:00:00', '2026-03-08 11:00:00', 0),
-- 语音识别类 (type_id=3) - 5条
(15, 3, 17, '语音转文字API', '将语音转换为文字', 'POST', '/api/v1/voice/transcribe', 'http://localhost:8020', '[{"name":"audio","type":"string","required":true,"description":"音频文件URL或Base64"}]', '[{"name":"text","type":"string","description":"识别文本"},{"name":"confidence","type":"float","description":"置信度"}]', 0.02, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/voice-transcribe', 4.4, 6800, 6600, 200, '2025-12-17 08:00:00', '2026-03-20 15:00:00', 0),
(16, 3, 18, '语音合成API', '将文字转换为语音', 'POST', '/api/v1/voice/synthesize', 'http://localhost:8021', '[{"name":"text","type":"string","required":true},{"name":"voice","type":"string","required":false}]', '[{"name":"audio_url","type":"string","description":"音频文件URL"}]', 0.015, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/voice-synthesize', 4.2, 5200, 5000, 200, '2025-12-18 09:00:00', '2026-03-18 10:30:00', 0),
(17, 3, 19, '声纹识别API', '识别说话人身份', 'POST', '/api/v1/voice/speaker', 'http://localhost:8022', '[{"name":"audio","type":"string","required":true}]', '[{"name":"speaker_id","type":"string","description":"说话人ID"},{"name":"confidence","type":"float","description":"置信度"}]', 0.05, 'per_call', 0, 1, 'approved', 'http://docs.api-platform.com/voice-speaker', 4.0, 2800, 2700, 100, '2025-12-19 10:00:00', '2026-03-15 14:00:00', 0),
(18, 3, 20, '语音情感识别API', '识别语音中的情感', 'POST', '/api/v1/voice/emotion', 'http://localhost:8023', '[{"name":"audio","type":"string","required":true}]', '[{"name":"emotion","type":"string","description":"情感类型"},{"name":"score","type":"float","description":"情感得分"}]', 0.03, 'per_call', 0, 0, 'pending', 'http://docs.api-platform.com/voice-emotion', 0.0, 0, 0, 0, '2026-03-23 11:00:00', '2026-03-23 11:00:00', 0),
(19, 3, 21, '语音降噪API', '去除语音中的噪声', 'POST', '/api/v1/voice/denoise', 'http://localhost:8024', '[{"name":"audio","type":"string","required":true}]', '[{"name":"clean_audio_url","type":"string","description":"降噪后音频URL"}]', 0.02, 'per_call', 0, 0, 'rejected', 'http://docs.api-platform.com/voice-denoise', 0.0, 0, 0, 0, '2026-03-19 09:00:00', '2026-03-21 15:00:00', 0),
-- 数据查询类 (type_id=4) - 6条
(20, 4, 22, '企业信息查询API', '查询企业工商信息', 'GET', '/api/v1/data/company', 'http://localhost:8030', '[{"name":"keyword","type":"string","required":true,"description":"企业名称或注册号"}]', '[{"name":"company_info","type":"object","description":"企业信息"}]', 0.1, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/data-company', 4.3, 4500, 4400, 100, '2025-12-20 08:00:00', '2026-03-22 09:00:00', 0),
(21, 4, 23, '天气查询API', '查询城市天气信息', 'GET', '/api/v1/data/weather', 'http://localhost:8031', '[{"name":"city","type":"string","required":true,"description":"城市名称"}]', '[{"name":"weather","type":"object","description":"天气信息"}]', 0.001, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/data-weather', 4.5, 22000, 21500, 500, '2025-12-21 09:00:00', '2026-03-24 10:00:00', 0),
(22, 4, 24, 'IP地址查询API', '查询IP地址归属地', 'GET', '/api/v1/data/ip', 'http://localhost:8032', '[{"name":"ip","type":"string","required":true,"description":"IP地址"}]', '[{"name":"location","type":"object","description":"归属地信息"}]', 0.0001, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/data-ip', 4.6, 35000, 34500, 500, '2025-12-22 10:00:00', '2026-03-25 11:00:00', 0),
(23, 4, 25, '手机号归属地API', '查询手机号归属地', 'GET', '/api/v1/data/phone', 'http://localhost:8033', '[{"name":"phone","type":"string","required":true,"description":"手机号码"}]', '[{"name":"location","type":"object","description":"归属地信息"},{"name":"carrier","type":"string","description":"运营商"}]', 0.0005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/data-phone', 4.4, 18000, 17800, 200, '2025-12-23 11:00:00', '2026-03-23 14:00:00', 0),
(24, 4, 26, '银行卡验证API', '验证银行卡号有效性', 'GET', '/api/v1/data/bankcard', 'http://localhost:8034', '[{"name":"card","type":"string","required":true,"description":"银行卡号"}]', '[{"name":"valid","type":"boolean","description":"是否有效"},{"name":"bank","type":"string","description":"银行名称"}]', 0.01, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/data-bankcard', 4.2, 8500, 8300, 200, '2025-12-24 08:00:00', '2026-03-20 16:00:00', 0),
(25, 4, 27, '身份证验证API', '验证身份证号有效性', 'GET', '/api/v1/data/idcard', 'http://localhost:8035', '[{"name":"idcard","type":"string","required":true,"description":"身份证号"}]', '[{"name":"valid","type":"boolean","description":"是否有效"},{"name":"info","type":"object","description":"基本信息"}]', 0.02, 'per_call', 0, 0, 'offline', 'http://docs.api-platform.com/data-idcard', 4.0, 3200, 3100, 100, '2025-12-25 09:00:00', '2026-03-10 10:00:00', 0),
-- 地图服务类 (type_id=5) - 5条
(26, 5, 28, '地理编码API', '将地址转换为经纬度', 'GET', '/api/v1/map/geocode', 'http://localhost:8040', '[{"name":"address","type":"string","required":true,"description":"地址"}]', '[{"name":"location","type":"object","description":"经纬度坐标"}]', 0.005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/map-geocode', 4.5, 12000, 11800, 200, '2025-12-26 08:00:00', '2026-03-21 09:30:00', 0),
(27, 5, 29, '逆地理编码API', '将经纬度转换为地址', 'GET', '/api/v1/map/reverse', 'http://localhost:8041', '[{"name":"lat","type":"float","required":true},{"name":"lng","type":"float","required":true}]', '[{"name":"address","type":"string","description":"详细地址"}]', 0.005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/map-reverse', 4.4, 9500, 9300, 200, '2025-12-27 09:00:00', '2026-03-19 14:00:00', 0),
(28, 5, 30, '路径规划API', '规划两点间最优路径', 'GET', '/api/v1/map/route', 'http://localhost:8042', '[{"name":"origin","type":"string","required":true},{"name":"destination","type":"string","required":true}]', '[{"name":"route","type":"object","description":"路径信息"}]', 0.01, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/map-route', 4.3, 7800, 7600, 200, '2025-12-28 10:00:00', '2026-03-17 11:00:00', 0),
(29, 5, 31, '周边搜索API', '搜索周边POI信息', 'GET', '/api/v1/map/search', 'http://localhost:8043', '[{"name":"location","type":"string","required":true},{"name":"keyword","type":"string","required":true}]', '[{"name":"pois","type":"array","description":"POI列表"}]', 0.008, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/map-search', 4.2, 6200, 6000, 200, '2025-12-29 11:00:00', '2026-03-15 16:00:00', 0),
(30, 5, 32, '行政区划API', '查询行政区划信息', 'GET', '/api/v1/map/district', 'http://localhost:8044', '[{"name":"keywords","type":"string","required":true,"description":"关键词"}]', '[{"name":"districts","type":"array","description":"行政区划列表"}]', 0.001, 'per_call', 0, 0, 'pending', 'http://docs.api-platform.com/map-district', 0.0, 0, 0, 0, '2026-03-22 10:00:00', '2026-03-22 10:00:00', 0),
-- 支付接口类 (type_id=6) - 4条
(31, 6, 3, '支付宝支付API', '支付宝支付接口', 'POST', '/api/v1/pay/alipay', 'http://localhost:8050', '[{"name":"order_no","type":"string","required":true},{"name":"amount","type":"float","required":true}]', '[{"name":"pay_url","type":"string","description":"支付链接"}]', 0.5, 'per_call', 0, 1, 'approved', 'http://docs.api-platform.com/pay-alipay', 4.6, 3200, 3150, 50, '2025-12-30 08:00:00', '2026-03-23 10:00:00', 0),
(32, 6, 4, '微信支付API', '微信支付接口', 'POST', '/api/v1/pay/wechat', 'http://localhost:8051', '[{"name":"order_no","type":"string","required":true},{"name":"amount","type":"float","required":true}]', '[{"name":"qr_code","type":"string","description":"支付二维码"}]', 0.5, 'per_call', 0, 1, 'approved', 'http://docs.api-platform.com/pay-wechat', 4.5, 2800, 2750, 50, '2025-12-31 09:00:00', '2026-03-22 11:00:00', 0),
(33, 6, 5, '银联支付API', '银联支付接口', 'POST', '/api/v1/pay/unionpay', 'http://localhost:8052', '[{"name":"order_no","type":"string","required":true},{"name":"amount","type":"float","required":true}]', '[{"name":"pay_url","type":"string","description":"支付链接"}]', 0.3, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/pay-unionpay', 4.3, 1500, 1480, 20, '2026-01-01 10:00:00', '2026-03-20 14:00:00', 0),
(34, 6, 6, '订单查询API', '查询支付订单状态', 'GET', '/api/v1/pay/query', 'http://localhost:8053', '[{"name":"order_no","type":"string","required":true}]', '[{"name":"status","type":"string","description":"订单状态"},{"name":"amount","type":"float","description":"订单金额"}]', 0.01, 'per_call', 0, 0, 'rejected', 'http://docs.api-platform.com/pay-query', 0.0, 0, 0, 0, '2026-03-18 09:00:00', '2026-03-20 15:00:00', 0),
-- 短信服务类 (type_id=7) - 5条
(35, 7, 7, '短信验证码API', '发送短信验证码', 'POST', '/api/v1/sms/code', 'http://localhost:8060', '[{"name":"phone","type":"string","required":true,"description":"手机号"}]', '[{"name":"code","type":"string","description":"验证码"},{"name":"expire","type":"int","description":"有效期(秒)"}]', 0.05, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/sms-code', 4.4, 15000, 14800, 200, '2026-01-02 08:00:00', '2026-03-24 09:00:00', 0),
(36, 7, 8, '短信通知API', '发送短信通知', 'POST', '/api/v1/sms/notify', 'http://localhost:8061', '[{"name":"phone","type":"string","required":true},{"name":"content","type":"string","required":true}]', '[{"name":"msg_id","type":"string","description":"消息ID"}]', 0.04, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/sms-notify', 4.3, 8500, 8300, 200, '2026-01-03 09:00:00', '2026-03-22 10:00:00', 0),
(37, 7, 9, '营销短信API', '发送营销短信', 'POST', '/api/v1/sms/marketing', 'http://localhost:8062', '[{"name":"phones","type":"array","required":true},{"name":"content","type":"string","required":true}]', '[{"name":"success_count","type":"int","description":"成功数量"}]', 0.03, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/sms-marketing', 4.1, 5200, 5000, 200, '2026-01-04 10:00:00', '2026-03-20 11:00:00', 0),
(38, 7, 10, '语音短信API', '发送语音短信', 'POST', '/api/v1/sms/voice', 'http://localhost:8063', '[{"name":"phone","type":"string","required":true},{"name":"content","type":"string","required":true}]', '[{"name":"call_id","type":"string","description":"通话ID"}]', 0.08, 'per_call', 0, 0, 'pending', 'http://docs.api-platform.com/sms-voice', 0.0, 0, 0, 0, '2026-03-21 10:00:00', '2026-03-21 10:00:00', 0),
(39, 7, 11, '国际短信API', '发送国际短信', 'POST', '/api/v1/sms/international', 'http://localhost:8064', '[{"name":"phone","type":"string","required":true},{"name":"content","type":"string","required":true},{"name":"country","type":"string","required":true}]', '[{"name":"msg_id","type":"string","description":"消息ID"}]', 0.2, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/sms-international', 4.2, 2800, 2700, 100, '2026-01-05 11:00:00', '2026-03-18 14:00:00', 0),
-- 人脸识别类 (type_id=8) - 6条
(40, 8, 12, '人脸检测API', '检测图片中的人脸', 'POST', '/api/v1/face/detect', 'http://localhost:8070', '[{"name":"image","type":"string","required":true,"description":"图片URL或Base64"}]', '[{"name":"faces","type":"array","description":"人脸列表"},{"name":"count","type":"int","description":"人脸数量"}]', 0.01, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/face-detect', 4.5, 9500, 9300, 200, '2026-01-06 08:00:00', '2026-03-23 09:00:00', 0),
(41, 8, 13, '人脸比对API', '比对两张人脸的相似度', 'POST', '/api/v1/face/compare', 'http://localhost:8071', '[{"name":"image1","type":"string","required":true},{"name":"image2","type":"string","required":true}]', '[{"name":"score","type":"float","description":"相似度得分"},{"name":"is_same","type":"boolean","description":"是否同一人"}]', 0.02, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/face-compare', 4.4, 6800, 6600, 200, '2026-01-07 09:00:00', '2026-03-21 10:00:00', 0),
(42, 8, 14, '人脸搜索API', '在人脸库中搜索相似人脸', 'POST', '/api/v1/face/search', 'http://localhost:8072', '[{"name":"image","type":"string","required":true},{"name":"group_id","type":"string","required":true}]', '[{"name":"results","type":"array","description":"搜索结果"}]', 0.03, 'per_call', 0, 1, 'approved', 'http://docs.api-platform.com/face-search', 4.3, 4500, 4400, 100, '2026-01-08 10:00:00', '2026-03-19 11:00:00', 0),
(43, 8, 15, '人脸活体检测API', '检测人脸是否为真人', 'POST', '/api/v1/face/liveness', 'http://localhost:8073', '[{"name":"image","type":"string","required":true}]', '[{"name":"is_live","type":"boolean","description":"是否真人"},{"name":"score","type":"float","description":"活体得分"}]', 0.05, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/face-liveness', 4.2, 3200, 3100, 100, '2026-01-09 11:00:00', '2026-03-17 14:00:00', 0),
(44, 8, 16, '人脸属性分析API', '分析人脸属性信息', 'POST', '/api/v1/face/attribute', 'http://localhost:8074', '[{"name":"image","type":"string","required":true}]', '[{"name":"age","type":"int","description":"年龄"},{"name":"gender","type":"string","description":"性别"},{"name":"emotion","type":"string","description":"表情"}]', 0.01, 'per_call', 0, 0, 'pending', 'http://docs.api-platform.com/face-attribute', 0.0, 0, 0, 0, '2026-03-20 10:00:00', '2026-03-20 10:00:00', 0),
(45, 8, 17, '人脸注册API', '将人脸注册到人脸库', 'POST', '/api/v1/face/register', 'http://localhost:8075', '[{"name":"image","type":"string","required":true},{"name":"user_id","type":"string","required":true}]', '[{"name":"face_id","type":"string","description":"人脸ID"}]', 0.02, 'per_call', 0, 0, 'rejected', 'http://docs.api-platform.com/face-register', 0.0, 0, 0, 0, '2026-03-17 09:00:00', '2026-03-19 15:00:00', 0),
-- OCR识别类 (type_id=9) - 8条
(46, 9, 18, '通用OCR识别API', '识别图片中的文字', 'POST', '/api/v1/ocr/general', 'http://localhost:8080', '[{"name":"image","type":"string","required":true,"description":"图片URL或Base64"}]', '[{"name":"text","type":"string","description":"识别文本"},{"name":"words","type":"array","description":"词语列表"}]', 0.005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/ocr-general', 4.6, 18000, 17800, 200, '2026-01-10 08:00:00', '2026-03-24 10:00:00', 0),
(47, 9, 19, '身份证OCR识别API', '识别身份证信息', 'POST', '/api/v1/ocr/idcard', 'http://localhost:8081', '[{"name":"image","type":"string","required":true}]', '[{"name":"name","type":"string","description":"姓名"},{"name":"id_number","type":"string","description":"身份证号"}]', 0.01, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/ocr-idcard', 4.5, 12000, 11800, 200, '2026-01-11 09:00:00', '2026-03-22 11:00:00', 0),
(48, 9, 20, '银行卡OCR识别API', '识别银行卡信息', 'POST', '/api/v1/ocr/bankcard', 'http://localhost:8082', '[{"name":"image","type":"string","required":true}]', '[{"name":"card_number","type":"string","description":"银行卡号"},{"name":"bank","type":"string","description":"银行名称"}]', 0.01, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/ocr-bankcard', 4.4, 8500, 8300, 200, '2026-01-12 10:00:00', '2026-03-20 14:00:00', 0),
(49, 9, 21, '驾驶证OCR识别API', '识别驾驶证信息', 'POST', '/api/v1/ocr/driver_license', 'http://localhost:8083', '[{"name":"image","type":"string","required":true}]', '[{"name":"name","type":"string","description":"姓名"},{"name":"license_no","type":"string","description":"驾驶证号"}]', 0.015, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/ocr-driver', 4.3, 5500, 5400, 100, '2026-01-13 11:00:00', '2026-03-18 16:00:00', 0),
(50, 9, 22, '行驶证OCR识别API', '识别行驶证信息', 'POST', '/api/v1/ocr/vehicle_license', 'http://localhost:8084', '[{"name":"image","type":"string","required":true}]', '[{"name":"plate_no","type":"string","description":"车牌号"},{"name":"owner","type":"string","description":"所有人"}]', 0.015, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/ocr-vehicle', 4.2, 4200, 4100, 100, '2026-01-14 08:00:00', '2026-03-16 09:00:00', 0),
(51, 9, 23, '营业执照OCR识别API', '识别营业执照信息', 'POST', '/api/v1/ocr/business_license', 'http://localhost:8085', '[{"name":"image","type":"string","required":true}]', '[{"name":"company_name","type":"string","description":"企业名称"},{"name":"credit_code","type":"string","description":"统一社会信用代码"}]', 0.02, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/ocr-business', 4.1, 3800, 3700, 100, '2026-01-15 09:00:00', '2026-03-14 11:00:00', 0),
(52, 9, 24, '车牌OCR识别API', '识别车牌号码', 'POST', '/api/v1/ocr/plate', 'http://localhost:8086', '[{"name":"image","type":"string","required":true}]', '[{"name":"plate_number","type":"string","description":"车牌号"},{"name":"color","type":"string","description":"车牌颜色"}]', 0.008, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/ocr-plate', 4.4, 6200, 6100, 100, '2026-01-16 10:00:00', '2026-03-12 14:00:00', 0),
(53, 9, 25, '发票OCR识别API', '识别发票信息', 'POST', '/api/v1/ocr/invoice', 'http://localhost:8087', '[{"name":"image","type":"string","required":true}]', '[{"name":"invoice_code","type":"string","description":"发票代码"},{"name":"amount","type":"float","description":"金额"}]', 0.02, 'per_call', 0, 0, 'offline', 'http://docs.api-platform.com/ocr-invoice', 4.0, 2500, 2450, 50, '2026-01-17 11:00:00', '2026-03-08 10:00:00', 0),
-- 翻译服务类 (type_id=10) - 7条
(54, 10, 26, '通用翻译API', '支持多语言互译', 'POST', '/api/v1/translate/general', 'http://localhost:8090', '[{"name":"text","type":"string","required":true},{"name":"from","type":"string","required":true},{"name":"to","type":"string","required":true}]', '[{"name":"translated","type":"string","description":"翻译结果"}]', 0.001, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/translate-general', 4.5, 28000, 27500, 500, '2026-01-18 08:00:00', '2026-03-25 09:00:00', 0),
(55, 10, 27, '文档翻译API', '翻译文档内容', 'POST', '/api/v1/translate/document', 'http://localhost:8091', '[{"name":"document","type":"string","required":true},{"name":"from","type":"string","required":true},{"name":"to","type":"string","required":true}]', '[{"name":"translated_url","type":"string","description":"翻译后文档URL"}]', 0.1, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/translate-document', 4.3, 3500, 3400, 100, '2026-01-19 09:00:00', '2026-03-23 10:00:00', 0),
(56, 10, 28, '图片翻译API', '翻译图片中的文字', 'POST', '/api/v1/translate/image', 'http://localhost:8092', '[{"name":"image","type":"string","required":true},{"name":"from","type":"string","required":true},{"name":"to","type":"string","required":true}]', '[{"name":"translated_url","type":"string","description":"翻译后图片URL"}]', 0.05, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/translate-image', 4.2, 2800, 2700, 100, '2026-01-20 10:00:00', '2026-03-21 11:00:00', 0),
(57, 10, 29, '语音翻译API', '翻译语音内容', 'POST', '/api/v1/translate/voice', 'http://localhost:8093', '[{"name":"audio","type":"string","required":true},{"name":"from","type":"string","required":true},{"name":"to","type":"string","required":true}]', '[{"name":"translated_audio_url","type":"string","description":"翻译后语音URL"}]', 0.1, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/translate-voice', 4.1, 1800, 1750, 50, '2026-01-21 11:00:00', '2026-03-19 14:00:00', 0),
(58, 10, 30, '专业领域翻译API', '专业术语翻译', 'POST', '/api/v1/translate/professional', 'http://localhost:8094', '[{"name":"text","type":"string","required":true},{"name":"field","type":"string","required":true},{"name":"from","type":"string","required":true},{"name":"to","type":"string","required":true}]', '[{"name":"translated","type":"string","description":"翻译结果"}]', 0.005, 'per_call', 0, 0, 'approved', 'http://docs.api-platform.com/translate-professional', 4.4, 4200, 4100, 100, '2026-01-22 08:00:00', '2026-03-17 16:00:00', 0),
(59, 10, 31, '批量翻译API', '批量翻译文本', 'POST', '/api/v1/translate/batch', 'http://localhost:8095', '[{"name":"texts","type":"array","required":true},{"name":"from","type":"string","required":true},{"name":"to","type":"string","required":true}]', '[{"name":"translated","type":"array","description":"翻译结果列表"}]', 0.0008, 'per_call', 0, 0, 'pending', 'http://docs.api-platform.com/translate-batch', 0.0, 0, 0, 0, '2026-03-19 10:00:00', '2026-03-19 10:00:00', 0),
(60, 10, 32, '实时翻译API', '实时流式翻译', 'POST', '/api/v1/translate/realtime', 'http://localhost:8096', '[{"name":"stream","type":"string","required":true},{"name":"from","type":"string","required":true},{"name":"to","type":"string","required":true}]', '[{"name":"translated","type":"string","description":"翻译结果"}]', 0.002, 'per_call', 0, 0, 'offline', 'http://docs.api-platform.com/translate-realtime', 3.8, 1500, 1450, 50, '2026-01-23 09:00:00', '2026-03-05 10:00:00', 0);
