package co.yixiang.modules.yaoshitong.web.controller;

import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLableRelation;
import co.yixiang.modules.yaoshitong.service.YaoshitongUserLableRelationService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableRelationQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableRelationQueryVo;
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
 * 患者对应的标签库 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Slf4j
@RestController
@RequestMapping("/yaoshitongUserLableRelation")
@Api("患者对应的标签库 API")
public class YaoshitongUserLableRelationController extends BaseController {

    @Autowired
    private YaoshitongUserLableRelationService yaoshitongUserLableRelationService;

    /**
    * 添加患者对应的标签库
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YaoshitongUserLableRelation对象",notes = "添加患者对应的标签库",response = ApiResult.class)
    public ApiResult<Boolean> addYaoshitongUserLableRelation(@Valid @RequestBody YaoshitongUserLableRelation yaoshitongUserLableRelation) throws Exception{
        boolean flag = yaoshitongUserLableRelationService.save(yaoshitongUserLableRelation);
        return ApiResult.result(flag);
    }

    /**
    * 修改患者对应的标签库
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YaoshitongUserLableRelation对象",notes = "修改患者对应的标签库",response = ApiResult.class)
    public ApiResult<Boolean> updateYaoshitongUserLableRelation(@Valid @RequestBody YaoshitongUserLableRelation yaoshitongUserLableRelation) throws Exception{
        boolean flag = yaoshitongUserLableRelationService.updateById(yaoshitongUserLableRelation);
        return ApiResult.result(flag);
    }

    /**
    * 删除患者对应的标签库
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YaoshitongUserLableRelation对象",notes = "删除患者对应的标签库",response = ApiResult.class)
    public ApiResult<Boolean> deleteYaoshitongUserLableRelation(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yaoshitongUserLableRelationService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取患者对应的标签库
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YaoshitongUserLableRelation对象详情",notes = "查看患者对应的标签库",response = YaoshitongUserLableRelationQueryVo.class)
    public ApiResult<YaoshitongUserLableRelationQueryVo> getYaoshitongUserLableRelation(@Valid @RequestBody IdParam idParam) throws Exception{
        YaoshitongUserLableRelationQueryVo yaoshitongUserLableRelationQueryVo = yaoshitongUserLableRelationService.getYaoshitongUserLableRelationById(idParam.getId());
        return ApiResult.ok(yaoshitongUserLableRelationQueryVo);
    }

    /**
     * 患者对应的标签库分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YaoshitongUserLableRelation分页列表",notes = "患者对应的标签库分页列表",response = YaoshitongUserLableRelationQueryVo.class)
    public ApiResult<Paging<YaoshitongUserLableRelationQueryVo>> getYaoshitongUserLableRelationPageList(@Valid @RequestBody(required = false) YaoshitongUserLableRelationQueryParam yaoshitongUserLableRelationQueryParam) throws Exception{
        Paging<YaoshitongUserLableRelationQueryVo> paging = yaoshitongUserLableRelationService.getYaoshitongUserLableRelationPageList(yaoshitongUserLableRelationQueryParam);
        return ApiResult.ok(paging);
    }

}

