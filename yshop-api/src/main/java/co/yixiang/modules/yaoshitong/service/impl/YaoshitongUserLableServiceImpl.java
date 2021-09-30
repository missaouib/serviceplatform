package co.yixiang.modules.yaoshitong.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongUserLableMapper;
import co.yixiang.modules.yaoshitong.service.YaoshitongUserLableService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
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


/**
 * <p>
 * 药师通用户标签 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YaoshitongUserLableServiceImpl extends BaseServiceImpl<YaoshitongUserLableMapper, YaoshitongUserLable> implements YaoshitongUserLableService {

    @Autowired
    private YaoshitongUserLableMapper yaoshitongUserLableMapper;

    @Override
    public YaoshitongUserLableQueryVo getYaoshitongUserLableById(Serializable id) throws Exception{
        return yaoshitongUserLableMapper.getYaoshitongUserLableById(id);
    }

    @Override
    public Paging<YaoshitongUserLable> getYaoshitongUserLablePageList(YaoshitongUserLableQueryParam yaoshitongUserLableQueryParam) throws Exception{
        Page page = setPageParam(yaoshitongUserLableQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YaoshitongUserLableQueryParam.class, yaoshitongUserLableQueryParam);
        IPage<YaoshitongUserLable> iPage = yaoshitongUserLableMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

    @Override
    public void saveUserLable(YaoshitongUserLable yaoshitongUserLable) {
        if(yaoshitongUserLable.getIsDefault() != null &&  yaoshitongUserLable.getIsDefault() == 1) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_default",0);
            updateWrapper.eq("uid",yaoshitongUserLable.getUid());
            this.update(updateWrapper);
        }

        if(yaoshitongUserLable.getId() == null) {
            // 判断标签名称是否已经存在
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("lable_name",yaoshitongUserLable.getLableName());
            queryWrapper.eq("uid",yaoshitongUserLable.getUid());
            int count = this.count(queryWrapper);
            if(count >0 ) {
                throw new ErrorRequestException("标签名称已经存在");
            }
            this.save(yaoshitongUserLable);
        } else {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("lable_name",yaoshitongUserLable.getLableName());
            queryWrapper.eq("uid",yaoshitongUserLable.getUid());
            queryWrapper.ne("id",yaoshitongUserLable.getId());
            int count = this.count(queryWrapper);
            if(count >0 ) {
                throw new ErrorRequestException("标签名称已经存在");
            }
            this.updateById(yaoshitongUserLable);
        }
    }
}
