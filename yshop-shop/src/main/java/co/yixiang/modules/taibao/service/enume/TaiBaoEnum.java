package co.yixiang.modules.taibao.service.enume;

/**
 * @author liuyun
 */
public interface TaiBaoEnum {


    enum OutsourcerSystemIdType {
        /**
         * 外包商系统 证件类型
         */
        SFZ("a", "身份证"),
        ZGSFZ("b", "中国新身份证"),
        HZ("c", "护照"),
        JGZ("d", "军官证"),
        SFZHM("e", "身份证号码"),
        JSZ("4", "驾驶证"),
        XNSFZ("g", "虚拟身份证"),
        CSZS("h", "出生证书"),
        OTHER("z", "其他"),
        JLZJ("3", "居留证件"),
        GAZJ("j", "港澳台护照"),
        LSSFZ("k", "临时身份证"),
        TWJMTXZ("l", "台湾居民通行证"),
        JMHKB("7", "居民户口簿"),
        CSZ("n", "出生证"),
        CSYXZM("o", "出生医学证明"),
        SBZ("5", "士兵证"),
        WGRYJJLZ("q", "外国人永久居留证"),
        GAJMJZZ("r", "港澳居民居住证"),
        TWJMJZZ("s", "台湾居民居住证"),
        JGLTXZ("6", "军官离退休证"),
        YCSFZ("8", "异常身份证"),
        WXZ("1", "回乡证"),
        JXZ("2", "旅行证"),;

        private String code;
        private String value;

        OutsourcerSystemIdType(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (OutsourcerSystemIdType communityCommentStatus : values()) {
                if (code.equals(communityCommentStatus.getCode())) {
                    return communityCommentStatus.getValue();
                }
            }
            return null;
        }
    }


    enum InformantInsuredRelationshipCode {
        /**
         * 报案人与被保人关系代码
         */
        BENREN("401", "本人"),
        PEIOU("402", "配偶"),
        FUZI("403", "父子"),
        FUNV("404", "父女"),
        SHOUYIREN("405", "受益人"),
        BEIBAOREN("406", "被保人"),
        TOUBAOREN("407", "投保人"),
        OTHER("408", "其他"),
        MUZI("409", "母子"),
        MUNV("410", "母女"),
        XIONGZI("411", "兄弟"),
        MEIMEI("412", "姊妹"),
        XIONGMEI("413", "兄妹"),
        JIEDI("414", "姐弟"),
        ZUSUN("415", "祖孙"),
        GUYONG("416", "雇佣"),
        YEWUYUAN("417", "业务员"),;

        private String code;
        private String value;

        InformantInsuredRelationshipCode(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (InformantInsuredRelationshipCode informantInsuredRelationshipCode : values()) {
                if (code.equals(informantInsuredRelationshipCode.getCode())) {
                    return informantInsuredRelationshipCode.getValue();
                }
            }
            return null;
        }
    }


    enum NatureClaimAccident {
        /**
         * 索赔事故性质
         */
        sg("01", "身故"),
        sc("02", "伤残"),
        zdjb("03", "重大疾病"),
        mjzyl("04", "门急诊医疗"),
        zyyl("05", "住院医疗"),
        zybt("06", "住院补贴"),
        nxsy("07", "女性生育"),;

        private String code;
        private String value;

        NatureClaimAccident(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (NatureClaimAccident natureClaimAccident : values()) {
                if (code.equals(natureClaimAccident.getCode())) {
                    return natureClaimAccident.getValue();
                }
            }
            return null;
        }
    }


    enum CompensationConclusion {
        /**
         * 赔付结论
         */
        ZCPF("01", "正常赔付"),
        JF("02", "拒付"),
        BFPF("03", "部分赔付"),
        TF("04", "通融"),
        XY("05", "协议"),
        BYLA("06", "不予立案"),
        CA("07", "撤案"),;

        private String code;
        private String value;

        CompensationConclusion(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (CompensationConclusion compensationConclusion : values()) {
                if (code.equals(compensationConclusion.getCode())) {
                    return compensationConclusion.getValue();
                }
            }
            return null;
        }
    }


    enum RiskArea {
        /**
         * 出险地区
         */
        DLDQ("1", "大陆地区"),
        GAT("2", "港澳台"),
        JWBHGAT("3", "境外不含港澳台"),;

        private String code;
        private String value;

        RiskArea(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (RiskArea riskArea : values()) {
                if (code.equals(riskArea.getCode())) {
                    return riskArea.getValue();
                }
            }
            return null;
        }
    }


    enum HangUpSign {
        /**
         * 挂起类型
         */
        BCZL("1", "补充资料"),
        LPTJ("2", "理赔体检"),
        XT("3", "协谈"),
        FWHQ("4", "法务会签"),
        SXTPXT("5", "寿险贴牌协谈，不生成通知书"),
        TC("6", "调查"),
        ZBYJ("7", "再保意见"),
        BQHQ("8", "保全会签"),
        HBHQ("9", "核保会签"),
        TJ("10", "体检"),
        FQZ("11", "反欺诈"),;

        private String code;
        private String value;

        HangUpSign(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (HangUpSign hangUpSign : values()) {
                if (code.equals(hangUpSign.getCode())) {
                    return hangUpSign.getValue();
                }
            }
            return null;
        }
    }


    enum BillingCode {
        /**
         * 账单项目
         */
        ZHENLIAOF("000001","诊疗费"),
        ZILIAOF("000002","治疗费"),
        SHOUSHUF("000003","手术费"),
        JIANCHAF("000004","检查费"),
        HUALIAOF("000005","化验费"),
        SHEPIAOF("000006","摄片费"),
        TOUSHIF("000007","透视费"),
        XIYAOF("000008","西药费"),
        ZHONGYAOF("000009","中成药费"),
        zhongcaoyaof("000010","中草药费"),
        guahaof("000011","挂号费"),
        caoliaof("000012","材料费"),
        HULIF("000014","护理费"),
        SHUXUEF("000015","输血费"),
        shuyangf("000016","输氧费"),
        ZYCWF("000017","住院床位费"),
        OTHER("000099","其它"),
        qiangjiuf("000040","抢救费"),
        CT("000041","CT"),
        TEJIANF("000042","特检费"),
        BCHAO("000044","B超"),
        MINGZYF("000091","民族药费"),
        ZIZYJ("000092","自制药剂"),
        ZHONGJF("000093","正畸费"),
        XIANGYAF("000991","镶牙费"),
        SHIFAF("000992","司法鉴定"),
        JIUHUCF("000993","救护车费"),
        BIANZHECHUFANGF("000994","辩证处方费"),;

        private String code;
        private String value;

        BillingCode(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (BillingCode billingCode : values()) {
                if (code.equals(billingCode.getCode())) {
                    return billingCode.getValue();
                }
            }
            return null;
        }
    }



    enum BenefitPersonIdType {
        /**
         *  证件类型
         */
        SFZ("1", "统一社会信用代码"),
        ZGSFZ("2", "营业执照注册号"),
        HZ("3", "组织机构代码"),
        XNSFZ("4", "税务登记号");

        private String code;
        private String value;

        BenefitPersonIdType(String code, String value) {
            this.code = code;
            this.value = value;
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

        public static String getValueByCode(String code) {
            for (BenefitPersonIdType benefitPersonIdType : values()) {
                if (code.equals(benefitPersonIdType.getCode())) {
                    return benefitPersonIdType.getValue();
                }
            }
            return null;
        }
    }


}
