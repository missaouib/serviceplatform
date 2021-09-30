package co.yixiang.modules.gjp.Lib.Controller;


import co.yixiang.modules.gjp.Lib.service.GjpServiceImpl;
import co.yixiang.modules.gjp.Lib.vo.GjpNotifyDTO;
import co.yixiang.modules.gjp.Lib.vo.GjpNotifyResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/gjp")
public class GjpNotifyController {

    @Autowired
    private GjpServiceImpl gjpService;
    /**
     * 接收管家婆平台发送的物流信息推送
     *
     * @param gjpNotifyDTO
     * @return
     */

    @PostMapping(value = "/notify")
    public GjpNotifyResult notify(@RequestBody GjpNotifyDTO gjpNotifyDTO) {

        log.info("gjpNotifyDTO={}",gjpNotifyDTO);
        gjpService.express(gjpNotifyDTO);

        GjpNotifyResult gjpNotifyResult = new GjpNotifyResult();

        gjpNotifyResult.setResult(true);
        gjpNotifyResult.setMessage("ok");


        return gjpNotifyResult;
    }
}
