package com.example.springchaindemo.chain.sms;

import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: spring-chain-demo
 * @className: SendLimitFilter
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 15:55
 **/
@Component
@Order(4)
public class SendLimitFilter implements Filter {

    @Autowired
    private Environment environment;
    // 模拟短信发送次数缓存
    private Map<String,Integer> smsSendMap = new ConcurrentHashMap<>();

    @Override
    public boolean filter(SmsDTO smsDTO) {
        String num = environment.getProperty("send.frequency.limit");
        if(StringUtils.isEmpty(num)){//没有限制条件
            return true;
        }
        Integer mobileNum = smsSendMap.get(smsDTO.getMobile());

        if(mobileNum != null && Integer.parseInt(num) <= mobileNum){
            System.out.println("手机号已被限制");
            return false;
        }
        if(mobileNum == null){
            smsSendMap.put(smsDTO.getMobile(),new Integer(1));
        }else{
            smsSendMap.put(smsDTO.getMobile(),new Integer(mobileNum) + 1);
        }
        return true;
    }
}
