/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.enums.WechatNameEnum;
import co.yixiang.mp.config.WxMpConfiguration;
import co.yixiang.mp.domain.YxWechatUserInfo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.mp.service.YxWechatUserInfoService;
import co.yixiang.mp.service.dto.YxWechatUserInfoDto;
import co.yixiang.mp.service.dto.YxWechatUserInfoQueryCriteria;
import co.yixiang.mp.service.mapper.YxWechatUserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visazhou
* @date 2020-12-27
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxWechatUserInfo")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class YxWechatUserInfoServiceImpl extends BaseServiceImpl<YxWechatUserInfoMapper, YxWechatUserInfo> implements YxWechatUserInfoService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxWechatUserInfoQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxWechatUserInfo> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxWechatUserInfoDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxWechatUserInfo> queryAll(YxWechatUserInfoQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxWechatUserInfo.class, criteria));
    }


    @Override
    public void download(List<YxWechatUserInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxWechatUserInfoDto yxWechatUserInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息", yxWechatUserInfo.getSubscribe());
            map.put("用户的昵称", yxWechatUserInfo.getNickname());
            map.put("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知", yxWechatUserInfo.getSex());
            map.put("用户的语言，简体中文为zh_CN", yxWechatUserInfo.getLanguage());
            map.put("用户所在城市", yxWechatUserInfo.getCity());
            map.put("用户所在省份", yxWechatUserInfo.getProvince());
            map.put("用户所在国家", yxWechatUserInfo.getCountry());
            map.put("公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注", yxWechatUserInfo.getRemark());
            map.put("用户id", yxWechatUserInfo.getUid());
            map.put(" createTime",  yxWechatUserInfo.getCreateTime());
            map.put(" updateTime",  yxWechatUserInfo.getUpdateTime());
            map.put("用户的标识，对当前公众号唯一", yxWechatUserInfo.getOpenId());
            map.put("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知", yxWechatUserInfo.getSexDesc());
            map.put("用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。", yxWechatUserInfo.getHeadImgUrl());
            map.put("用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间", yxWechatUserInfo.getSubscribeTime());
            map.put("只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。", yxWechatUserInfo.getUnionId());
            map.put("公众号名称", yxWechatUserInfo.getWechatName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void fetchUser() {
        WxMpService wxService = WxMpConfiguration.getWxMpService(WechatNameEnum.WECHAT.getValue());
        // 获取所有关注的用户openId
        try{
            WxMpUserList wxMpUserList = wxService.getUserService().userList("");
            // 循环用户列表，获得用户详情
            for( String openid : wxMpUserList.getOpenids()) {
                WxMpUser wxMpUser =  wxService.getUserService().userInfo(openid);
                log.info("公众号关注用户：{}",wxMpUser);
                if(wxMpUser != null && StrUtil.isNotBlank(wxMpUser.getUnionId()) ) {
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq("union_id",wxMpUser.getUnionId());
                    queryWrapper.eq("wechat_name",WechatNameEnum.WECHAT.getValue());
                    queryWrapper.select("id");
                    YxWechatUserInfo yxWechatUserInfo = this.getOne(queryWrapper,false);
                    if(yxWechatUserInfo == null) {
                        yxWechatUserInfo = new YxWechatUserInfo();
                    }

                    BeanUtils.copyProperties(wxMpUser,yxWechatUserInfo);
                    yxWechatUserInfo.setWechatName(WechatNameEnum.WECHAT.getValue());
                    saveOrUpdate(yxWechatUserInfo);
                }

            }
        } catch (WxErrorException e) {

        }

    }

    @Override
    public void updateWechatOpenidByUniqueId(String uniqueId,String wechatOpenId) {
        if(StrUtil.isNotBlank(uniqueId) && StrUtil.isNotBlank(wechatOpenId)) {
            baseMapper.updateWechatOpenidByUniqueId(uniqueId,wechatOpenId);
        }

    }
}
