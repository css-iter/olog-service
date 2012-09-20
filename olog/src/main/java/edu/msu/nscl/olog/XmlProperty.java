/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Property object that can be represented as XML/JSON in payload data.
 *
 * @author berryman
 */
@XmlRootElement(name = "property")
public class XmlProperty {

    private Long id;
    private int groupingNum;
    private String name = null;
    private Map<String, String> attributes;
    private Logs logs = null;

    /**
     * Creates a new instance of XmlProperty.
     *
     */
    public XmlProperty() {
    }

    /**
     * Creates a new instance of XmlProperty.
     *
     * @param name
     * @param value
     */
    public XmlProperty(String name) {
        this.name = name;
    }

    /**
     * @param name
     * @param attributes
     */
    public XmlProperty(String name, Map<String, String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    /**
     * Getter for property id.
     *
     * @return property id
     */
    @XmlAttribute
    public Long getId() {
        return id;
    }

    /**
     * Setter for property id.
     *
     * @param id property id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for property id.
     *
     * @return property id
     */
    @XmlAttribute
    public int getGroupingNum() {
        return groupingNum;
    }

    /**
     * Setter for property id.
     *
     * @param id property id
     */
    public void setGroupingNum(int groupingNum) {
        this.groupingNum = groupingNum;
    }

    /**
     * Getter for property name.
     *
     * @return property name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for property name.
     *
     * @param name property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Getter for property's Logs.
     *
     * @return XmlChannels object
     */
    @XmlElement(name = "logs")
    public Logs getXmlLogs() {
        return logs;
    }

    /**
     * Setter for property's Logs.
     *
     * @param logs Logs object
     */
    public void setXmlLogs(Logs logs) {
        this.logs = logs;
    }

    public Property toProperty() {
        Property prop = new Property(this.getName());
        for (Map.Entry<String, String> att : this.getAttributes().entrySet()) {
            Attribute newAtt = new Attribute(att.getKey());
            newAtt.setState(State.Active);
            newAtt.setProperty(prop);
            prop.addAttribute(newAtt);
        }
        prop.setId(this.getId());
        prop.setState(State.Active);
        return prop;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlProperty to log
     * @return string representation for log
     */
    public static String toLogger(XmlProperty data) {
        if (data.logs == null) {
            return data.getName() + "(" + data.getAttributes().toString() + ")";
        } else {
            return data.getName() + "(" + data.getAttributes().toString() + ")"
                    + Logs.toLogger(data.logs);
        }
    }
}
