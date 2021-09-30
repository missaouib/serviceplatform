package co.yixiang.modules.taiping.web.controller;

import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taiping.entity.TaipingCard;
import co.yixiang.modules.taiping.entity.TaipingParamDto;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.taiping.web.param.TaipingCardQueryParam;
import co.yixiang.modules.taiping.web.vo.TaipingCardQueryVo;
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
 * 太平乐享虚拟卡 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-11-19
 */
@Slf4j
@RestController
@RequestMapping("/taipingCard")
@Api("太平乐享虚拟卡 API")
public class TaipingCardController extends BaseController {

    @Autowired
    private TaipingCardService taipingCardService;

    /**
    * 添加太平乐享虚拟卡
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加TaipingCard对象",notes = "添加太平乐享虚拟卡",response = ApiResult.class)
    public ApiResult<Boolean> addTaipingCard(@Valid @RequestBody TaipingCard taipingCard) throws Exception{
        boolean flag = taipingCardService.save(taipingCard);
        return ApiResult.result(flag);
    }

    /**
    * 修改太平乐享虚拟卡
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改TaipingCard对象",notes = "修改太平乐享虚拟卡",response = ApiResult.class)
    public ApiResult<Boolean> updateTaipingCard(@Valid @RequestBody TaipingCard taipingCard) throws Exception{
        boolean flag = taipingCardService.updateById(taipingCard);
        return ApiResult.result(flag);
    }

    /**
    * 删除太平乐享虚拟卡
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除TaipingCard对象",notes = "删除太平乐享虚拟卡",response = ApiResult.class)
    public ApiResult<Boolean> deleteTaipingCard(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = taipingCardService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取太平乐享虚拟卡
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取TaipingCard对象详情",notes = "查看太平乐享虚拟卡",response = TaipingCardQueryVo.class)
    public ApiResult<TaipingCardQueryVo> getTaipingCard(@Valid @RequestBody IdParam idParam) throws Exception{
        TaipingCardQueryVo taipingCardQueryVo = taipingCardService.getTaipingCardById(idParam.getId());
        return ApiResult.ok(taipingCardQueryVo);
    }

    /**
     * 太平乐享虚拟卡分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取TaipingCard分页列表",notes = "太平乐享虚拟卡分页列表",response = TaipingCardQueryVo.class)
    public ApiResult<Paging<TaipingCardQueryVo>> getTaipingCardPageList(@Valid @RequestBody(required = false) TaipingCardQueryParam taipingCardQueryParam) throws Exception{
        Paging<TaipingCardQueryVo> paging = taipingCardService.getTaipingCardPageList(taipingCardQueryParam);
        return ApiResult.ok(paging);
    }


    /**
     * 太平乐享参数解析
     */
    @AnonymousAccess
    @Log(value = "太平乐享参数解析")
    @PostMapping("/analysis")
    @ApiOperation(value = "太平乐享参数解析",notes = "太平乐享参数解析",response = TaipingParamDto.class)
    public ApiResult<TaipingParamDto> analysis( @RequestBody(required = false) TaipingParamDto taipingParamDto) throws Exception{
        log.info("太平乐享参数解析:{}", JSONUtil.parseObj(taipingParamDto));
        TaipingParamDto param  = taipingCardService.analysisParam(taipingParamDto);
        log.info("太平乐享参数解析结果:{}",JSONUtil.parseObj(taipingParamDto));
        return ApiResult.ok(param);
    }
}

