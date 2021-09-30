package co.yixiang.modules.taibao.service.vo;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
@Data
public class RootVo {
    private ClaimInfoVo claimInfo;

}
