package co.yixiang.modules.zhongan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.entity.YxStoreOrderCartInfo;
import co.yixiang.modules.order.mapper.YxStoreOrderCartInfoMapper;
import co.yixiang.modules.order.mapper.YxStoreOrderMapper;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.taiping.util.EncryptionToolUtilAes;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.impl.YxUserServiceImpl;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ZhongAnMaBingServiceImpl {

    @Value("${zhonganpuyao.cipherKey}")
    private String cipherKey;

    @Autowired
    private YxUserService yxUserService;

    // @Async
    public ZhongAnParamDto analysisParam(ZhongAnParamDto zhongAnParamDto){
        log.info("众安慢病跳转参数解析：{}", JSONUtil.parseObj(zhongAnParamDto));

        // 众安用户标识码
        String cardNumber_encrypt = zhongAnParamDto.getCardNumber();
        String cardNumber = "";
        if(StrUtil.isNotBlank(cardNumber_encrypt)) {
            try {
                cardNumber = EncryptionToolUtilAes.decrypt(cardNumber_encrypt, cipherKey);
            }catch (Exception e) {
                throw new BadRequestException("参数解析出错");
            }
        }else{
            throw new BadRequestException("用户标识码不能为空");
        }

        // 时间戳
        String orderNumber_encrypt = zhongAnParamDto.getOrderNumber();
        String orderNumber = "";
        if(StrUtil.isNotBlank(orderNumber_encrypt)) {
            try{
                orderNumber = EncryptionToolUtilAes.decrypt(orderNumber_encrypt, cipherKey);
            } catch (Exception e) {
                throw new BadRequestException("参数解析出错");
            }

            long systemTime = System.currentTimeMillis();
            long requestTime = Long.valueOf(orderNumber).longValue();
            long gap = systemTime - requestTime;
            if(gap >= 60000) {
          //      throw new BadRequestException("跳转链接已过期，请重新获取");
            }

        }


        // 手机号
        String cardType_encrypt = zhongAnParamDto.getCardType();
        String cardType = "";
        if(StrUtil.isNotBlank(cardType_encrypt)) {
            try{
                cardType = EncryptionToolUtilAes.decrypt(cardType_encrypt, cipherKey);

            } catch (Exception e) {
                throw new BadRequestException("参数解析出错");
            }

            if(! StringUtils.isPhone(cardType)){
                throw new BadRequestException("手机号码格式不正确");
            }
        }



        zhongAnParamDto.setCardType(cardType);
        zhongAnParamDto.setCardNumber(cardNumber);

        zhongAnParamDto.setOrderNumber(orderNumber);

        log.info("众安慢病跳转参数解析结果：{}", JSONUtil.parseObj(zhongAnParamDto));

        return zhongAnParamDto;
    };


    public static void main(String[] args) {

        String orderNumber =  String.valueOf(System.currentTimeMillis());
        String cardType=EncryptionToolUtilAes.encrypt(orderNumber, "ZAMB@xsyffwk2021");
//        String orderNumber = String.valueOf(System.currentTimeMillis());
        System.out.println(cardType);
        // Znzqz5mlYLS8qKJIxHbzIA==
//

//        https://mtest.yiyaogo.com/#/?projectCode=zhonganmanbing&pageNum=3&cardType=d4bN+OfKgr0LuVGxVmhQ7g==&cardNumber=Znzqz5mlYLS8qKJIxHbzIA==&orderNumber=ZUebbMqhjOabUH8SRXbwRg==
    }
}
