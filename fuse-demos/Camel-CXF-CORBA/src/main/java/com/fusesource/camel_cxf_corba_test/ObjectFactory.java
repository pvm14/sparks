package com.fusesource.camel_cxf_corba_test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.fusesource.camel_cxf_corba_test package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GridGridException_QNAME = new QName("http://fusesource.com/camel_cxf_corba_test", "Grid.GridException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.fusesource.camel_cxf_corba_test
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetHeightResult }
     * 
     */
    public GetHeightResult createGetHeightResult() {
        return new GetHeightResult();
    }

    /**
     * Create an instance of {@link GetWidthResult }
     * 
     */
    public GetWidthResult createGetWidthResult() {
        return new GetWidthResult();
    }

    /**
     * Create an instance of {@link Set }
     * 
     */
    public Set createSet() {
        return new Set();
    }

    /**
     * Create an instance of {@link GetResponse }
     * 
     */
    public GetResponse createGetResponse() {
        return new GetResponse();
    }

    /**
     * Create an instance of {@link GridGridExceptionType }
     * 
     */
    public GridGridExceptionType createGridGridExceptionType() {
        return new GridGridExceptionType();
    }

    /**
     * Create an instance of {@link Get }
     * 
     */
    public Get createGet() {
        return new Get();
    }

    /**
     * Create an instance of {@link GetHeight }
     * 
     */
    public GetHeight createGetHeight() {
        return new GetHeight();
    }

    /**
     * Create an instance of {@link SetResponse }
     * 
     */
    public SetResponse createSetResponse() {
        return new SetResponse();
    }

    /**
     * Create an instance of {@link GetWidth }
     * 
     */
    public GetWidth createGetWidth() {
        return new GetWidth();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GridGridExceptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fusesource.com/camel_cxf_corba_test", name = "Grid.GridException")
    public JAXBElement<GridGridExceptionType> createGridGridException(GridGridExceptionType value) {
        return new JAXBElement<GridGridExceptionType>(_GridGridException_QNAME, GridGridExceptionType.class, null, value);
    }

}
