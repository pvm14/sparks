
package com.fusesource.camel_cxf_corba_test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="m" type="{http://www.w3.org/2001/XMLSchema}short"/>
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
    "n",
    "m"
})
@XmlRootElement(name = "get")
public class Get {

    protected short n;
    protected short m;

    /**
     * Gets the value of the n property.
     * 
     */
    public short getN() {
        return n;
    }

    /**
     * Sets the value of the n property.
     * 
     */
    public void setN(short value) {
        this.n = value;
    }

    /**
     * Gets the value of the m property.
     * 
     */
    public short getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     */
    public void setM(short value) {
        this.m = value;
    }

}
