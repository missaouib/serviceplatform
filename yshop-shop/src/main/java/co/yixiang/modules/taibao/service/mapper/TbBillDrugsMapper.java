/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.taibao.domain.TbBillDrugs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Repository
@Mapper
public interface TbBillDrugsMapper extends CoreMapper<TbBillDrugs> {

    public boolean deleteByMainId(@Param("mainId") Long mainId);

    public List<TbBillDrugs> selectByMainId(@Param("mainId") Long mainId);


}
