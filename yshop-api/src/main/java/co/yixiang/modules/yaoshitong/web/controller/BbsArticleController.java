package co.yixiang.modules.yaoshitong.web.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.yaoshitong.entity.BbsArticle;
import co.yixiang.modules.yaoshitong.entity.BbsReply;
import co.yixiang.modules.yaoshitong.service.BbsArticleService;
import co.yixiang.modules.yaoshitong.web.param.BbsArticleQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsArticleQueryVo;
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
 * bbs文章列表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Slf4j
@RestController
@RequestMapping("/bbsArticle")
@Api("bbs文章列表 API")
public class BbsArticleController extends BaseController {

    @Autowired
    private BbsArticleService bbsArticleService;

    /**
    * 添加bbs文章列表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加BbsArticle对象",notes = "添加bbs文章列表",response = ApiResult.class)
    public ApiResult<Boolean> addBbsArticle(@Valid @RequestBody BbsArticle bbsArticle) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        bbsArticle.setAuthorId(uid);
        bbsArticle.setCreateAt(DateUtil.date());
        boolean flag = bbsArticleService.save(bbsArticle);
        return ApiResult.result(flag);
    }

    /**
    * 修改bbs文章列表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改BbsArticle对象",notes = "修改bbs文章列表",response = ApiResult.class)
    public ApiResult<Boolean> updateBbsArticle(@Valid @RequestBody BbsArticle bbsArticle) throws Exception{
        boolean flag = bbsArticleService.updateById(bbsArticle);
        return ApiResult.result(flag);
    }

    /**
    * 删除bbs文章列表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除BbsArticle对象",notes = "删除bbs文章列表",response = ApiResult.class)
    public ApiResult<Boolean> deleteBbsArticle(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = bbsArticleService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取bbs文章列表
    */
    @PostMapping("/info")
    @AnonymousAccess
    @ApiOperation(value = "获取BbsArticle对象详情",notes = "查看bbs文章列表",response = BbsArticleQueryVo.class)
    public ApiResult<BbsArticle> getBbsArticle(@Valid @RequestBody IdParam idParam) throws Exception{

        // 更新阅读数
        bbsArticleService.updateVisitCount(idParam.getId());

        BbsArticle BbsArticle = bbsArticleService.getBbsArticleById(idParam.getId());
        return ApiResult.ok(BbsArticle);
    }

    /**
     * bbs文章列表分页列表
     */
    @AnonymousAccess
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取BbsArticle分页列表",notes = "bbs文章列表分页列表",response = BbsArticleQueryVo.class)
    public ApiResult<Paging<BbsArticle>> getBbsArticlePageList(@Valid @RequestBody(required = false) BbsArticleQueryParam bbsArticleQueryParam) throws Exception{
        Paging<BbsArticle> paging = bbsArticleService.getBbsArticlePageList(bbsArticleQueryParam);
        return ApiResult.ok(paging);
    }

    /**
     * 点赞
     */
    @PostMapping("/up")
    @ApiOperation(value = "点赞帖子",notes = "点赞帖子",response = ApiResult.class)
    public ApiResult<Boolean> upArticle(@Valid @RequestBody BbsArticle bbsArticle) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        Integer count = bbsArticleService.upArticle(bbsArticle.getId(),uid);
        return ApiResult.ok(count);
    }

    /**
     * 收藏
     */
    @PostMapping("/collect")
    @ApiOperation(value = "收藏帖子",notes = "收藏帖子",response = ApiResult.class)
    public ApiResult<Boolean> collectArticle(@Valid @RequestBody BbsArticle bbsArticle) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        Integer count = bbsArticleService.collectArticle(bbsArticle.getId(),uid);
        return ApiResult.ok(count);
    }
}

