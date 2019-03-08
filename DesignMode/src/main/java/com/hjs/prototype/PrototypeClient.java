package com.hjs.prototype;

/**
 * @author hjs
 * @date 2019/3/72:32
 * @Dec
 */
public class PrototypeClient {
    public static void main(String[] args) throws Exception {
        SimpleCloneWeeklyLog previousSimpleCloneWeeklyLog = new SimpleCloneWeeklyLog();
        previousSimpleCloneWeeklyLog.setAttachment(new Attachment());
        //---------------------浅克隆start--------------------------------
        SimpleCloneWeeklyLog newSimpleCloneWeeklyLog = previousSimpleCloneWeeklyLog.simpleClone();
        System.out.println("浅克隆周报：" + (newSimpleCloneWeeklyLog == previousSimpleCloneWeeklyLog));//false
        System.out.println("浅克隆附件：" + (newSimpleCloneWeeklyLog.getAttachment() == previousSimpleCloneWeeklyLog.getAttachment()));//true
        //----------------------浅克隆end-------------------------------

        DeepCloneWeeklyLog preDeepCloneWeeklyLog = new DeepCloneWeeklyLog();
        preDeepCloneWeeklyLog.setAttachment(new Attachment());
        DeepCloneWeeklyLog deepCloneWeeklyLog = preDeepCloneWeeklyLog.deepClone();
        System.out.println("深克隆周报：" + (preDeepCloneWeeklyLog == deepCloneWeeklyLog));//false
        System.out.println("深克隆附件：" + (preDeepCloneWeeklyLog.getAttachment() == deepCloneWeeklyLog.getAttachment()));//false

    }
}
