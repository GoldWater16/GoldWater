package com.example.eBeydemo.ebay.model;

import java.util.Date;

/**
 * @program: eBey-demo
 * @description
 * @author: HuGoldWater
 * @create: 2019-08-01 16:44
 **/

public class AccessToken {
    private String token;
    private Date expiresOn;//过期日期
    private int expiresIn;//过期时间
    private TokenType tokenType;

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccessToken{");
        sb.append(", token='").append(token).append('\'');
        sb.append(", expiresOn=").append(expiresOn);
        sb.append('}');
        return sb.toString();
    }
}
