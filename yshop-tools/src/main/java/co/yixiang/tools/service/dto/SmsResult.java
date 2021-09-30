package co.yixiang.tools.service.dto;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dell
 * Date: 12-12-2
 * Time: 下午9:38
 * To change this template use File | Settings | File Templates.
 */
public class SmsResult implements Serializable {
	private static final long serialVersionUID = 4408693564578617502L;
	public static Map<String, String> resultMap = new HashMap<String, String>();

    static {
        resultMap.put("-2","客户端异常");
        resultMap.put("-9000","数据格式错误,数据超出数据库允许范围");
        resultMap.put("-9001","序列号格式错误");
        resultMap.put("-9002","密码格式错误");
        resultMap.put("-9003","客户端Key格式错误");
        resultMap.put("-9004","设置转发格式错误");
        resultMap.put("-9005","公司地址格式错误");
        resultMap.put("-9006","企业中文名格式错误");
        resultMap.put("-9007","企业中文名简称格式错误");
        resultMap.put("-9008","邮件地址格式错误");
        resultMap.put("-9009","企业英文名格式错误");
        resultMap.put("-9010","企业英文名简称格式错误");
        resultMap.put("-9011","传真格式错误");
        resultMap.put("-9012","联系人格式错误");
        resultMap.put("-9013","联系电话");
        resultMap.put("-9014","邮编格式错误");
        resultMap.put("-9015","新密码格式错误");
        resultMap.put("-9016","发送短信包大小超出范围");
        resultMap.put("-9017","发送短信内容格式错误");
        resultMap.put("-9018","发送短信扩展号格式错误");
        resultMap.put("-9019","发送短信优先级格式错误");
        resultMap.put("-9020","发送短信手机号格式错误");
        resultMap.put("-9021","发送短信定时时间格式错误");
        resultMap.put("-9022","发送短信唯一序列值错误");
        resultMap.put("-9023","充值卡号格式错误");
        resultMap.put("-9024","充值密码格式错误");
        resultMap.put("-9025","客户端请求sdk5超时（需确认）");
        resultMap.put("0","成功");
        resultMap.put("-1","系统异常");
        resultMap.put("-101","命令不被支持");
        resultMap.put("-102","RegistryTransInfo删除信息失败（转接）");
        resultMap.put("-103","RegistryInfo更新信息失败（序列号相关注册）");
        resultMap.put("-104","请求超过限制");
        resultMap.put("-111","企业注册失败");
        resultMap.put("-117","发送短信失败");
        resultMap.put("-118","接收MO失败");
        resultMap.put("-119","接收Report失败");
        resultMap.put("-120","修改密码失败");
        resultMap.put("-122","号码注销失败");
        resultMap.put("-110","号码注册激活失败");
        resultMap.put("-123","查询单价失败");
        resultMap.put("-124","查询余额失败");
        resultMap.put("-125","设置MO转发失败");
        resultMap.put("-126","路由信息失败");
        resultMap.put("-127","计费失败0余额");
        resultMap.put("-128","计费失败余额不足");
        resultMap.put("-1100","序列号错误,序列号不存在内存中,或尝试攻击的用户");
        resultMap.put("-1103","序列号Key错误");
        resultMap.put("-1102","序列号密码错误");
        resultMap.put("-1104","路由失败，请联系系统管理员");
        resultMap.put("-1105","注册号状态异常, 未用 1");
        resultMap.put("-1107","注册号状态异常, 停用 3");
        resultMap.put("-1108","注册号状态异常, 停止 5");
        resultMap.put("-113","充值失败");
        resultMap.put("-1131","充值卡无效");
        resultMap.put("-1132","充值密码无效");
        resultMap.put("-1133","充值卡绑定异常");
        resultMap.put("-1134","充值状态无效");
        resultMap.put("-1135","充值金额无效");
        resultMap.put("-190","数据操作失败");
        resultMap.put("-1901","数据库插入操作失败");
        resultMap.put("-1902","数据库更新操作失败");
        resultMap.put("-1903","数据库删除操作失败");
    }

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * 获取发送返回消息
     *
     * @return
     */
    public String getRetMsg() {
        if(resultMap.keySet().contains(error)) {
            return (String)resultMap.get(error);
        }

        return "未定义错误!";
    }

    /**
     * 是否成功发送
     *
     * @return
     */
    public Boolean isSuccess() {
        if(error.equals("0")) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
