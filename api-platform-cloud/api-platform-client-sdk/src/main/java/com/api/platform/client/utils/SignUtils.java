package com.api.platform.client.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具类（SDK端） —— 生成AK/SK签名
 *
 * 签名算法：SHA256(body + "." + secretKey)
 * 与网关 AuthFilter 中的签名校验逻辑对应
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
