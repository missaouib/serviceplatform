package co.yixiang.tools.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author joni
 */
public class TeddyResult implements Serializable {
    private static final long serialVersionUID = 4408692564578617502L;

    public static Map<String, String> resultMap = new HashMap<String, String>();

    static {
        resultMap.put("0", "提交成功");
        resultMap.put("1", "账号无效");
        resultMap.put("2", "密码错误");
        resultMap.put("3", "订单产生失败");
        resultMap.put("4", "错误号码/限制运营商号码");
        resultMap.put("5", "手机号码个数超过最大限制");
        resultMap.put("6", "短信内容超过最大限制");
        resultMap.put("7", "扩展子号码无效");
        resultMap.put("8", "定时时间格式错误");
        resultMap.put("14", "手机号码为空");
        resultMap.put("19", "用户被禁发或禁用");
        resultMap.put("20", "ip鉴权失败");
        resultMap.put("21", "短信内容为空");
        resultMap.put("22", "数据包大小不匹配");
        resultMap.put("24", "无可用号码");
        resultMap.put("25", "批量提交短信数超过最大限制");
        resultMap.put("26", "模板未报备（废弃）");
        resultMap.put("27", "签名未报备");
        resultMap.put("28", "进入发送队列");
        resultMap.put("98", "系统正忙");
        resultMap.put("99", "消息格式错误");
        resultMap.put("101", "服务错误");
        resultMap.put("102", "请勿重复提交");
        resultMap.put("163", "短信发送，记录修改失败");
        resultMap.put("164", "短信条数为空或者超过限制");
        resultMap.put("165", "调用短信接口出错");
        resultMap.put("166", "手机号码不正确");
        resultMap.put("167", "变量短信参数格式不对应");
        resultMap.put("171", "短信内容字数超过了最大限制350个字");
        resultMap.put("172", "可用短信条数不足，请充值");
        resultMap.put("-190", "数据操作失败");
        resultMap.put("-1901", "数据库插入操作失败");
        resultMap.put("-1902", "数据库更新操作失败");
        resultMap.put("-1903", "数据库删除操作失败");
    }


    /**
     * error_code : 0
     * error_msg : 提交成功
     * data : {"result":true,"orderId":"20200212215"}
     */

    private int error_code;
    private String error_msg;
    private DataBean data;


    /**
     * 是否成功发送
     *
     * @return
     */
    public Boolean isSuccess() {
        if (error_code == 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * result : true
         * orderId : 20200212215
         */

        private boolean result;
        private String orderId;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }
}
