package co.yixiang.modules.meideyi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.entity.YxStoreDisease;
import co.yixiang.modules.shop.entity.YxStoreProduct;
import co.yixiang.modules.shop.mapper.YxStoreCartMapper;
import co.yixiang.modules.shop.mapper.YxStoreDiseaseMapper;
import co.yixiang.modules.shop.mapper.YxStoreProductMapper;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.rabbitmq.send.MqProducer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/6 14:01
 */
@RestController
@RequestMapping("/yiyaomall")
@Slf4j
public class MeideyiController {

    @Autowired
    private YxStoreProductMapper yxStoreProductMapper;

    @Autowired
    private YxStoreDiseaseMapper yxStoreDiseaseMapper;

    @Autowired
    private MqProducer mqProducer;

    @PostMapping(value = "/goods/queryDrugs")
    @Log(value = "美德医查询药品列表")
    @AnonymousAccess
    public Result ListGoods(@RequestBody RequestParam requestBody){

        Result result = new Result();

        String keyword = requestBody.getKeyword();

        if(StrUtil.isBlank(keyword)) {
            result.setMsg("搜索关键词不能为空");
            result.setStatus(500);
            return result;
        }

        Page page = new Page();
        // 设置当前页码
        page.setCurrent(requestBody.getPage());
        // 设置页大小
        page.setSize(requestBody.getLimit());


        if( 20 == requestBody.getSortType())  {  // 价格升序
            page.setOrders(Arrays.asList(OrderItem.asc("price")));
        } else if (21 == requestBody.getSortType()) {  // 价格降序
            page.setOrders(Arrays.asList(OrderItem.desc("price")));
        }

        List<String> ids = new ArrayList<>();


        LambdaQueryWrapper<YxStoreDisease> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(YxStoreDisease::getProjectCode,ProjectNameEnum.TAIPING_LEXIANG.getValue());
        lambdaQueryWrapper.eq(YxStoreDisease::getIsDel,0);
        lambdaQueryWrapper.like(YxStoreDisease::getCateName,keyword);
        List<YxStoreDisease> yxStoreDiseaseList = yxStoreDiseaseMapper.selectList(lambdaQueryWrapper);

        for(YxStoreDisease disease:yxStoreDiseaseList) {
            ids.add(String.valueOf(disease.getId()));
        }



        IPage<Goods> iPage = yxStoreProductMapper.getProduct4ProjectPageList(page, ProjectNameEnum.MEIDEYI.getValue(),requestBody.getKeyword(),ids);

        result.setMsg("");
        result.setStatus(200);
        SubResult subResult = new SubResult();
        subResult.setTotal( new Long(iPage.getTotal()).intValue());
        subResult.setRoot(iPage.getRecords());
        result.setResult(subResult);



        return result;



    }
}
