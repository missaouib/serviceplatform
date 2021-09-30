package co.yixiang.modules.gjp.Lib.task;


import co.yixiang.modules.gjp.Lib.service.GjpServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Component
@Transactional
@Slf4j
public class GjpTask {
    @Autowired
    private GjpServiceImpl gjpService;


    public void uploadOrder() {
        log.info("upload order ========================");

        try {
            gjpService.DoUploadSaleOrders();
        }catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void uploadProduct(){
        log.info("upload product ========================");
        try {
            gjpService.DoUploadProduct();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
