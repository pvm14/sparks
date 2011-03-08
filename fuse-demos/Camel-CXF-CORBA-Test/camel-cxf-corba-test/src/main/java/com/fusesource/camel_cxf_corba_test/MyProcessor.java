package com.fusesource.camel_cxf_corba_test;

import java.util.List;
import java.util.ArrayList;

import org.apache.camel.Processor; 
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.cxf.CxfConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converts an JAXB object into an ArrayList so that it can be 
 * passed on to a CXF producer endpoint.
 * As the CXF producer endpoint uses the CORBA binding, it is necessary
 * to use the POJO data format.
 * This is necessary because marshaling of CORBA requests is entirely different
 * from marshaling SOAP requests in CXF. The CXF CORBA binding puts the marshal 
 * transport part together.
 * 
 * @See 
 * http://camel.apache.org/cxf.html#CXF-HowtopreparethemessageforthecamelcxfendpointinPOJOdataformat
 * 
 * @author TMIELKE
 *
 */
public class MyProcessor implements Processor {
		
	//Logger
	private static final transient Log LOG = LogFactory.getLog(MyProcessor.class);
    
	//default constructor
	public MyProcessor() { }
		
	/**
	 * Expects the in message of the exchange to hold an JAXB object
	 * of one of the types defined in wsdl/grid.wsdl.
	 * 
	 * @param - The camel exchange
	 */
	public void process(Exchange exchange) throws Exception
	{
		LOG.debug("MyProcessor.process() started.");
		
		// Get the in message
		Message in = exchange.getIn();
		LOG.debug(in.getBody().getClass().toString());
		
		// Check the JAXB type of the in message and 
		// create an ArrayList with the argument values.
		// This list is then set on the Echange in message
		// and passed to the CXF producer endpoint.
		final List<Object> params = new ArrayList<Object>();
		if (in.getBody().getClass().equals(Get.class)) {
			LOG.info("get invoked.");
			Get tmp = (Get) in.getBody();
			params.add(tmp.n);
			params.add(tmp.m);
			in.setHeader(CxfConstants.OPERATION_NAME, "get");
			
		} else if (in.getBody().getClass().equals(Set.class)) {
			LOG.info("set invoked.");			
			Set tmp = (Set) in.getBody();
			params.add(tmp.n);
			params.add(tmp.m);
			params.add(tmp.value);
			in.setHeader(CxfConstants.OPERATION_NAME, "set");
		} 
		
		// set ArrayList as new message body 
		in.setBody(params);
		LOG.debug("MyProcessor.process() finished");
	}
}
