package com.fusesource.camel_cxf_corba_test;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Progress FUSE Services Framework 2.2.10-fuse-00-00
 * Wed Dec 29 12:15:47 CET 2010
 * Generated source version: 2.2.10-fuse-00-00
 * 
 */
 
@WebService(targetNamespace = "http://fusesource.com/camel_cxf_corba_test", name = "Grid")
@XmlSeeAlso({ObjectFactory.class})
public interface Grid {

    @RequestWrapper(localName = "set", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.Set")
    @WebMethod
    @ResponseWrapper(localName = "setResponse", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.SetResponse")
    public void set(
        @WebParam(name = "n", targetNamespace = "")
        short n,
        @WebParam(name = "m", targetNamespace = "")
        short m,
        @WebParam(name = "value", targetNamespace = "")
        int value
    ) throws GridGridException;

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "get", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.Get")
    @WebMethod
    @ResponseWrapper(localName = "getResponse", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.GetResponse")
    public int get(
        @WebParam(name = "n", targetNamespace = "")
        short n,
        @WebParam(name = "m", targetNamespace = "")
        short m
    ) throws GridGridException;

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "_get_height", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.GetHeight")
    @WebMethod(operationName = "_get_height")
    @ResponseWrapper(localName = "_get_heightResult", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.GetHeightResult")
    public short getHeight();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "_get_width", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.GetWidth")
    @WebMethod(operationName = "_get_width")
    @ResponseWrapper(localName = "_get_widthResult", targetNamespace = "http://fusesource.com/camel_cxf_corba_test", className = "com.fusesource.camel_cxf_corba_test.GetWidthResult")
    public short getWidth();
}