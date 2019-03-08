package com.hjs.prototype;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hjs
 * @date 2019/3/72:29
 * @Dec
 */
@Getter
@Setter
public class SimpleCloneWeeklyLog implements Cloneable {
    private Attachment attachment;
    private String name;
    private String date;
    private String content;


    public SimpleCloneWeeklyLog simpleClone() {
        try {
            return (SimpleCloneWeeklyLog) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.out.println("不支持克隆");
            return null;
        }
    }
}
