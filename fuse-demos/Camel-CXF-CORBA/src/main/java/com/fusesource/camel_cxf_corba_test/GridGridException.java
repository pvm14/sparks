package com.fusesource.camel_cxf_corba_test;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Progress FUSE Services Framework 2.2.10-fuse-00-00
 * Wed Dec 29 12:15:47 CET 2010
 * Generated source version: 2.2.10-fuse-00-00
 * 
 */

@WebFault(name = "Grid.GridException", targetNamespace = "http://fusesource.com/camel_cxf_corba_test")
public class GridGridException extends Exception {
    public static final long serialVersionUID = 20101229121547L;
    
    private com.fusesource.camel_cxf_corba_test.GridGridExceptionType gridGridException;

    public GridGridException() {
        super();
    }
    
    public GridGridException(String message) {
        super(message);
    }
    
    public GridGridException(String message, Throwable cause) {
        super(message, cause);
    }

    public GridGridException(String message, com.fusesource.camel_cxf_corba_test.GridGridExceptionType gridGridException) {
        super(message);
        this.gridGridException = gridGridException;
    }

    public GridGridException(String message, com.fusesource.camel_cxf_corba_test.GridGridExceptionType gridGridException, Throwable cause) {
        super(message, cause);
        this.gridGridException = gridGridException;
    }

    public com.fusesource.camel_cxf_corba_test.GridGridExceptionType getFaultInfo() {
        return this.gridGridException;
    }
}