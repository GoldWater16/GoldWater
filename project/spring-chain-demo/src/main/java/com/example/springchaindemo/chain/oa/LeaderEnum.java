package com.example.springchaindemo.chain.oa;

/**
 * @projectName: spring-chain-demo
 * @className: LeaderEnum
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 18:09
 **/
public enum LeaderEnum {
    TEAM_LEADER(0,"组长"),
    SUPERVISOR_LEADER(1,"主管"),
    BOSS_LEADER(2,"老板");

    LeaderEnum(int code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    private int code;
    private String remark;

    public int getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }
}
