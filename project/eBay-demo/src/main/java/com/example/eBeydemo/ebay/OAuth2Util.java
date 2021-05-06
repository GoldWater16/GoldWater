package com.example.eBeydemo.ebay;


import com.example.eBeydemo.ebay.model.*;
import com.google.gson.Gson;
import okhttp3.Response;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @program: eBey-demo
 * @description
 * @author: HuGoldWater
 * @create: 2019-08-01 16:44
 **/

class OAuth2Util {

    private static Date generateExpiration(int expiresIn) {
        return DateTime.now().plusSeconds(expiresIn).toDate();
    }

    static Optional<String> buildScopeForRequest(List<String> scopes) {
        String scopeList = null;
        if (null != scopes && !scopes.isEmpty()) {
            scopeList = String.join("+", scopes);
        }
        return Optional.of(scopeList);
    }

    static OAuthResponse parseUserToken(String s) {
        Gson gson = new Gson();
        TokenResponse tokenResponse = gson.fromJson(s, TokenResponse.class);
        AccessToken accessToken = new AccessToken();
        accessToken.setTokenType(TokenType.USER);
        accessToken.setToken(tokenResponse.getAccessToken());
        accessToken.setExpiresOn(generateExpiration(tokenResponse.getExpiresIn()));
        accessToken.setExpiresIn(tokenResponse.getExpiresIn());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenResponse.getRefreshToken());
        refreshToken.setExpiresOn(generateExpiration(tokenResponse.getRefreshTokenExpiresIn()));
        refreshToken.setExpiresIn(tokenResponse.getRefreshTokenExpiresIn());

        return new OAuthResponse(Optional.of(accessToken), Optional.of(refreshToken));
    }

    static OAuthResponse handleError(Response response) throws IOException {
        String errorMessage = response.body().string();
        return new OAuthResponse(errorMessage);
    }
}
