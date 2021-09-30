
package co.yixiang.tools.express.domain;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Head" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Body">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RouteResponse">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Route" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                     &lt;attribute name="remark" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="accept_time" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="accept_address" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="opcode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="mailno" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="service" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "head",
    "body"
})
@XmlRootElement(name = "Response")
public class RouteResponseInfo {

    @XmlElement(name = "Head", required = true)
    protected String head;
    @XmlElement(name = "Body", required = true)
    protected RouteResponseInfo.Body body;
    @XmlAttribute(name = "service")
    protected String service;

    /**
     * ��ȡhead���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHead() {
        return head;
    }

    /**
     * ����head���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHead(String value) {
        this.head = value;
    }

    /**
     * ��ȡbody���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link RouteResponseInfo.Body }
     *
     */
    public RouteResponseInfo.Body getBody() {
        return body;
    }

    /**
     * ����body���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link RouteResponseInfo.Body }
     *
     */
    public void setBody(RouteResponseInfo.Body value) {
        this.body = value;
    }

    /**
     * ��ȡservice���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getService() {
        return service;
    }

    /**
     * ����service���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setService(String value) {
        this.service = value;
    }


    /**
     * <p>anonymous complex type�� Java �ࡣ
     *
     * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="RouteResponse">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Route" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                           &lt;attribute name="remark" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="accept_time" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="accept_address" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="opcode" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="mailno" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "routeResponse"
    })
    public static class Body {

        @XmlElement(name = "RouteResponse", required = true)
        protected RouteResponseInfo.Body.RouteResponse routeResponse;

        /**
         * ��ȡrouteResponse���Ե�ֵ��
         *
         * @return
         *     possible object is
         *     {@link RouteResponseInfo.Body.RouteResponse }
         *
         */
        public RouteResponseInfo.Body.RouteResponse getRouteResponse() {
            return routeResponse;
        }

        /**
         * ����routeResponse���Ե�ֵ��
         *
         * @param value
         *     allowed object is
         *     {@link RouteResponseInfo.Body.RouteResponse }
         *
         */
        public void setRouteResponse(RouteResponseInfo.Body.RouteResponse value) {
            this.routeResponse = value;
        }


        /**
         * <p>anonymous complex type�� Java �ࡣ
         *
         * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="Route" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                 &lt;attribute name="remark" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="accept_time" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="accept_address" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="opcode" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/extension>
         *             &lt;/simpleContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="mailno" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "route"
        })
        public static class RouteResponse {

            @XmlElement(name = "Route")
            protected List<Route> route;
            @XmlAttribute(name = "mailno")
            protected String mailno;

            /**
             * Gets the value of the route property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the route property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getRoute().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link RouteResponseInfo.Body.RouteResponse.Route }
             *
             *
             */
            public List<Route> getRoute() {
                if (route == null) {
                    route = new ArrayList<Route>();
                }
                return this.route;
            }

            /**
             * ��ȡmailno���Ե�ֵ��
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMailno() {
                return mailno;
            }

            /**
             * ����mailno���Ե�ֵ��
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMailno(String value) {
                this.mailno = value;
            }


            /**
             * <p>anonymous complex type�� Java �ࡣ
             * 
             * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *       &lt;attribute name="remark" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="accept_time" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="accept_address" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="opcode" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/extension>
             *   &lt;/simpleContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Route {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "remark")
                protected String remark;
                @XmlAttribute(name = "accept_time")
                protected String acceptTime;
                @XmlAttribute(name = "accept_address")
                protected String acceptAddress;
                @XmlAttribute(name = "opcode")
                protected String opcode;

                /**
                 * ��ȡvalue���Ե�ֵ��
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getValue() {
                    return value;
                }

                /**
                 * ����value���Ե�ֵ��
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setValue(String value) {
                    this.value = value;
                }

                /**
                 * ��ȡremark���Ե�ֵ��
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getRemark() {
                    return remark;
                }

                /**
                 * ����remark���Ե�ֵ��
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setRemark(String value) {
                    this.remark = value;
                }

                /**
                 * ��ȡacceptTime���Ե�ֵ��
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getAcceptTime() {
                    return acceptTime;
                }

                /**
                 * ����acceptTime���Ե�ֵ��
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setAcceptTime(String value) {
                    this.acceptTime = value;
                }

                /**
                 * ��ȡacceptAddress���Ե�ֵ��
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getAcceptAddress() {
                    return acceptAddress;
                }

                /**
                 * ����acceptAddress���Ե�ֵ��
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setAcceptAddress(String value) {
                    this.acceptAddress = value;
                }

                /**
                 * ��ȡopcode���Ե�ֵ��
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getOpcode() {
                    return opcode;
                }

                /**
                 * ����opcode���Ե�ֵ��
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setOpcode(String value) {
                    this.opcode = value;
                }

            }

        }

    }

}
