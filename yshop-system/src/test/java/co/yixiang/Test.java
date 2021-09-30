package co.yixiang;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.meideyi.ProviderAES;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Test {
    public static void main(String[] args) {
        String plainText = String.valueOf(System.currentTimeMillis());
        String sign = ProviderAES.encrypt(plainText,ProviderAES.SEED);
        log.info("plainText={}",plainText);
        log.info("sign={}",sign);

        String status1 = "43";
        String status2 = "45";
        log.info("{}", status1.compareTo(status2));



        log.info("{}",System.currentTimeMillis());

    }
}
