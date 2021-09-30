/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.mp.domain.YxWechatUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author visazhou
* @date 2020-12-27
*/
@Repository
@Mapper
public interface YxWechatUserInfoMapper extends CoreMapper<YxWechatUserInfo> {

    @Update("UPDATE yx_wechat_user ywu SET ywu.openid = #{wechatOpenId} WHERE ywu.unionid = #{uniqueId} ")
   void updateWechatOpenidByUniqueId(@Param("uniqueId") String uniqueId,@Param("wechatOpenId") String wechatOpenId);
}
