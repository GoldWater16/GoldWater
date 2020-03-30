package com.example.springchaindemo.entity;

/**
 * @projectName: spring-chain-demo
 * @className: Employee
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 18:01
 **/
public class Employee {
    /**员工名字**/
    private String name;
    /**员工年龄**/
    private String age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
