package co.yixiang.modules.msh.service.enume;

/**
 * @author liuyun
 */
public interface MshStatusEnum {


    enum AuditStatus {
        /**
         * 审核状态（0：待审核，1：客服审核通过，2：客服审核不通过 3：药剂师审核通过，4：药剂师审核不通过 5：驳回  6：取消）
         */
        DSH(0, "待审核"),
        KFSHTG(1, "客服审核通过"),
        KFSHBTG(2, "客服审核不通过"),
        YJSSHTG(3, "药剂师审核通过"),
        YJSSHBTG(4, "药剂师审核不通过"),
        BH(5, "驳回"),
        QX(6, "取消"),;

        private Integer code;
        private String value;

        AuditStatus(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static String getValueByCode(String code) {
            for (AuditStatus auditStatus : values()) {
                if (auditStatus.getCode()==Integer.parseInt(code)) {
                    return auditStatus.getValue();
                }
            }
            return null;
        }
    }


    enum SaveStatus {
        /**
         * 保存状态(0：保存 1：需求单提交  2：审核提交 3：驳回，)
         */
        BC(0, "保存"),
        QXDTJ(1, "需求单提交"),
        SHTJ(2, "审核提交"),
        BH(3, "驳回"),;

        private Integer code;
        private String value;

        SaveStatus(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static String getValueByCode(String code) {
            for (SaveStatus saveStatus : values()) {
                if (saveStatus.getCode()==Integer.parseInt(code)) {
                    return saveStatus.getValue();
                }
            }
            return null;
        }
    }


    enum OrderStatus {
        /**
         * 订单状态（0：待审核，1：审核通过，2：审核不通过 3：已发货，4：已完成 5：已退货  6：驳回）
         */
        DSH(0, "待审核"),
        SHTG(1, "审核通过"),
        SHBTG(2, "审核不通过"),
        YFH(3, "已发货"),
        YWC(4, "已完成"),
        YTH(5, "已退货"),
        BH(6, "驳回"),;

        private Integer code;
        private String value;

        OrderStatus(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static String getValueByCode(String code) {
            for (OrderStatus orderStatus : values()) {
                if (orderStatus.getCode()==Integer.parseInt(code)) {
                    return orderStatus.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 取消原因
     */
    enum CancelReason {
        reason_1("01","无法配送，请联系专属客服","2"),
        reason_2("02","保单过期，无法配送","1"),
        reason_3("03","受限于您的保单福利，无法配送","1"),
        reason_4("04","国家管控类药物，无法配送","1"),
        reason_5("05","冷链药物，无法配送","1"),
        reason_6("06","单轨制药物，无法配送","1"),
        reason_7("07","已有相同申请，无法重复配送","1"),
        reason_8("08","物流不能到达，无法配送","1"),
        reason_9("09","药品剂量需由专业医师控制，为保证您的用药安全，本次不提供配送","1"),
        reason_10("10","客户要求取消","1"),
        reason_12("11","配送药物剂量与处方不符或超过福利规定药量，无法配送","1"),
        reason_13("12","申请药品内容与病历不符，无法配送","1"),
        reason_14("13","医疗文件（病历或处方笺）中缺少配送药品明细信息，无法配送","1"),
        reason_15("14","缺少病历或处方笺，无法配送","1"),
        reason_16("15","暂无此药品，无法配送","1"),
        reason_17("16","病历/处方笺非一年内出具，无法配送","1"),
        reason_18("17","病历/处方未由中国大陆境内有合法医疗资质的公/私立医疗机构出具，无法配送","1"),
        reason_19("99","其他原因","2"),

        ;


        private String code;
        private String value;
        private String type;

        CancelReason(String code, String value,String type) {
            this.code = code;
            this.value = value;
            this.type = type;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static CancelReason getCancelReason(String value) {
            for (CancelReason cancelReason : values()) {
                if (cancelReason.getValue().equals(value)) {
                    return cancelReason;
                }
            }
            return null;
        }

        public static CancelReason getCancelReasonValue(String code) {
            for (CancelReason cancelReason : values()) {
                if (cancelReason.getCode().equals(code)) {
                    return cancelReason;
                }
            }
            return null;
        }
    }

}
