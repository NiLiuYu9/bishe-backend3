package com.api.platform.common.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具类（公共模块） —— 生成AK/SK签名
 *
 * 签名算法：SHA256(body + "." + secretKey)
 * 网关 AuthFilter 使用此工具类校验请求签名
 */
public class SignUtils {

    private SignUtils() {
    }

    public static String genSign(String body, String secretKey) {
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        return digester.digestHex(content);
    }
}
