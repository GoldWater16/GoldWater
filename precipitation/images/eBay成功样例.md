eBay对接流程

首先，你必须要有个eBay的开发者账号，然后使用开发者账号的信息去跳转到eBay的授权页面，拿到授权code，之后，你就可以随意控制卖家的所有操作了。

下面详细的说下操作流程：

##### 第一步：

![image-20190801172006268](/Users/lcp/Library/Application Support/typora-user-images/image-20190801172006268.png)

##### 第二步：

点击上图的”User Tokens“，









首先，你得弄明白这张图：

![image-20190801150933071](/Users/lcp/Documents/ebay-picture/授权流程图.png)

这个图表达的意思是：先从应用端请求eBay授权服务，跳转到eBay授权页面，由卖家登录后得到授权码(Authorization code)，再使用这个授权码去请求eBay的接口获取token，最后，用这个token去调用eBay的api接口；

具体流程参考官网文档：https://developer.ebay.com/api-docs/static/oauth-authorization-code-grant.html

### 第一步：获取eBay授权页面的地址

```java
@GetMapping("getAuthUrl")
public Mono<String> getAuthUrl() {
    OAuth2Api oauth2Api = new OAuth2Api();
    List<String> scopeList = new ArrayList<>();
    scopeList.add("https://api.ebay.com/oauth/api_scope");
    scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment");
    scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment.readonly");
    String authorization_url = oauth2Api.generateUserAuthorizationUrl(EBayEnvironment.PRODUCTION, scopeList, Optional.empty());
    return Mono.just(authorization_url);
}
```

### 第二步：根据授权url地址响应的code，去eBay获取token

```java
@GetMapping("getToken/{code}")
public Mono<OAuthResponse> getToken(@PathVariable("code") final String code) throws IOException {
    OAuth2Api oauth2Api = new OAuth2Api();
    OAuthResponse oauth2Response = oauth2Api.exchangeCodeForAccessToken(EBayEnvironment.PRODUCTION, code);
    return Mono.just(oauth2Response);
}
```

### 第三步：根据token获取订单数据

```java
@GetMapping("getOrders/{token}")
public Mono<Response> getOrders(@PathVariable("token") final String token) throws IOException {
    final OkHttpClient okHttpClient = new OkHttpClient();
    final Request request = new Request.Builder()
            .url("https://api.ebay.com/sell/fulfillment/v1/order")
            .get()
            .addHeader("Authorization", "Bearer " + token)
            .build();
    final Response response = okHttpClient.newCall(request).execute();
    return Mono.just(response);
}
```



demo地址：https://github.com/GoldWater16/eBay-demo









