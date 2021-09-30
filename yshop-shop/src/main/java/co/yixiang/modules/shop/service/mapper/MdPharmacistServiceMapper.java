/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.MdPharmacistService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @author visa
* @date 2020-06-02
*/
@Repository
@Mapper
public interface MdPharmacistServiceMapper extends CoreMapper<MdPharmacistService> {

       @Select("SELECT COUNT(1) as cc FROM yaoshitong_patient_relation ypr,yaoshitong_patient yp\n" +
               "  WHERE ypr.patient_id = yp.id AND yp.uid IS NOT null and  ypr.pharmacist_id = #{id} ")
       int countPatientByPharmacistId(@Param("id") String id);
}
