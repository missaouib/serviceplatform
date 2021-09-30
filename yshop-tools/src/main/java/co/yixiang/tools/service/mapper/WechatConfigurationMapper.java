/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.tools.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.tools.domain.WechatConfiguration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @author zhoujinlai
* @date 2021-09-01
*/
@Repository
@Mapper
public interface WechatConfigurationMapper extends CoreMapper<WechatConfiguration> {

    @Select("<script> select count(1) from wechat_configuration " +
            " where mch_id = #{mchId} and delete_flag=0 and type = #{type}" +
            " <if test=\"id != null\" >" +
            " and id != #{id}" +
            " </if>" +
            " </script>")
    Integer checkedMchid(WechatConfiguration resources);

    @Select("<script> select count(1) from project " +
            " where 1=1 " +
            " <if test=\"wechatHfiveMchid != null\" >" +
            " and wechat_hfive_mchid != #{wechatHfiveMchid}" +
            " </if>" +
            " <if test=\"wechatAppletMchid != null\" >" +
            " and wechat_applet_mchid != #{wechatAppletMchid}" +
            " </if>" +
            " <if test=\"wechatAppMchid != null\" >" +
            " and wechat_app_mchid != #{wechatAppMchid}" +
            " </if>" +
            " <if test=\"wechatZhonganAppid != null\" >" +
            " and wechat_zhongan_mchid != #{wechatZhonganMchid}" +
            " </if>" +
            " </script>")
    Integer getProjectCountByMchId(@Param("wechatHfiveMchid")String wechatHfiveMchid, @Param("wechatAppletMchid")String wechatAppletMchid, @Param("wechatAppMchid")String wechatAppMchid, @Param("wechatZhonganAppid")String wechatZhonganAppid);


    @Select("<script> select count(1) from yx_system_store " +
            " where is_del=0 " +
            " <if test=\"wechatHfiveMchid != null\" >" +
            " and wechat_hfive_mchid != #{wechatHfiveMchid}" +
            " </if>" +
            " <if test=\"wechatAppletMchid != null\" >" +
            " and wechat_applet_mchid != #{wechatAppletMchid}" +
            " </if>" +
            " <if test=\"wechatAppMchid != null\" >" +
            " and wechat_app_mchid != #{wechatAppMchid}" +
            " </if>" +
            " <if test=\"wechatZhonganAppid != null\" >" +
            " and wechat_zhongan_mchid != #{wechatZhonganMchid}" +
            " </if>" +
            " </script>")
    Integer getStoreCountByMchId(@Param("wechatHfiveMchid")String wechatHfiveMchid, @Param("wechatAppletMchid")String wechatAppletMchid, @Param("wechatAppMchid")String wechatAppMchid, @Param("wechatZhonganAppid")String wechatZhonganAppid);

}
