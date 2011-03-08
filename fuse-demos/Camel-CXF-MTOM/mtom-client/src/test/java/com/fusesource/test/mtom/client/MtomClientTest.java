package com.fusesource.test.mtom.client;

import com.fusesource.test.mtom.MtomTest;
import com.fusesource.test.mtom.MtomTestService;
import com.fusesource.test.mtom.v1.DoMtomRequest;
import com.fusesource.test.mtom.v1.DoMtomResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import junit.framework.Assert;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;
import org.junit.Test;


public class MtomClientTest {

	private static Logger LOG = Logger.getLogger(MtomClientTest.class);
	
	@Test
	public void runMtomTest() throws Exception {
		LOG.info("runMtomTest starting.");
		MtomTestService service = new MtomTestService(new URL("http://localhost:8102/test/mtom/v1?wsdl"));
		MtomTest mtom = service.getMtom();
		((SOAPBinding) ((BindingProvider) mtom).getBinding()).setMTOMEnabled(true);
		
		// for debugging raise request timeout
		LOG.debug("raising default jetty http timeout.");
		Client client = ClientProxy.getClient(mtom);
		HTTPConduit http = (HTTPConduit) client.getConduit();
		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(36000);
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setReceiveTimeout(10*60*1000);
		http.setClient(httpClientPolicy);
		// end

		// construct request object including MTOM attachment
		LOG.debug("Creating request object");
		DoMtomRequest request = new DoMtomRequest();
		request.setInput("This is my input string.");
		DataSource source = new FileDataSource(new File("./src/test/resources/fusesource_logo.png"));
		request.setData((new DataHandler(source)));

		// invoke and get response
		LOG.debug("invoking on server.");
		DoMtomResponse response = mtom.doMtom(request);
		InputStream in = response.getData().getInputStream();

		try {
			// read in MTOM attachment.
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) != -1) {
				LOG.debug("Read bytes of length " + bytesRead);
			}
	    } catch (IOException e) {
	    	LOG.error(e.getMessage(), e);
	    }
		Assert.assertEquals("This is a return message.", response.getReturn());
		LOG.debug("Test finished.");
	}
}
