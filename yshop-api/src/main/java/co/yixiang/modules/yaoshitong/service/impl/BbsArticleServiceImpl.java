package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.BbsArticle;
import co.yixiang.modules.yaoshitong.entity.BbsReply;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.mapper.BbsArticleMapper;
import co.yixiang.modules.yaoshitong.mapping.BbsArticleVoMap;
import co.yixiang.modules.yaoshitong.service.BbsArticleService;
import co.yixiang.modules.yaoshitong.service.BbsReplyService;
import co.yixiang.modules.yaoshitong.web.param.BbsArticleQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsArticleQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.yaoshitong.web.vo.BbsAuthorVo;
import co.yixiang.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.*;


/**
 * <p>
 * bbs文章列表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BbsArticleServiceImpl extends BaseServiceImpl<BbsArticleMapper, BbsArticle> implements BbsArticleService {

    @Autowired
    private BbsArticleMapper bbsArticleMapper;

    @Autowired
    private YxUserService yxUserService;

    @Autowired
    private BbsReplyService replyService;

    @Override
    public BbsArticle getBbsArticleById(Serializable id) throws Exception{

        BbsArticle article = bbsArticleMapper.selectById(id);

        // 获取发帖人的信息 姓名和头像
        YxUserQueryVo user = yxUserService.getYxUserById(article.getAuthorId());
        BbsAuthorVo bbsAuthorVo = new BbsAuthorVo();
        if(user != null) {
            bbsAuthorVo.setAvatar_url(user.getAvatar());
            bbsAuthorVo.setLoginname(user.getNickname());
        }
        article.setAuthor(bbsAuthorVo);

        // 获取回复数
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("article_id",id);
        int replyCount =replyService.count(queryWrapper);
        article.setReplyCount(replyCount);

        // 点赞数
        article.setIsSelfUp(false);
        if(StrUtil.isNotBlank(article.getUpsStr())) {
            int count = article.getUpsStr().split(",").length;
            List<String> upsList = Arrays.asList(article.getUpsStr().split(","));
            article.setUps(upsList.size());

            if(upsList.contains(String.valueOf(SecurityUtils.getUserId()))) {
                article.setIsSelfUp(true);
            }

        }else{
            article.setUps(0);
        }

        // 收藏数
        article.setIsSelfCollect(false);
        if(StrUtil.isNotBlank(article.getCollectStr())) {
            int count = article.getCollectStr().split(",").length;
            List<String> collectList = Arrays.asList(article.getCollectStr().split(","));
            article.setCollects(collectList.size());

            if(collectList.contains(String.valueOf(SecurityUtils.getUserId()))) {
                article.setIsSelfCollect(true);
            }
        }else{
            article.setCollects(0);
        }


        return article;
    }

    @Override
    public Paging<BbsArticle> getBbsArticlePageList(BbsArticleQueryParam bbsArticleQueryParam) throws Exception{
        Page page = setPageParam(bbsArticleQueryParam,OrderItem.desc("create_at"));
        page.setOrders(OrderItem.descs("is_top","create_at"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(BbsArticleQueryParam.class, bbsArticleQueryParam);
        if(StrUtil.isNotBlank(bbsArticleQueryParam.getOption()) && "myArticle".equals(bbsArticleQueryParam.getOption())) {
            queryWrapper.eq("author_id", SecurityUtils.getUserId().intValue());
        }

        if(StrUtil.isNotBlank(bbsArticleQueryParam.getOption()) && "myCollectArticle".equals(bbsArticleQueryParam.getOption())) {
            queryWrapper.apply(" FIND_IN_SET({0},collect_str) ",String.valueOf(SecurityUtils.getUserId()));
        }

        if(StrUtil.isNotBlank(bbsArticleQueryParam.getOption()) && "myUpArticle".equals(bbsArticleQueryParam.getOption())) {
            queryWrapper.apply(" FIND_IN_SET({0},ups_str) ",String.valueOf(SecurityUtils.getUserId()));
        }

        IPage<BbsArticle> iPage = bbsArticleMapper.selectPage(page,queryWrapper);

        for(BbsArticle article:iPage.getRecords()) {

            // 获取发帖人姓名和 头像
            Integer authorId = article.getAuthorId();
            YxUserQueryVo user = yxUserService.getYxUserById(authorId);
            BbsAuthorVo bbsAuthorVo = new BbsAuthorVo();
            if(user != null) {
                bbsAuthorVo.setAvatar_url(user.getAvatar());
                bbsAuthorVo.setLoginname(user.getNickname());
            }
            article.setAuthor(bbsAuthorVo);
            // 获取回复数
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("article_id",article.getId());
            int replyCount =replyService.count(queryWrapper1);
            article.setReplyCount(replyCount);
        }


        return new Paging(iPage);
    }

    @Override
    public void updateVisitCount(String articleId) {
        bbsArticleMapper.updateVisitCount(articleId);
    }


    @Override
    public Integer upArticle(String id, Integer uid) {
        BbsArticle article = bbsArticleMapper.selectById(id);
        String upsStr = article.getUpsStr();
        Map<String,String> uperMap = new HashMap<>();
        if(StrUtil.isNotBlank(upsStr)) {
            //
            for(String uper: Arrays.asList(upsStr.split(","))) {
                uperMap.put(uper,uper);
            }
        }

        if(uperMap.containsKey(String.valueOf(uid))) {
            // 取消点赞
            uperMap.remove(String.valueOf(uid));

        }else{
            // 点赞
            uperMap.put(String.valueOf(uid),String.valueOf(uid));
        }

        upsStr = CollUtil.join(uperMap.values(),",");

       /* UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id",id);
        updateWrapper.set("ups_str",upsStr);*/
        BbsArticle article1 = new BbsArticle();
        article1.setId(id);
        article1.setUpsStr(upsStr);
        bbsArticleMapper.updateById(article1);
        return  uperMap.values().size();
    }

    @Override
    public Integer collectArticle(String id, Integer uid) {
        BbsArticle article = bbsArticleMapper.selectById(id);
        String collectStr = article.getCollectStr();
        Map<String,String> uperMap = new HashMap<>();
        if(StrUtil.isNotBlank(collectStr)) {
            //
            for(String uper: Arrays.asList(collectStr.split(","))) {
                uperMap.put(uper,uper);
            }
        }

        if(uperMap.containsKey(String.valueOf(uid))) {
            // 取消点赞
            uperMap.remove(String.valueOf(uid));

        }else{
            // 点赞
            uperMap.put(String.valueOf(uid),String.valueOf(uid));
        }

        collectStr = CollUtil.join(uperMap.values(),",");

       /* UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id",id);
        updateWrapper.set("ups_str",upsStr);*/
        BbsArticle article1 = new BbsArticle();
        article1.setId(id);
        article1.setCollectStr(collectStr);
        bbsArticleMapper.updateById(article1);
        return  uperMap.values().size();
    }
}
