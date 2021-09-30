package co.yixiang.modules.yiyaobao.web.controller;

import co.yixiang.modules.yiyaobao.entity.ProductStoreMapping;
import co.yixiang.modules.yiyaobao.service.ProductStoreMappingService;
import co.yixiang.modules.yiyaobao.web.param.ProductStoreMappingQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.ProductStoreMappingQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

/**
 * <p>
 * 商品-药店-价格配置 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-05-18
 */
@Slf4j
@RestController
@RequestMapping("/productStoreMapping")
@Api("商品-药店-价格配置 API")
public class ProductStoreMappingController extends BaseController {

    @Autowired
    private ProductStoreMappingService productStoreMappingService;

    /**
    * 添加商品-药店-价格配置
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加ProductStoreMapping对象",notes = "添加商品-药店-价格配置",response = ApiResult.class)
    public ApiResult<Boolean> addProductStoreMapping(@Valid @RequestBody ProductStoreMapping productStoreMapping) throws Exception{
        boolean flag = productStoreMappingService.save(productStoreMapping);
        return ApiResult.result(flag);
    }

    /**
    * 修改商品-药店-价格配置
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改ProductStoreMapping对象",notes = "修改商品-药店-价格配置",response = ApiResult.class)
    public ApiResult<Boolean> updateProductStoreMapping(@Valid @RequestBody ProductStoreMapping productStoreMapping) throws Exception{
        boolean flag = productStoreMappingService.updateById(productStoreMapping);
        return ApiResult.result(flag);
    }

    /**
    * 删除商品-药店-价格配置
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除ProductStoreMapping对象",notes = "删除商品-药店-价格配置",response = ApiResult.class)
    public ApiResult<Boolean> deleteProductStoreMapping(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = productStoreMappingService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取商品-药店-价格配置
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取ProductStoreMapping对象详情",notes = "查看商品-药店-价格配置",response = ProductStoreMappingQueryVo.class)
    public ApiResult<ProductStoreMappingQueryVo> getProductStoreMapping(@Valid @RequestBody IdParam idParam) throws Exception{
        ProductStoreMappingQueryVo productStoreMappingQueryVo = productStoreMappingService.getProductStoreMappingById(idParam.getId());
        return ApiResult.ok(productStoreMappingQueryVo);
    }

    /**
     * 商品-药店-价格配置分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取ProductStoreMapping分页列表",notes = "商品-药店-价格配置分页列表",response = ProductStoreMappingQueryVo.class)
    public ApiResult<Paging<ProductStoreMappingQueryVo>> getProductStoreMappingPageList(@Valid @RequestBody(required = false) ProductStoreMappingQueryParam productStoreMappingQueryParam) throws Exception{
        Paging<ProductStoreMappingQueryVo> paging = productStoreMappingService.getProductStoreMappingPageList(productStoreMappingQueryParam);
        return ApiResult.ok(paging);
    }

}

