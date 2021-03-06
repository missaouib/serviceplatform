package co.yixiang.modules.monitor.config;

import co.yixiang.modules.monitor.service.VisitsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化站点统计
 * @author Zheng Jie
 */
@Component
@Slf4j
public class VisitsInitialization implements ApplicationRunner {

    private final VisitsService visitsService;

    public VisitsInitialization(VisitsService visitsService) {
        this.visitsService = visitsService;
    }

    @Override
    public void run(ApplicationArguments args){
        log.info("--------------- 初始化站点统计，如果存在今日统计则跳过 ---------------");
        visitsService.save();
        log.info("--------------- 初始化站点统计完成 ---------------");
    }
}