package co.yixiang.modules.yaoshitong.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPrescription;
import co.yixiang.modules.yaoshitong.mapping.YaoshitongPrescriptionVoMap;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.service.YaoshitongPrescriptionService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPrescriptionQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionListQueryVo;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;

import co.yixiang.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

/**
 * <p>
 * 药师通-处方信息表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-07-17
 */
@Slf4j
@RestController
@RequestMapping("/yaoshitongPrescription")
@Api("药师通-处方信息表 API")
public class YaoshitongPrescriptionController extends BaseController {

    @Autowired
    private YaoshitongPrescriptionService yaoshitongPrescriptionService;

    @Autowired
    private YaoshitongPrescriptionVoMap yaoshitongPrescriptionVoMap;

    @Autowired
    private YaoshitongPatientService yaoshitongPatientService;

    @Autowired
    private MdPharmacistServiceService pharmacistService;

    /**
    * 添加药师通-处方信息表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YaoshitongPrescription对象",notes = "添加药师通-处方信息表",response = ApiResult.class)
    public ApiResult<Boolean> addYaoshitongPrescription(@Valid @RequestBody YaoshitongPrescription yaoshitongPrescription) throws Exception{
        if(StrUtil.isBlank(yaoshitongPrescription.getPharmacistId() )) {

            Integer uid = SecurityUtils.getUserId().intValue();
            MdPharmacistService pharmacist = pharmacistService.getMdPharmacistByUid(uid);
            if(pharmacist != null) {
                yaoshitongPrescription.setPharmacistId(pharmacist.getId());
            }
        }
       // yaoshitongPrescription.setCreateTime(new Date( System.currentTimeMillis()));
       // yaoshitongPrescription.setUpdateTime(new Date( System.currentTimeMillis()));
        boolean flag = yaoshitongPrescriptionService.saveOrUpdate(yaoshitongPrescription);
        return ApiResult.result(flag);
    }

    /**
    * 修改药师通-处方信息表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YaoshitongPrescription对象",notes = "修改药师通-处方信息表",response = ApiResult.class)
    public ApiResult<Boolean> updateYaoshitongPrescription(@Valid @RequestBody YaoshitongPrescription yaoshitongPrescription) throws Exception{
        boolean flag = yaoshitongPrescriptionService.updateById(yaoshitongPrescription);
        return ApiResult.result(flag);
    }

    /**
    * 删除药师通-处方信息表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YaoshitongPrescription对象",notes = "删除药师通-处方信息表",response = ApiResult.class)
    public ApiResult<Boolean> deleteYaoshitongPrescription(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yaoshitongPrescriptionService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取药师通-处方信息表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YaoshitongPrescription对象详情",notes = "查看药师通-处方信息表",response = YaoshitongPrescriptionQueryVo.class)
    public ApiResult<YaoshitongPrescriptionQueryVo> getYaoshitongPrescription(@Valid @RequestBody IdParam idParam) throws Exception{

        YaoshitongPrescription yaoshitongPrescription = yaoshitongPrescriptionService.getById(idParam.getId());
        YaoshitongPrescriptionQueryVo vo = yaoshitongPrescriptionVoMap.toDto(yaoshitongPrescription);
         // 获得患者信息
        YaoshitongPatient patient = yaoshitongPatientService.getById(yaoshitongPrescription.getPatientId());
        if(patient != null) {
            if(StrUtil.isNotBlank(patient.getBirth())) {
                vo.setAge( DateUtil.ageOfNow(patient.getBirth() + "01"));
            }

            vo.setName(patient.getName());
            vo.setPhone(patient.getPhone());
            vo.setAddress(patient.getAddress());
        }
        return ApiResult.ok(vo);
    }

    /**
     * 药师通-处方信息表分页列表
     */
    @AnonymousAccess
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YaoshitongPrescription分页列表",notes = "药师通-处方信息表分页列表",response = YaoshitongPrescriptionQueryVo.class)
    public ApiResult<Paging<YaoshitongPrescriptionListQueryVo>> getYaoshitongPrescriptionPageList(@Valid @RequestBody(required = false) YaoshitongPrescriptionQueryParam yaoshitongPrescriptionQueryParam) throws Exception{
        if( StrUtil.isBlank(yaoshitongPrescriptionQueryParam.getPharmacistId() )) {
            Integer uid = SecurityUtils.getUserId().intValue();
            MdPharmacistService pharmacist = pharmacistService.getMdPharmacistByUid(uid);
            if(pharmacist != null) {
                yaoshitongPrescriptionQueryParam.setPharmacistId(pharmacist.getId());
            }
        }
        Paging<YaoshitongPrescriptionListQueryVo> paging = yaoshitongPrescriptionService.getYaoshitongPrescriptionPageList(yaoshitongPrescriptionQueryParam);
        return ApiResult.ok(paging);
    }

}

