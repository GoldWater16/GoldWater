package com.example.eBeydemo;

import com.alibaba.fastjson.JSONObject;
import com.example.eBeydemo.ebay.EBayEnvironment;
import com.example.eBeydemo.ebay.OAuth2Api;
import com.example.eBeydemo.ebay.model.OAuthResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class EBayController {

    /**
     * 获取授权url地址
     *
     * @return
     */
    @GetMapping("getAuthUrl")
    public Mono<String> getAuthUrl() {
        OAuth2Api oauth2Api = new OAuth2Api();
        List<String> scopeList = new ArrayList<>();
        scopeList.add("https://api.ebay.com/oauth/api_scope");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.finances");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment.readonly");
        String authorization_url = oauth2Api.generateUserAuthorizationUrl(EBayEnvironment.PRODUCTION, scopeList, Optional.empty());
        return Mono.just(authorization_url);
    }

    /**
     * 根据授权url地址响应的code，去eBay获取token
     *
     * @param code
     * @return
     * @throws IOException
     */
    @GetMapping("getToken/{code}")
    public Mono<OAuthResponse> getToken(@PathVariable("code") final String code) throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        OAuthResponse oauth2Response = oauth2Api.exchangeCodeForAccessToken(EBayEnvironment.PRODUCTION, code);
        return Mono.just(oauth2Response);
    }

    /**
     * 根据刷新令牌中的token去获取新的token
     * @param token
     * @return
     * @throws IOException
     */
    @PostMapping(value = "refreshToken", produces = {"application/json"})
    public Mono<OAuthResponse> refreshToken(@RequestBody final String token) throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        List<String> scopeList = new ArrayList<>();
        scopeList.add("https://api.ebay.com/oauth/api_scope");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.finances");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment.readonly");
        return Mono.just(oauth2Api.refreshToken(EBayEnvironment.PRODUCTION, scopeList, JSONObject.parseObject(token).getString("token")));
    }

    /**
     * 根据token获取订单数据
     *
     * @param token
     * @return
     * @throws IOException
     */
    @PostMapping(value = "getOrders", produces = {"application/json"})
    public Mono<String> getOrders(@RequestBody final String token) throws IOException {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://api.ebay.com/sell/fulfillment/v1/order")
                .get()
                .addHeader("Authorization", "Bearer " + JSONObject.parseObject(token).getString("token"))
                .build();
        final Response response = okHttpClient.newCall(request).execute();
        return Mono.just(response.body().string());
    }

    @PostMapping(value = "getPayouts", produces = {"application/json"})
    public Mono<String> getPayouts(@RequestBody final String token) throws IOException {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://api.ebay.com/sell/finances/v1_alpha/payout")
                .get()
                .addHeader("Authorization", "Bearer " + JSONObject.parseObject(token).getString("token"))
                .build();
        final Response response = okHttpClient.newCall(request).execute();
        return Mono.just(response.body().string());
    }
}
