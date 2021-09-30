package co.yixiang.modules.yaoshitong.mapping;

import co.yixiang.mapper.EntityMapper;
import co.yixiang.modules.shop.entity.YxStoreProductAttr;
import co.yixiang.modules.shop.web.vo.YxStoreProductAttrQueryVo;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPrescription;

import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionListQueryVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


/**
* @author visa
* @date 2019-10-19
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface YaoshitongPrescriptionListMap extends EntityMapper<YaoshitongPrescriptionListQueryVo, YaoshitongPrescription> {

}