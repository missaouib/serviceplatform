package co.yixiang.modules.yaoshitong.web.vo;

import lombok.Data;

@Data
public class ChatUserInfo {
    private Integer uidSelf;
    private String nameSelf;
    private String imageSelf;

    private Integer uidOther;
    private String nameOther;
    private String imageOther;
}
