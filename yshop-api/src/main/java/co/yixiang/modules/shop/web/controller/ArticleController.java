/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.web.controller;

import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.shop.entity.YxArticle;
import co.yixiang.modules.shop.entity.YxUserAppointment;
import co.yixiang.modules.shop.service.ArticleService;
import co.yixiang.modules.shop.web.param.YxArticleQueryParam;
import co.yixiang.modules.shop.web.vo.YxArticleQueryVo;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>
 * 文章 前端控制器
 * </p>
 *
 * @author hupeng
 * @since 2019-10-02
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/article")
@Api(value = "文章模块", tags = "商城:文章模块", description = "文章模块")
public class ArticleController extends BaseController {

    private final ArticleService articleService;

    @Autowired
    private DictDetailService dictDetailService;

    /**
    * 获取文章文章详情
    */
    @AnonymousAccess
    @GetMapping("/details/{id}")
    @ApiOperation(value = "文章详情",notes = "文章详情",response = YxArticleQueryVo.class)
    public ApiResult<YxArticle> getYxArticle(@PathVariable Integer id,@RequestParam(value = "",required = false) String spread) throws Exception{
       // int uid = SecurityUtils.getUserId().intValue();
       // log.info("文章详情分享人spread= {}，文章详情查看人uid= {}",spread,uid);

        YxArticle yxArticle = articleService.getById(id);
        articleService.incVisitNum(id);
        return ApiResult.ok(yxArticle);
    }

    /**
     * 文章列表
     */
    @AnonymousAccess
    @GetMapping("/list")
    @ApiOperation(value = "文章列表",notes = "文章列表",response = YxArticleQueryVo.class)
    public ApiResult<IPage<YxArticle>> getYxArticlePageList(YxArticleQueryParam queryParam){

        QueryWrapper<YxArticle> queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("add_time");
        if(queryParam.getType() != null) {
            queryWrapper.eq("type",queryParam.getType());
        }

        if(StrUtil.isNotBlank(queryParam.getCid())) {
            queryWrapper.apply(" FIND_IN_SET({0},cid)",queryParam.getCid());
        }

        if(queryParam.getIsHot() != null) {
            queryWrapper.eq("is_hot",queryParam.getIsHot());
        }

        if(queryParam.getStoreId() != null) {
            queryWrapper.eq("store_id",queryParam.getStoreId());
        }

        if(StrUtil.isNotBlank(queryParam.getKeyword())) {
            queryWrapper.apply(" ( title like concat('%',{0},'%') or synopsis like concat('%',{1},'%') )",queryParam.getKeyword(),queryParam.getKeyword());
           // queryWrapper.like("title",queryParam.getKeyword());
        }
        if("null".equals(queryParam.getProjectCode())) {
            queryParam.setProjectCode("");
        }
        queryWrapper.eq("project_code", StrUtil.emptyIfNull(queryParam.getProjectCode()));

        Page<YxArticle> pageModel = new Page<>(queryParam.getPage(), queryParam.getLimit());
        IPage<YxArticle> pageList =  articleService.page(pageModel,queryWrapper);
        for(YxArticle article:pageList.getRecords()) {
            if(StrUtil.isNotBlank(article.getCid() )) {

                article.setLables(dictDetailService.findDetails( Arrays.asList(article.getCid().split(",")),"articleType"));

            }
        }
        return ApiResult.ok(pageList);
    }


    @AnonymousAccess
    @GetMapping("/labelList")
    @ApiOperation(value = "文章列表标签",notes = "文章列表标签",response = YxArticleQueryVo.class)
    public ApiResult<Object> getlabelList(YxArticleQueryParam queryParam){

        DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
        dictDetailQueryParam.setName("articleType");
        return  ApiResult.ok(dictDetailService.getDictDetailList(dictDetailQueryParam));

    }
}

