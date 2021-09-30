/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.message.service.impl;

import co.yixiang.modules.message.domain.MessageUser;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.message.service.MessageUserService;
import co.yixiang.modules.message.service.dto.MessageUserDto;
import co.yixiang.modules.message.service.dto.MessageUserQueryCriteria;
import co.yixiang.modules.message.service.mapper.MessageUserMapper;
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
* @author zhoujinlai
* @date 2021-07-28
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "messageUser")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MessageUserServiceImpl extends BaseServiceImpl<MessageUserMapper, MessageUser> implements MessageUserService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MessageUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MessageUser> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MessageUserDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MessageUser> queryAll(MessageUserQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MessageUser.class, criteria));
    }


    @Override
    public void download(List<MessageUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MessageUserDto messageUser : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("消息发送人Id，ALL 为所有人", messageUser.getUserId());
            map.put("发送部门", messageUser.getSendDept());
            map.put(" createTime",  messageUser.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
