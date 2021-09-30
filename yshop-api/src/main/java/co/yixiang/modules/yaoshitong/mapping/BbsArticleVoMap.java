package co.yixiang.modules.yaoshitong.mapping;

import co.yixiang.mapper.EntityMapper;

import co.yixiang.modules.yaoshitong.entity.BbsArticle;
import co.yixiang.modules.yaoshitong.web.vo.BbsArticleQueryVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


/**
* @author visa
* @date 2019-10-19
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BbsArticleVoMap extends EntityMapper<BbsArticleQueryVo, BbsArticle> {

}