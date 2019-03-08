package com.hjs.prototype;

import lombok.Getter;
import lombok.Setter;

import java.io.*;

/**
 * @author hjs
 * @date 2019/3/72:38
 * @Dec
 */
@Getter
@Setter
public class DeepCloneWeeklyLog implements Serializable {
    private Attachment attachment;
    private String name;
    private String date;
    private String content;

    public DeepCloneWeeklyLog deepClone() throws Exception {
        //将对象写入流
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bao);
        oos.writeObject(this);

        //将对象从流中取出
        ByteArrayInputStream bai = new ByteArrayInputStream(bao.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bai);
        return (DeepCloneWeeklyLog) ois.readObject();
    }
}
