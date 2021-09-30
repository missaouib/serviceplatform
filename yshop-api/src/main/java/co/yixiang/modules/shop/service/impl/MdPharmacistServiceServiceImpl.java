package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.mapper.MdPharmacistServiceMapper;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.shop.web.param.MdPharmacistServiceQueryParam;
import co.yixiang.modules.shop.web.vo.MdPharmacistServiceQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 药师在线配置表 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-09
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MdPharmacistServiceServiceImpl extends BaseServiceImpl<MdPharmacistServiceMapper, MdPharmacistService> implements MdPharmacistServiceService {

    @Autowired
    private MdPharmacistServiceMapper mdPharmacistServiceMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public MdPharmacistServiceQueryVo getMdPharmacistServiceById(Serializable id) throws Exception{
        return mdPharmacistServiceMapper.getMdPharmacistServiceById(id);
    }

    @Override
    public Paging<MdPharmacistServiceQueryVo> getMdPharmacistServicePageList(MdPharmacistServiceQueryParam mdPharmacistServiceQueryParam) throws Exception{
        Page page = setPageParam(mdPharmacistServiceQueryParam,OrderItem.desc("create_time"));
        IPage<MdPharmacistServiceQueryVo> iPage = mdPharmacistServiceMapper.getMdPharmacistServicePageList(page,mdPharmacistServiceQueryParam);
        return new Paging(iPage);
    }

    @Override
    public MdPharmacistService getMdPharmacistByUid(Integer id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",id);
        return this.getOne(queryWrapper,true);
    }

    @Override
    public List<MdPharmacistService> getPharmacistByPatientId(Integer patientId) {
        QueryWrapper queryWrapper = new QueryWrapper();
      //  queryWrapper.apply("EXISTS (SELECT 1 FROM yaoshitong_patient_relation ypr WHERE  md_pharmacist_service.ID = ypr.pharmacist_id AND ypr.patient_id = {0} )",patientId);

        queryWrapper.apply(" a.ID = b.pharmacist_id AND b.patient_id = {0}",patientId);
        queryWrapper.orderByDesc("b.update_time");
        Page<MdPharmacistService> pageModel = new Page<>(1,
                20);
        IPage<MdPharmacistService> pharmacistPage = mdPharmacistServiceMapper.getPharmacistPageList(pageModel,queryWrapper);
        for(MdPharmacistService pharmacist : pharmacistPage.getRecords()) {
            Integer senduserid = pharmacist.getUid();
            Integer reviceuserid = SecurityUtils.getUserId().intValue();
            String key = "msgUnread-"+senduserid+"-"+reviceuserid;

            if(redisUtils.get(key) != null) {
                Integer unReadCount = Integer.valueOf(String.valueOf(redisUtils.get(key)));
                pharmacist.setUnRead(unReadCount);
            }
        }
        return pharmacistPage.getRecords();

    }
}
