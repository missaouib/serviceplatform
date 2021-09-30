package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.BbsReply;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.BbsReplyQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsReplyQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 帖子回复表 服务类
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
public interface BbsReplyService extends BaseService<BbsReply> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    BbsReplyQueryVo getBbsReplyById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param bbsReplyQueryParam
     * @return
     */
    Paging<BbsReply> getBbsReplyPageList(BbsReplyQueryParam bbsReplyQueryParam,Integer uid) throws Exception;

    Integer upReply(String id,Integer uid);

    Boolean saveReply(BbsReply bbsReply);
}
