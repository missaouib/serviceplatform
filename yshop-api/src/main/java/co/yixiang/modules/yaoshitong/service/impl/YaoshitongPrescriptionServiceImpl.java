package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPrescription;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongPatientMapper;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongPrescriptionMapper;
import co.yixiang.modules.yaoshitong.mapping.YaoshitongPrescriptionListMap;
import co.yixiang.modules.yaoshitong.service.YaoshitongPrescriptionService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPrescriptionQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionListQueryVo;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 药师通-处方信息表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-07-17
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YaoshitongPrescriptionServiceImpl extends BaseServiceImpl<YaoshitongPrescriptionMapper, YaoshitongPrescription> implements YaoshitongPrescriptionService {

    @Autowired
    private YaoshitongPrescriptionMapper yaoshitongPrescriptionMapper;

    @Autowired
    private YaoshitongPrescriptionListMap yaoshitongPrescriptionListMap;

    @Autowired
    private YaoshitongPatientMapper yaoshitongPatientMapper;

    @Override
    public YaoshitongPrescriptionQueryVo getYaoshitongPrescriptionById(Serializable id) throws Exception{
        return yaoshitongPrescriptionMapper.getYaoshitongPrescriptionById(id);
    }

    @Override
    public Paging<YaoshitongPrescriptionListQueryVo> getYaoshitongPrescriptionPageList(YaoshitongPrescriptionQueryParam yaoshitongPrescriptionQueryParam) throws Exception{
        Page page = setPageParam(yaoshitongPrescriptionQueryParam,OrderItem.desc("update_time"));
        QueryWrapper<YaoshitongPrescription> queryWrapper = QueryHelpPlus.getPredicate(YaoshitongPrescriptionQueryParam.class, yaoshitongPrescriptionQueryParam);
        if(StrUtil.isNotBlank(yaoshitongPrescriptionQueryParam.getKeyword())) {
            // queryWrapper.and(Wrapper -> Wrapper.like("name",yaoshitongPrescriptionQueryParam.getKeyword()).or().like("phone",yaoshitongPrescriptionQueryParam.getKeyword()));
            queryWrapper.apply(" exists (select 1 from yaoshitong_patient b where b.id = yaoshitong_prescription.patient_id and ( b.name like concat('%',{0},'%') or b.phone like concat('%',{1},'%')))");
        }
        IPage iPage = yaoshitongPrescriptionMapper.selectPage(page,queryWrapper);
        List<YaoshitongPrescriptionListQueryVo> voList = new ArrayList<>();
        for(Object object:iPage.getRecords()) {
            YaoshitongPrescription prescription = (YaoshitongPrescription)object;
            YaoshitongPrescriptionListQueryVo vo = yaoshitongPrescriptionListMap.toDto(prescription);
            // 获得患者信息
            YaoshitongPatient patient = yaoshitongPatientMapper.selectById(prescription.getPatientId());
            if(patient != null) {
                if(StrUtil.isNotBlank(patient.getBirth())) {
                    vo.setAge( DateUtil.ageOfNow(patient.getBirth() + "01"));
                }

                vo.setName(patient.getName());
                vo.setPhone(patient.getPhone());
            }

            voList.add(vo);
        }
        iPage.setRecords(voList);
        return new Paging(iPage);
    }

    @Override
    public int saveYaoshitongPrescription(YaoshitongPrescription resource) {
        return 0;
    }
}
