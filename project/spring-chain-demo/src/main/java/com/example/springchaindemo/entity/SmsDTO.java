package com.example.springchaindemo.entity;

/**
 * @projectName: spring-chain-demo
 * @className: SmsDTO
 * @description: 发送短信DTO
 * @author: HuGoldWater
 * @create: 2020-03-30 15:35
 **/
public class SmsDTO {
    /**手机号**/
    private String mobile;
    /**短信验证码**/
    private String verificationCode;

    public String getMobile() {
        return mobile;
    }

    public SmsDTO setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public SmsDTO setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }
}
