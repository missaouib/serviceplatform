/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.taibao.entity.TbClaimInfo;
import co.yixiang.modules.taibao.mapper.TbClaimInfoMapper;
import co.yixiang.modules.taibao.service.TbClaimInfoService;
import co.yixiang.modules.taibao.web.vo.ClaimInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TbClaimInfoServiceImpl extends BaseServiceImpl<TbClaimInfoMapper, TbClaimInfo> implements TbClaimInfoService {

    @Autowired
    private TbClaimInfoMapper claimInfoMapper;

    @Value("${file.path}")
    private String path;

    @Override
    public TbClaimInfo getByClaimno(String claimno) {
        return claimInfoMapper.getByClaimno(claimno);
    }

    @Override
    public ClaimInfoVo getByOrderId(Long orderId) {
        return claimInfoMapper.getByOrderId(orderId);
    }

    @Override
    public void updateImgUrlById(TbClaimInfo tbClaimInfo) {
        claimInfoMapper.updateImgUrlById(tbClaimInfo);
    }

}
