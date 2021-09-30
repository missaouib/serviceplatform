package co.yixiang.modules.yiyaobao.web.controller;

import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.manage.web.param.YxStoreCartProjectQueryParam;
import co.yixiang.modules.shop.entity.ProjectSalesArea;
import co.yixiang.modules.shop.service.ProjectSalesAreaService;
import co.yixiang.modules.yiyaobao.entity.MdCountry;
import co.yixiang.modules.yiyaobao.service.MdCountryService;
import co.yixiang.modules.yiyaobao.web.param.MdCountryQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.MdCountryQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 国家地区信息表 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-05-16
 */
@Slf4j
@RestController
@RequestMapping("/mdCountry")
@Api("国家地区信息表 API")
public class MdCountryController extends BaseController {

    @Autowired
    private MdCountryService mdCountryService;

    @Autowired
    private ProjectSalesAreaService projectSalesAreaService;

    /**
    * 添加国家地区信息表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加MdCountry对象",notes = "添加国家地区信息表",response = ApiResult.class)
    public ApiResult<Boolean> addMdCountry(@Valid @RequestBody MdCountry mdCountry) throws Exception{
        boolean flag = mdCountryService.save(mdCountry);
        return ApiResult.result(flag);
    }

    /**
    * 修改国家地区信息表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改MdCountry对象",notes = "修改国家地区信息表",response = ApiResult.class)
    public ApiResult<Boolean> updateMdCountry(@Valid @RequestBody MdCountry mdCountry) throws Exception{
        boolean flag = mdCountryService.updateById(mdCountry);
        return ApiResult.result(flag);
    }

    /**
    * 删除国家地区信息表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除MdCountry对象",notes = "删除国家地区信息表",response = ApiResult.class)
    public ApiResult<Boolean> deleteMdCountry(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = mdCountryService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取国家地区信息表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取MdCountry对象详情",notes = "查看国家地区信息表",response = MdCountryQueryVo.class)
    public ApiResult<MdCountryQueryVo> getMdCountry(@Valid @RequestBody IdParam idParam) throws Exception{
        MdCountryQueryVo mdCountryQueryVo = mdCountryService.getMdCountryById(idParam.getId());
        return ApiResult.ok(mdCountryQueryVo);
    }

    /**
     * 国家地区信息表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取MdCountry分页列表",notes = "国家地区信息表分页列表",response = MdCountryQueryVo.class)
    public ApiResult<Paging<MdCountryQueryVo>> getMdCountryPageList(@Valid @RequestBody(required = false) MdCountryQueryParam mdCountryQueryParam) throws Exception{
        Paging<MdCountryQueryVo> paging = mdCountryService.getMdCountryPageList(mdCountryQueryParam);
        return ApiResult.ok(paging);
    }




    @GetMapping("/children")
    @ApiOperation(value = "查询下属区域",notes = "查询下属区域")
    @AnonymousAccess
    public ApiResult<List<MdCountry>> queryChildend(MdCountryQueryParam mdCountryQueryParam){
        if (StrUtil.isBlank(mdCountryQueryParam.getParentId())) {
            mdCountryQueryParam.setParentId("0");
        }

        List<MdCountry> mdCountries = new ArrayList<>();
        if(mdCountryQueryParam.getType() != null && mdCountryQueryParam.getType() == 1) {
            MdCountry mdCountry = new MdCountry();
            mdCountry.setName("全国药房");
            mdCountries.add(mdCountry);
        }


        if(StrUtil.isNotBlank(mdCountryQueryParam.getProjectCode()) && "0".equals(mdCountryQueryParam.getParentId())) {  // 项目的销售省份
            QueryWrapper<MdCountry> queryWrapper = new QueryWrapper<MdCountry>();
            queryWrapper.eq("tree_id","1");
            queryWrapper.apply(" EXISTS (SELECT 1 FROM project_sales_area psa WHERE md_country.NAME = psa.area_name AND psa.project_code = {0})",mdCountryQueryParam.getProjectCode());
            List<MdCountry> mdCountryList =  mdCountryService.list(queryWrapper);
            mdCountries.addAll(mdCountryList);

        } else {
            List<MdCountry> mdCountryList =  mdCountryService.list(new QueryWrapper<MdCountry>().eq("PARENT_ID",mdCountryQueryParam.getParentId()));
            mdCountries.addAll(mdCountryList);
        }

        return ApiResult.ok(mdCountries);
    }
}

