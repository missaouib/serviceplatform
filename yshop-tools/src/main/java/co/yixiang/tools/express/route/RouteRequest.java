package co.yixiang.tools.express.route;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

public class RouteRequest implements Serializable {
    @XmlAttribute(name="tracking_type")
    private String tracking_type;
    @XmlAttribute(name="method_type")
    private String method_type;
    @XmlAttribute(name="tracking_number")
    private String tracking_number;

    public void setTracking_type(String tracking_type) {
        this.tracking_type = tracking_type;
    }

    public void setMethod_type(String method_type) {
        this.method_type = method_type;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number;
    }
}
