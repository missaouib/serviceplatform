package co.yixiang.modules.manage.web.controller;

import co.yixiang.modules.manage.entity.Partner;
import co.yixiang.modules.manage.service.PartnerService;
import co.yixiang.modules.manage.web.param.PartnerQueryParam;
import co.yixiang.modules.manage.web.vo.PartnerQueryVo;
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
 *  前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Slf4j
@RestController
@RequestMapping("/partner")
@Api(" API")
public class PartnerController extends BaseController {

    @Autowired
    private PartnerService partnerService;

    /**
    * 添加
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加Partner对象",notes = "添加",response = ApiResult.class)
    public ApiResult<Boolean> addPartner(@Valid @RequestBody Partner partner) throws Exception{
        boolean flag = partnerService.save(partner);
        return ApiResult.result(flag);
    }

    /**
    * 修改
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改Partner对象",notes = "修改",response = ApiResult.class)
    public ApiResult<Boolean> updatePartner(@Valid @RequestBody Partner partner) throws Exception{
        boolean flag = partnerService.updateById(partner);
        return ApiResult.result(flag);
    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除Partner对象",notes = "删除",response = ApiResult.class)
    public ApiResult<Boolean> deletePartner(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = partnerService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取Partner对象详情",notes = "查看",response = PartnerQueryVo.class)
    public ApiResult<PartnerQueryVo> getPartner(@Valid @RequestBody IdParam idParam) throws Exception{
        PartnerQueryVo partnerQueryVo = partnerService.getPartnerById(idParam.getId());
        return ApiResult.ok(partnerQueryVo);
    }

    /**
     * 分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取Partner分页列表",notes = "分页列表",response = PartnerQueryVo.class)
    public ApiResult<Paging<PartnerQueryVo>> getPartnerPageList(@Valid @RequestBody(required = false) PartnerQueryParam partnerQueryParam) throws Exception{
        Paging<PartnerQueryVo> paging = partnerService.getPartnerPageList(partnerQueryParam);
        return ApiResult.ok(paging);
    }

}

