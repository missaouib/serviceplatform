package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.BbsArticle;
import co.yixiang.modules.yaoshitong.entity.BbsReply;
import co.yixiang.modules.yaoshitong.mapper.BbsArticleMapper;
import co.yixiang.modules.yaoshitong.mapper.BbsReplyMapper;
import co.yixiang.modules.yaoshitong.service.BbsReplyService;
import co.yixiang.modules.yaoshitong.web.param.BbsReplyQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsAuthorVo;
import co.yixiang.modules.yaoshitong.web.vo.BbsReplyQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 帖子回复表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BbsReplyServiceImpl extends BaseServiceImpl<BbsReplyMapper, BbsReply> implements BbsReplyService {

    @Autowired
    private BbsReplyMapper bbsReplyMapper;

    @Autowired
    private YxUserService yxUserService;

    @Autowired
    private BbsArticleMapper bbsArticleMapper;

    @Override
    public BbsReplyQueryVo getBbsReplyById(Serializable id) throws Exception{
        return bbsReplyMapper.getBbsReplyById(id);
    }

    @Override
    public Paging<BbsReply> getBbsReplyPageList(BbsReplyQueryParam bbsReplyQueryParam,Integer uid) throws Exception{
        Page page = setPageParam(bbsReplyQueryParam,OrderItem.desc("create_at"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(BbsReplyQueryParam.class, bbsReplyQueryParam);
        // queryWrapper.eq("is_del",1);
        IPage<BbsReply> iPage = bbsReplyMapper.selectPage(page,queryWrapper);

        for(BbsReply reply :iPage.getRecords()) {
            // 是否本人回复
            if(reply.getAuthorId().equals(uid)) {
                reply.setIsSelf(true);
            } else {
                reply.setIsSelf(false);
            }
            // 点赞人
            reply.setIsSelfUp(false);
            if(StrUtil.isNotBlank(reply.getUpsStr())) {
                List<String> upsList = Arrays.asList(reply.getUpsStr().split(","));
                reply.setUps(upsList.size());

                if(upsList.contains(String.valueOf(uid))) {
                    reply.setIsSelfUp(true);
                }

            }



            // 获取回复人的姓名，头像
            YxUserQueryVo user_reply = yxUserService.getYxUserById(reply.getAuthorId());
            BbsAuthorVo bbsAuthorVo_reply = new BbsAuthorVo();
            if(user_reply != null) {
                bbsAuthorVo_reply.setAvatar_url(user_reply.getAvatar());
                bbsAuthorVo_reply.setLoginname(user_reply.getNickname());
            }
            reply.setAuthor(bbsAuthorVo_reply);


            // 如果这条是二级回复，则回复内容显示的时候，拼接上原回复内容

            if(StrUtil.isNotBlank(reply.getReplyId())) {
                BbsReply reply1 = bbsReplyMapper.selectById(reply.getReplyId());
                if(reply1 != null) {
                    YxUserQueryVo user_reply1 = yxUserService.getYxUserById(reply1.getAuthorId());
                    if(user_reply1 != null) {
                        reply.setContent(reply.getContent() + "//@" + user_reply1.getNickname() + " " + reply1.getContent());
                    }
                }
            }
        }

        return new Paging(iPage);
    }

    @Override
    public Integer upReply(String id, Integer uid) {
        BbsReply reply = bbsReplyMapper.selectById(id);
        String upsStr = reply.getUpsStr();
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
        BbsReply bbsReply = new BbsReply();
        bbsReply.setId(id);
        bbsReply.setUpsStr(upsStr);
        bbsReplyMapper.updateById(bbsReply);
        return  uperMap.values().size();
    }

    @Override
    public Boolean saveReply(BbsReply bbsReply) {

        BbsArticle bbsArticle = new BbsArticle();
        bbsArticle.setId(bbsReply.getArticleId());
        bbsArticle.setLastReplyAt(DateUtil.date());
        bbsArticleMapper.updateById(bbsArticle);



        return this.save(bbsReply);
    }
}
