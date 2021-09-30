package co.yixiang.tools.express.route;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="Request")
public class Request implements Serializable {
    @XmlAttribute(name="service")
    private String service;
    @XmlAttribute(name="lang")
    private String lang;
    @XmlElement(name="Head")
    private String head;
    @XmlElement(name="Body")
    private Body body;

    public void setService(String service) {
        this.service = service;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
