package co.yixiang.modules.shop.web.controller;

import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.entity.EnterpriseTopics;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.shop.web.param.MdPharmacistServiceQueryParam;
import co.yixiang.modules.shop.web.vo.MdPharmacistServiceQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * 药师在线配置表 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-06-09
 */
@Slf4j
@RestController
@RequestMapping("/mdPharmacistService")
@Api("药师在线配置表 API")
public class MdPharmacistServiceController extends BaseController {

    @Autowired
    private MdPharmacistServiceService mdPharmacistServiceService;

    /**
    * 添加药师在线配置表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加MdPharmacistService对象",notes = "添加药师在线配置表",response = ApiResult.class)
    public ApiResult<Boolean> addMdPharmacistService(@Valid @RequestBody MdPharmacistService mdPharmacistService) throws Exception{
        boolean flag = mdPharmacistServiceService.save(mdPharmacistService);
        return ApiResult.result(flag);
    }

    /**
    * 修改药师在线配置表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改MdPharmacistService对象",notes = "修改药师在线配置表",response = ApiResult.class)
    public ApiResult<Boolean> updateMdPharmacistService(@Valid @RequestBody MdPharmacistService mdPharmacistService) throws Exception{
        boolean flag = mdPharmacistServiceService.updateById(mdPharmacistService);
        return ApiResult.result(flag);
    }

    /**
    * 删除药师在线配置表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除MdPharmacistService对象",notes = "删除药师在线配置表",response = ApiResult.class)
    public ApiResult<Boolean> deleteMdPharmacistService(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = mdPharmacistServiceService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取药师在线配置表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取MdPharmacistService对象详情",notes = "查看药师在线配置表",response = MdPharmacistServiceQueryVo.class)
    public ApiResult<MdPharmacistServiceQueryVo> getMdPharmacistService(@Valid @RequestBody IdParam idParam) throws Exception{
        MdPharmacistServiceQueryVo mdPharmacistServiceQueryVo = mdPharmacistServiceService.getMdPharmacistServiceById(idParam.getId());
        return ApiResult.ok(mdPharmacistServiceQueryVo);
    }

    /**
     * 药师在线配置表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取MdPharmacistService分页列表",notes = "药师在线配置表分页列表",response = MdPharmacistServiceQueryVo.class)
    public ApiResult<IPage<MdPharmacistService>> getMdPharmacistServicePageList(@Valid @RequestBody(required = false) MdPharmacistServiceQueryParam mdPharmacistServiceQueryParam) throws Exception{

        QueryWrapper<MdPharmacistService> queryWrapper = new QueryWrapper();

        if(StrUtil.isNotBlank(mdPharmacistServiceQueryParam.getForeignId())) {
            queryWrapper.eq("FOREIGN_ID",mdPharmacistServiceQueryParam.getForeignId() );
        }
        queryWrapper.isNotNull("uid");
        Page<MdPharmacistService> pageModel = new Page<>(mdPharmacistServiceQueryParam.getPage(), mdPharmacistServiceQueryParam.getLimit());
        IPage<MdPharmacistService> pageList =  mdPharmacistServiceService.page(pageModel,queryWrapper);

        return ApiResult.ok(pageList);
    }

}

