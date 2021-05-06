package com.example.eBeydemo.ebay;

import com.example.eBeydemo.ebay.model.OAuthResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * @program: eBey-demo
 * @description
 * @author: HuGoldWater
 * @create: 2019-08-01 16:44
 **/

public class OAuth2Api {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Api.class);


    /**
     * 根据授权url地址响应的code，去eBay获取token
     *
     * @param environment
     * @param code
     * @return
     * @throws IOException
     */
    public OAuthResponse exchangeCodeForAccessToken(EBayEnvironment environment, String code) throws IOException {
        OkHttpClient client = new OkHttpClient();

        StringBuilder requestData = new StringBuilder();
        requestData.append("grant_type=authorization_code").append("&");
        requestData.append(String.format("redirect_uri=%s", environment.getRuName())).append("&");
        requestData.append(String.format("code=%s", code));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestData.toString());

        Request request = new Request.Builder().url(environment.getTokenUrl())
                .header("Authorization", buildAuthorization(environment))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return OAuth2Util.parseUserToken(response.body().string());
        } else {
            return OAuth2Util.handleError(response);
        }
    }

    /**
     * 刷新token
     *
     * @param environment
     * @param scopes
     * @return
     * @throws IOException
     */
    public OAuthResponse refreshToken(EBayEnvironment environment, List<String> scopes, final String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String scope = buildScopeForRequest(scopes).orElse("");
        StringBuilder requestData = new StringBuilder();
        requestData.append("grant_type=refresh_token").append("&");
        requestData.append("refresh_token=").append(token).append("&");
        requestData.append("scope=").append(scope);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestData.toString());
        Request request = new Request.Builder().url(environment.getTokenUrl())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", buildAuthorization(environment))
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return OAuth2Util.parseUserToken(response.body().string());
        } else {
            return OAuth2Util.handleError(response);
        }
    }

    /**
     * 获取授权url
     *
     * @param environment 环境
     * @param scopes      授权的接口地址
     * @param state
     * @return
     */
    public String generateUserAuthorizationUrl(EBayEnvironment environment, List<String> scopes, Optional<String> state) {
        StringBuilder sb = new StringBuilder();
        String scope = buildScopeForRequest(scopes).orElse("");

        sb.append(environment.getAuthorizeUrl()).append("?");
        sb.append("client_id=").append(environment.getAppId()).append("&");
        sb.append("response_type=code").append("&");
        sb.append("redirect_uri=").append(environment.getRuName()).append("&");
        sb.append("scope=").append(scope).append("&");
        if (state.isPresent()) {
            sb.append("state=").append(state.get());
        }
        logger.debug("authorize_url=" + sb.toString());
        return sb.toString();
    }

    static Optional<String> buildScopeForRequest(List<String> scopes) {
        String scopeList = null;
        if (null != scopes && !scopes.isEmpty()) {
            scopeList = String.join("+", scopes);
        }
        return Optional.of(scopeList);
    }

    private String buildAuthorization(EBayEnvironment environment) {
        StringBuilder sb = new StringBuilder();
        sb.append(environment.getAppId()).append(":").append(environment.getCertId());
        byte[] encodeBytes = Base64.getEncoder().encode(sb.toString().getBytes());
        return "Basic " + new String(encodeBytes);
    }
}
