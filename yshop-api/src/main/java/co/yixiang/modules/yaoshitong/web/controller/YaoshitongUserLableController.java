package co.yixiang.modules.yaoshitong.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.modules.yaoshitong.service.YaoshitongUserLableService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

import java.util.List;

/**
 * <p>
 * 药师通用户标签 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Slf4j
@RestController
@RequestMapping("/yaoshitongUserLable")
@Api("药师通用户标签 API")
public class YaoshitongUserLableController extends BaseController {

    @Autowired
    private YaoshitongUserLableService yaoshitongUserLableService;

    /**
    * 添加药师通用户标签
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YaoshitongUserLable对象",notes = "添加药师通用户标签",response = ApiResult.class)
    public ApiResult<Boolean> addYaoshitongUserLable(@Valid @RequestBody YaoshitongUserLable yaoshitongUserLable) throws Exception{
        boolean flag = yaoshitongUserLableService.save(yaoshitongUserLable);
        return ApiResult.result(flag);
    }

    /**
    * 修改药师通用户标签
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YaoshitongUserLable对象",notes = "修改药师通用户标签",response = ApiResult.class)
    public ApiResult<Boolean> updateYaoshitongUserLable(@Valid @RequestBody YaoshitongUserLable yaoshitongUserLable) throws Exception{
        boolean flag = true;
        yaoshitongUserLable.setUid(SecurityUtils.getUserId().intValue());
        yaoshitongUserLableService.saveUserLable(yaoshitongUserLable);
       // boolean flag = yaoshitongUserLableService.updateById(yaoshitongUserLable);
        return ApiResult.result(flag);
    }

    /**
    * 删除药师通用户标签
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YaoshitongUserLable对象",notes = "删除药师通用户标签",response = ApiResult.class)
    public ApiResult<Boolean> deleteYaoshitongUserLable(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yaoshitongUserLableService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取药师通用户标签
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YaoshitongUserLable对象详情",notes = "查看药师通用户标签",response = YaoshitongUserLableQueryVo.class)
    public ApiResult<YaoshitongUserLableQueryVo> getYaoshitongUserLable(@Valid @RequestBody IdParam idParam) throws Exception{
        YaoshitongUserLableQueryVo yaoshitongUserLableQueryVo = yaoshitongUserLableService.getYaoshitongUserLableById(idParam.getId());
        return ApiResult.ok(yaoshitongUserLableQueryVo);
    }

    /**
     * 药师通用户标签分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YaoshitongUserLable分页列表",notes = "药师通用户标签分页列表",response = YaoshitongUserLableQueryVo.class)
    public ApiResult<Paging<YaoshitongUserLableQueryVo>> getYaoshitongUserLablePageList(@Valid @RequestBody(required = false) YaoshitongUserLableQueryParam yaoshitongUserLableQueryParam) throws Exception{

        Integer uid = SecurityUtils.getUserId().intValue();
        yaoshitongUserLableQueryParam.setUid(uid);
        Paging<YaoshitongUserLable> paging = yaoshitongUserLableService.getYaoshitongUserLablePageList(yaoshitongUserLableQueryParam);
        return ApiResult.ok(paging);
    }


    @GetMapping("/defaultPatientLable")
    @ApiOperation("查询药师通-患者主数据详情")
    public ApiResult<List<YaoshitongUserLable>> getdefaultPatientLable(){
        Integer uid = SecurityUtils.getUserId().intValue();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_default",1);
        queryWrapper.eq("uid",uid);

        List<YaoshitongUserLable> lableList  = yaoshitongUserLableService.list(queryWrapper);

        return ApiResult.ok(lableList);
    }
}

