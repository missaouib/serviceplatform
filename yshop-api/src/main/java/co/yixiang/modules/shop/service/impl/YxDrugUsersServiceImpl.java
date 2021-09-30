package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.YxDrugUsers;
import co.yixiang.modules.shop.mapper.YxDrugUsersMapper;
import co.yixiang.modules.shop.service.YxDrugUsersService;
import co.yixiang.modules.shop.web.param.YxDrugUsersQueryParam;
import co.yixiang.modules.shop.web.vo.YxDrugUsersQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.IDCardUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 用药人列表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-12-20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxDrugUsersServiceImpl extends BaseServiceImpl<YxDrugUsersMapper, YxDrugUsers> implements YxDrugUsersService {

    @Autowired
    private YxDrugUsersMapper yxDrugUsersMapper;

    @Override
    public YxDrugUsersQueryVo getYxDrugUsersById(Serializable id) throws Exception{
        return yxDrugUsersMapper.getYxDrugUsersById(id);
    }

    @Override
    public Paging<YxDrugUsers> getYxDrugUsersPageList(YxDrugUsersQueryParam yxDrugUsersQueryParam) throws Exception{
        Page page = setPageParam(yxDrugUsersQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxDrugUsersQueryParam.class, yxDrugUsersQueryParam);
        IPage<YxDrugUsers> iPage = yxDrugUsersMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

    @Override
    public YxDrugUsers saveDrugUsers(YxDrugUsers yxDrugUsers) {

        if(yxDrugUsers.getIsDefault() != null &&  yxDrugUsers.getIsDefault() == 1) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_default",0);
            updateWrapper.eq("uid",yxDrugUsers.getUid());
            this.update(updateWrapper);
        }

        // 计算出生年月和性别
        if(StrUtil.isNotBlank(yxDrugUsers.getIdcard())) {
            String idCard = yxDrugUsers.getIdcard();
            String sex = IDCardUtil.getSex(idCard);
            yxDrugUsers.setSex(sex);
            String birth = IDCardUtil.getBirthday(idCard);
            birth = birth.substring(0,8);
            yxDrugUsers.setBirth(birth);
        }
        if(yxDrugUsers.getId() == null) {
            yxDrugUsers.setCreateTime(new Date());
            yxDrugUsers.setUpdateTime(new Date());
            this.save(yxDrugUsers);
        } else {
            yxDrugUsers.setUpdateTime(new Date());
            this.updateById(yxDrugUsers);
        }

        return yxDrugUsers;

    }

    @Override
    public YxDrugUsers getUserDefaultDrugUser(int uid) {
        QueryWrapper<YxDrugUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("is_default",1).eq("uid",uid).eq("is_del",0).last("limit 1");
        return getOne(wrapper);

    }

    @Override
    public YxDrugUsers getDrugUserByInfo(int uid, String drugUserName, String drugUserPhone) {
        LambdaQueryWrapper<YxDrugUsers> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(YxDrugUsers::getUid,uid);
        lambdaQueryWrapper.eq(YxDrugUsers::getName,drugUserName);
        YxDrugUsers yxDrugUsers = this.getOne(lambdaQueryWrapper,false);
        if(yxDrugUsers == null) {
            yxDrugUsers = new YxDrugUsers();
            yxDrugUsers.setUserType(1);
            yxDrugUsers.setUid(uid);
            yxDrugUsers.setIsDel(0);
            yxDrugUsers.setName(drugUserName);
            yxDrugUsers.setPhone(drugUserPhone);
            save(yxDrugUsers);
        }

        return yxDrugUsers;
    }
}
