package com.fusesource.test.mtom;

import com.fusesource.test.mtom.v1.DoMtomResponse;
import com.fusesource.test.mtom.v1.DoMtomRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.apache.log4j.Logger;
 
/**
 * Implementation of WSDL MtomTest Port Type.
 * uses JAX-WS annotations. 
 * @author tmielke
 */
@WebService(targetNamespace = "http://www.fusesource.com/test/mtom", name = "MtomTest")
@XmlSeeAlso({com.fusesource.test.mtom.v1.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class MtomImpl implements MtomTest {

	// Log4j logging 
	private static Logger LOG = Logger.getLogger(MtomImpl.class);
	
	/**
	 * Implementation of WSDL business method
	 * @param parameters - input parameter with MTOM encoded attachment
	 * @return mtom encoded response
	 */
    @WebResult(name = "doMtomResponse", targetNamespace = "http://www.fusesource.com/test/mtom/v1", partName = "parameters")
    @WebMethod(action = "http://www.fusesource.com/test/mtom/do")
    public com.fusesource.test.mtom.v1.DoMtomResponse doMtom(
        @WebParam(partName = "parameters", name = "doMtomRequest", targetNamespace = "http://www.fusesource.com/test/mtom/v1")
        com.fusesource.test.mtom.v1.DoMtomRequest parameters
    ) {
    	LOG.debug("Entered doMtom().");
    	LOG.info("input: " + parameters.getInput());
    	
    	// extract incoming data first
    	try {
    	  DataHandler handler = parameters.getData();
    	  InputStream is = handler.getInputStream();
    	  LOG.debug("Got datahandler and input stream.");

    	  // read in MTOM attachment.
    	  int bytesRead = 0;
    	  byte[] buffer = new byte[1024];
    	  while ((bytesRead = is.read(buffer)) != -1) {
    		LOG.debug("Read bytes of length " + bytesRead);
    	  }
    	} catch (IOException e) {
    		LOG.error(e.getMessage(), e);
    	}
    	
    	// construct a response
    	LOG.debug("Constructing response.");

    	// NOTE: If you deploy the Camel route into a container like OSGI, 
    	// loading of the external binary resource will not work anymore. 
    	// You will then need to use fully qualified file names. 
    	DataSource source = new FileDataSource(new File("src/main/resources/fusesource_logo.png"));
    	
    	LOG.debug("Response DataSource created.");
    	DoMtomResponse response = new DoMtomResponse();
    	response.setReturn("This is a return message.");
    	response.setData(new DataHandler(source));
    	
    	LOG.debug("Returning response.");
    	return response;
    }
}