package co.yixiang.tools.express.route;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class Body implements Serializable {
    @XmlElement(name="RouteRequest")
    private RouteRequest routeRequest;

    public void setRouteRequest(RouteRequest routeRequest) {
        this.routeRequest = routeRequest;
    }
}
