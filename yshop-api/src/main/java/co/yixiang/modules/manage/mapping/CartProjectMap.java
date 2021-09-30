package co.yixiang.modules.manage.mapping;

import co.yixiang.mapper.EntityMapper;
import co.yixiang.modules.manage.entity.YxStoreCartProject;
import co.yixiang.modules.manage.web.vo.YxStoreCartProjectQueryVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


/**
* @author hupeng
* @date 2019-10-26
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartProjectMap extends EntityMapper<YxStoreCartProjectQueryVo, YxStoreCartProject> {

}