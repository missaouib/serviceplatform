/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.web.param.YxStoreOrderQueryParam;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @author visa
* @date 2020-07-13
*/
@Repository
public interface YaoshitongPatientMapper extends CoreMapper<YaoshitongPatient> {
    @Select("SELECT yp.* FROM yx_user yu ,yaoshitong_patient yp WHERE yu.phone = yp.phone\n" +
            "    AND yu.uid = #{uid} LIMIT 1")
    YaoshitongPatient findPatientByUid(@Param("uid") Integer uid);

    @Select("select a.* from yaoshitong_patient a , yaoshitong_patient_relation b ${ew.customSqlSegment}   ")
    IPage<YaoshitongPatient> getPatientPageList(@Param("page") Page page,@Param(Constants.WRAPPER) Wrapper<YaoshitongPatient> wrapper,@Param("pharmacistId") String pharmacistId );
}
