package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.io.FileUtil;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DualImageUrlServiceImpl {
    @Autowired
    private YxStoreOrderService yxStoreOrderService;
    @Autowired
    private LocalStorageService localStorageService;
    @Value("${file.localUrl}")
    private String localUrl;
    public void dual(){
        LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.notLike(YxStoreOrder::getImagePath,"https");
        lambdaQueryWrapper.ne(YxStoreOrder::getImagePath,"");
        lambdaQueryWrapper.select(YxStoreOrder::getId, YxStoreOrder::getImagePath);
        List<YxStoreOrder> yxStoreOrderList = yxStoreOrderService.list(lambdaQueryWrapper);
        for(YxStoreOrder yxStoreOrder : yxStoreOrderList) {
            String recipel = yxStoreOrder.getImagePath();

          //  String fileName = UUID.randomUUID().toString().replace("-","");
            String fileName = FileUtil.extName(recipel);

            try {
                LocalStorageDto localStorageDTO = localStorageService.createByUrl(recipel,fileName);

                String imagePath = localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName();

                log.info("originalUrl={},currentUrl={}",recipel,imagePath);
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.set("image_path2",imagePath);
                updateWrapper.eq("id",yxStoreOrder.getId());
                yxStoreOrderService.update(updateWrapper);
            }catch (Exception e) {

            }

        }
    }
}
