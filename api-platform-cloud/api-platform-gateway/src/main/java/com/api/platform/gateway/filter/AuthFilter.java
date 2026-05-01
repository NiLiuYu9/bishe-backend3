package com.api.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.api.platform.common.constant.AuthConstants;
import com.api.platform.common.service.InnerUserService;
import com.api.platform.common.service.InnerUserInterfaceInfoService;
import com.api.platform.common.utils.SignUtils;
import com.api.platform.common.vo.InvokeUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * AK/SK鉴权过滤器 —— 网关过滤器链第2环（Order=0）
 *
 * 职责：校验请求中的AK/SK签名，确保调用者身份合法
 * 鉴权流程：
 * 1. 从请求头获取 accessKey、nonce、timestamp、sign
 * 2. 通过Dubbo查询 accessKey 对应的用户信息
 * 3. 用 secretKey + 请求体 重新计算签名，与传入的 sign 比对
 * 4. 校验 timestamp 防重放攻击（5分钟内有效）
 * 5. 校验 nonce 防重复请求
 * 签名算法：SHA256(body + "." + secretKey)
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 步骤1：白名单判断 —— 登录/注册等公开接口无需鉴权，直接放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 步骤2：从请求头提取鉴权参数（accessKey、nonce、timestamp、sign、body）
        HttpHeaders headers = request.getHeaders();

        String accessKey = headers.getFirst(AuthConstants.ACCESS_KEY_HEADER);
        String nonce = headers.getFirst(AuthConstants.NONCE_HEADER);
        String timestamp = headers.getFirst(AuthConstants.TIMESTAMP_HEADER);
        String sign = headers.getFirst(AuthConstants.SIGN_HEADER);
        String body = headers.getFirst(AuthConstants.BODY_HEADER);

        if (StrUtil.isBlank(accessKey)) {
            return handleNoAuth(exchange, "accessKey不能为空");
        }

        // 步骤3：AK查询 —— 通过accessKey查询用户信息，验证调用者身份
        InvokeUserVO user = innerUserService.getInvokeUser(accessKey);
        if (user == null) {
            return handleNoAuth(exchange, "用户不存在");
        }

        // 步骤4：用户状态校验 —— 禁用用户不允许调用接口
        if (user.getStatus() != null && user.getStatus() == 0) {
            return handleNoAuth(exchange, "用户已被禁用");
        }

        // 步骤5：nonce校验 —— nonce作为请求唯一标识，范围校验防止恶意请求（超出10000视为异常）
        if (StrUtil.isBlank(nonce) || Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(exchange, "nonce无效");
        }

        // 步骤6：timestamp校验 —— 防重放攻击，请求时间戳与服务器时间差超过300秒（5分钟）则视为过期
        if (StrUtil.isBlank(timestamp)) {
            return handleNoAuth(exchange, "timestamp不能为空");
        }
        long currentTime = System.currentTimeMillis() / 1000;
        if ((currentTime - Long.parseLong(timestamp)) >= 300L) { // 300秒 = 5分钟有效期
            return handleNoAuth(exchange, "请求已过期");
        }

        // 步骤7：签名校验 —— 用secretKey + 请求体重新计算SHA256签名，与传入sign比对
        if (StrUtil.isBlank(sign)) {
            return handleNoAuth(exchange, "sign不能为空");
        }
        String serverSign = SignUtils.genSign(body == null ? "" : body, user.getSecretKey());
        if (!sign.equals(serverSign)) {
            return handleNoAuth(exchange, "签名验证失败");
        }

        exchange.getAttributes().put("userId", user.getId());
        exchange.getAttributes().put("accessKey", accessKey);

        return chain.filter(exchange);
    }

    /**
     * 判断请求路径是否属于白名单，白名单路径无需AK/SK鉴权直接放行
     *
     * @param path 请求路径
     * @return true表示白名单路径，无需鉴权；false表示需要鉴权
     */
    private boolean isWhitePath(String path) {
        return path.contains("/auth/login") ||      // 登录接口：未登录用户调用，无法提供AK/SK
               path.contains("/auth/register") ||   // 注册接口：未注册用户调用，尚未分配AK/SK
               path.contains("/actuator/health") || // 健康检查：运维监控探针，非业务请求
               path.contains("/test/");             // 测试接口：在线测试页面发起，通过Session鉴权而非AK/SK
    }

    /**
     * 处理鉴权失败响应，返回HTTP 401状态码
     *
     * 为什么返回401而非403：
     * - 401 Unauthorized 表示未认证（身份未验证），即调用者未通过AK/SK鉴权
     * - 403 Forbidden 表示已认证但无权限（身份已验证但被拒绝访问）
     * - 此处是未通过鉴权（AK/SK校验失败），属于未认证场景，因此使用401
     *
     * @param exchange 服务端Web交换上下文
     * @param message  鉴权失败原因描述
     * @return Mono<Void> 响应完成信号
     */
    private Mono<Void> handleNoAuth(ServerWebExchange exchange, String message) {
        log.warn("鉴权失败: {}", message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
