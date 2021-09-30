package co.yixiang.modules.yaoshitong.web.controller;

import cn.hutool.core.date.DateUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.yaoshitong.entity.BbsReply;
import co.yixiang.modules.yaoshitong.service.BbsReplyService;
import co.yixiang.modules.yaoshitong.web.param.BbsReplyQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsReplyQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
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
 * 帖子回复表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Slf4j
@RestController
@RequestMapping("/bbsReply")
@Api("帖子回复表 API")
public class BbsReplyController extends BaseController {

    @Autowired
    private BbsReplyService bbsReplyService;

    /**
    * 添加帖子回复表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加BbsReply对象",notes = "添加帖子回复表",response = ApiResult.class)
    public ApiResult<Boolean> addBbsReply(@Valid @RequestBody BbsReply bbsReply) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        bbsReply.setAuthorId(uid);
        bbsReply.setCreateAt(DateUtil.date());
        boolean flag = bbsReplyService.saveReply(bbsReply);



        return ApiResult.result(flag);
    }

    /**
    * 修改帖子回复表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改BbsReply对象",notes = "修改帖子回复表",response = ApiResult.class)
    public ApiResult<Boolean> updateBbsReply(@Valid @RequestBody BbsReply bbsReply) throws Exception{
        boolean flag = bbsReplyService.updateById(bbsReply);
        return ApiResult.result(flag);
    }

    /**
    * 删除帖子回复表
    */
    @PostMapping("/delete")
    @AnonymousAccess
    @ApiOperation(value = "删除BbsReply对象",notes = "删除帖子回复表",response = ApiResult.class)
    public ApiResult<Boolean> deleteBbsReply(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = bbsReplyService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取帖子回复表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取BbsReply对象详情",notes = "查看帖子回复表",response = BbsReplyQueryVo.class)
    public ApiResult<BbsReplyQueryVo> getBbsReply(@Valid @RequestBody IdParam idParam) throws Exception{
        BbsReplyQueryVo bbsReplyQueryVo = bbsReplyService.getBbsReplyById(idParam.getId());
        return ApiResult.ok(bbsReplyQueryVo);
    }

    /**
     * 帖子回复表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取BbsReply分页列表",notes = "帖子回复表分页列表",response = BbsReplyQueryVo.class)
    public ApiResult<Paging<BbsReply>> getBbsReplyPageList(@Valid @RequestBody(required = false) BbsReplyQueryParam bbsReplyQueryParam) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        Paging<BbsReply> paging = bbsReplyService.getBbsReplyPageList(bbsReplyQueryParam,uid);
        return ApiResult.ok(paging);
    }


    /**
     * 点赞
     */
    @PostMapping("/up")
    @ApiOperation(value = "点赞回复",notes = "点赞回复",response = ApiResult.class)
    public ApiResult<Boolean> upReply(@Valid @RequestBody BbsReply bbsReply) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        Integer count = bbsReplyService.upReply(bbsReply.getId(),uid);
        return ApiResult.ok(count);
    }




}

