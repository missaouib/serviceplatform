package co.yixiang.modules.yaoshitong.mapping;

import co.yixiang.mapper.EntityMapper;
import co.yixiang.modules.yaoshitong.entity.ChatGroup;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupQueryVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


/**
* @author visa
* @date 2019-10-19
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatGroupVoMap extends EntityMapper<ChatGroupQueryVo, ChatGroup> {

}