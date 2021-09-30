package co.yixiang.modules.yaoshitong.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.param.IdParam;
import co.yixiang.constant.ShopConstants;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.modules.user.service.YxUserAddressService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.entity.YaoshitongRepurchaseReminder;
import co.yixiang.modules.yaoshitong.service.ChatMsgService;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseReminderService;
import co.yixiang.modules.yaoshitong.web.vo.ChatMsg;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "药师通模块", tags = "药师通模块")
@RequestMapping("/yaoshitong")
@Slf4j
public class YaoshitongController {
    @Autowired
    private  YxSystemGroupDataService systemGroupDataService;

    @Autowired
    private YxSystemConfigService systemConfigService;
    @Autowired
    private MdPharmacistServiceService pharmacistService;
    @Value("${file.path}")
    private String path;

    @Value(("${yiyao.url}"))
    private String yiyao_url;

    @Autowired
    private YxUserService userService;

    @Autowired
    private ChatMsgService chatMsgService;


    @Autowired
    private YxStoreCartService storeCartService;

    @Autowired
    private YaoshitongRepurchaseReminderService yaoshitongRepurchaseReminderService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private YxUserAddressService yxUserAddressService;

    @GetMapping("/live/banner")
    @ApiOperation(value = "直播教程轮播图",notes = "直播教程轮播图")
    public ApiResult<List<Map<String,Object>>> banner(){
        //banner
        List<Map<String,Object>> data = systemGroupDataService.getDatas(ShopConstants.YSHOP_HOME_BANNER_YAOSHITONG);

        return ApiResult.ok(data);
    }



    @GetMapping("/pharmacist/info")
    @ApiOperation("查询药师通-药师信息，二维码信息")
    public ApiResult<Object> getYaoshitongPatientDetail(){

        String apiUrl = systemConfigService.getData("api_url");
        if(StrUtil.isEmpty(apiUrl)){
            return ApiResult.fail("未配置api地址");
        }
        Integer uid = SecurityUtils.getUserId().intValue();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);

        MdPharmacistService pharmacist = pharmacistService.getOne(queryWrapper,true);
        if(pharmacist == null) {
            return ApiResult.fail("请绑定手机号");
        }
        // 更新年龄
        if(pharmacist!=null && StrUtil.isNotBlank(pharmacist.getBirth())) {
            pharmacist.setAge(DateUtil.ageOfNow(pharmacist.getBirth() + "01"));
        }
        String fileDir = path+"qrcode"+ File.separator;
        String name = "yaoshitong"+"_"+ pharmacist.getId() +".jpg";
        String url = yiyao_url + "/patientsInfo/" + pharmacist.getId();
        log.info("filePath============{}",fileDir+name);
        QrCodeUtil.generate(url, 180, 180,
                FileUtil.file(fileDir+name));


        String qrcodeUrl = apiUrl + "/api/file/qrcode/"+name;
        pharmacist.setQrcode(qrcodeUrl);
        return ApiResult.ok(pharmacist);
    }


    @PostMapping("/generateOrder")
    @ApiOperation("药师通-根据上次的订单生成新订单")
    public ApiResult<Object> generateOrder(@Valid @RequestBody IdParam idParam){
        Integer uid = SecurityUtils.getUserId().intValue();



        String departmentCode = "";
        String partnerCode = "";
        String refereeCode = "";
        String projectCode = "";
        int isNew = 0;
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;

        YaoshitongRepurchaseReminder yaoshitongRepurchaseReminder = yaoshitongRepurchaseReminderService.getById( Integer.valueOf(idParam.getId()));

        Integer productId =  yaoshitongRepurchaseReminder.getMedId();
        Integer cartNum = yaoshitongRepurchaseReminder.getLastPurchasseQty();
        Integer drugstoreid = yaoshitongRepurchaseReminder.getDrugstoreId();
        String provinceName = yaoshitongRepurchaseReminder.getProvinceName();
        String cityName = yaoshitongRepurchaseReminder.getCityName();
        String districtName = yaoshitongRepurchaseReminder.getDistrictName();
        String address = yaoshitongRepurchaseReminder.getAddress();
        String receiver = yaoshitongRepurchaseReminder.getReceiver();
        String receiverMobile = yaoshitongRepurchaseReminder.getReceiverMobile();



        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("product_id",productId);
        queryWrapper.eq("store_id",drugstoreid);
        YxStoreProductAttrValue attrValue =  yxStoreProductAttrValueService.getOne(queryWrapper,false);

        if(attrValue == null) {
            return  ApiResult.fail("线上药房不出售此药品");
        } else {
            //删除已有的未成订单的购物车明细
            storeCartService.deleteCartByUidProductid(uid,productId,attrValue.getUnique());
            // 收货地址
            // 判断收货地址是否已经存在

            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_default",0);
            updateWrapper.eq("uid",uid);
            yxUserAddressService.update(updateWrapper);

            if(StrUtil.isNotBlank(provinceName) && StrUtil.isNotBlank(cityName) && StrUtil.isNotBlank(address) ) {
                QueryWrapper<YxUserAddress> queryWrapper1 = new QueryWrapper();
                queryWrapper1.eq("province",provinceName);
                queryWrapper1.eq("city",cityName);
                queryWrapper1.eq("detail",address);
                queryWrapper1.eq("real_name",receiver);
                queryWrapper1.eq("phone",receiverMobile);
                queryWrapper1.eq("uid",uid);
                YxUserAddress yxUserAddress = yxUserAddressService.getOne(queryWrapper1,false);

                if(yxUserAddress != null) {



                    yxUserAddress.setIsDefault(1);
                    yxUserAddress.setIsDel(0);
                    yxUserAddressService.updateById(yxUserAddress);
                } else {


                    YxUserAddress addressNew = new YxUserAddress();
                    addressNew.setIsDefault(1);
                    addressNew.setIsDel(0);
                    addressNew.setCity(cityName);
                    addressNew.setProvince(provinceName);
                    addressNew.setDistrict(districtName);
                    addressNew.setDetail(address);
                    addressNew.setPhone(receiverMobile);
                    addressNew.setRealName(receiver);
                    addressNew.setUid(uid);
                    yxUserAddressService.save(addressNew);

                }


            }




            int cartId = storeCartService.addCart(uid,productId,cartNum,attrValue.getUnique()
                    ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode);
            return ApiResult.ok(cartId);
        }



    }


}
