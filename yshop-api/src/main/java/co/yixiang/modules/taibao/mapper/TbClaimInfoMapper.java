/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.taibao.entity.TbClaimInfo;
import co.yixiang.modules.taibao.web.vo.ClaimInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Repository
public interface TbClaimInfoMapper  extends BaseMapper<TbClaimInfo> {

    TbClaimInfo getByClaimno(@Param("claimno") String claimno);

    ClaimInfoVo getByOrderId(@Param("orderId")Long orderId);

    void updateImgUrlById(TbClaimInfo tbClaimInfo);
}
