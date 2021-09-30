/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import co.yixiang.modules.msh.domain.MshDemandListFile;
import co.yixiang.modules.msh.domain.MshDemandListItem;
import co.yixiang.modules.msh.domain.MshPatientInformation;
import co.yixiang.modules.msh.domain.MshPatientListFile;

import java.io.Serializable;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshDemandListForCreateDto implements Serializable {
	//患者信息
	private MshPatientInformation mshPatientInformation;

	//需求单附件表申请表
	private List<MshDemandListFile> mshDemandListFileListApplication;

	//需求单附件表病例
	private List<MshDemandListFile> mshDemandListFileListCaseUrl;

	//需求单附件表处方照片
	private List<MshDemandListFile> mshDemandListFileListPicUrl;

	//需求单附件表其他
	private List<MshDemandListFile> mshDemandListFileList;

	//需求单患者信息附件表
	private List<MshPatientListFile> mshPatientListFileList;

	//需求单详细表
	private List<MshDemandListItem> mshDemandListItemList;

	//新增或修改FLAG
	private String flag;

	//需求单ID
	private Integer id;

	//保存状态  保存状态(0：保存 1：需求单提交  2：审核提交 3：驳回)
	private Integer saveStatus;

	/**
	 * '来源（APP/Wechat/线下）'
	 */
	private String source;

}
