DESCRIPTION:
==============
This demo shows how to leverage the camel-cxf component to invoke on a CORBA 
server. CXF has support for CORBA and IIOP, so it can be used to invoke on 
external CORBA servers.
When combined with Camel, it is possible to route messages from all sorts of 
formats and transports to and from CORBA.

The configuration of CXF when using the CORBA binding is a bit tricky but this
demo can be used as a reference.

The CXF producer endpoint configuration requires a wsdl file that contains a 
CORBA binding in order to be able to invoke on an external CORBA server. For an 
existing CORBA server with existing IDL, it is possible to use the CXF tool
idl2wsdl to generate a corresponding WSDL file. 
That WSDL file will not define any SOAP bindings but will use CORBA binding 
instead. The WSDL file can then be used to generate JAX-WS stub code using
the CXF tool wsdl2java. All these pieces are required in order to fully 
configure the CXF producer endpoint. 

This demo uses a simple Camel route as defined in 
src/main/resources/META-INF/spring/camel-context.xml
It starts with a timer that periodically every 60 seconds generates a fixed 
soap message. 

<route>
  <from uri="timer://myTimer?fixedRate=true&amp;period=60000"/>
  <setBody>
	<constant>
	  <![CDATA[
	    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	      xmlns:cam="http://fusesource.com/camel_cxf_corba_test">
          <soapenv:Header/>
          <soapenv:Body>
            <cam:set>
              <n>1</n>
              <m>1</m>
              <value>10</value>
            </cam:set>
          </soapenv:Body>
        </soapenv:Envelope>
      ]]>
    </constant>
  </setBody>

This SOAP message is then passed into an XQuery transformer that strips off the
SOAP header, so that the next Camel component only works on the message payload.

  <transform>
	<!-- assuming only one child under <soapenv:Body> -->
   	<xquery>/soap:Envelope/soap:Body/child::*</xquery>
  </transform>


When using the CORBA binding in a cxf producer endpoint, it is necessary to 
use the POJO dataformat in the cxf component. This is required because 
marshaling of CORBA requests is entirely different from marshaling SOAP 
requests in CXF. The CXF CORBA binding puts the marshal and transport part 
together. Hence it is necessary to convert the raw message into a 
java.util.ArrayList containing the argument values of the WSDL/CORBA operation
to invoke on. Some sample code is given in 
http://camel.apache.org/cxf.html#CXF-HowtopreparethemessageforthecamelcxfendpointinPOJOdataformat

This conversion is easiest done in a custom Java bean processor 
(See MyProcessor.java). 
To make this conversion easier in the Java bean processor, it is possible to
unmarshal the message payload into JAXB object (as generated upfront by the 
wsdl2java compiler) and deal with the JAXB object inside the custom Java bean
processor. That way it is not necessary to manually parse the message payload 
using either an XML parser or any other parsing method).

  <unmarshal ref="myJaxb"/>

So the message payload is first converted into a JAXB object before it is 
being passed to a custom Java bean processor that converts the data of the 
JAXB object into an java.util.ArrayList. 
  
  <process ref="MyProcessor"/>
  
This ArrayList can then be passed to the CXF producer endpoint. 

  <to uri="cxf:bean:CORBAServiceEndpoint?dataFormat=POJO"/>

Its WSDL file is configured to use the CORBA binding and hence a CORBA 
invocation to the external CORBA server is performed. 
Any operation results are sent to a Camel logger component.  

  <to uri="log:AfterCORBAInvocation"/>
</route>

Again, the JAXB marshaling step is not required but makes life easier for the 
Java processor. 

In case you wonder if its possible to pass the JAXB unmarshaled object straight
into the CXF producer endpoint, then the answer is no. Because in POJO mode the
data format is a "list of Java objects", one object for each parameter. The JAXB
object however encapsulates all parameters in a single object. Hence the need for
a custom conversion.


Project Directory Structure:
-----------------------------
./corba - the standalone CORBA server. It uses the ORB provided by Sun's JRE. 
  Contains the IDL file grid.idl and the server implementation code in 
  server/server.jar

./src/main/java - contains all the Java code including the custom Camel bean processor 
  (MyProcessor.java) and all of the JAX-WS generated stub code.

./src/main/resources/wsdl/grid.wsdl - the wsdl generated from corba/idl/grid.idl.

./src/main/resources/META-INF/spring/camel-context.xml - The Camel route definition and 
  CXF endpoint configuration

./src/main/filtered-resources/features.xml - template OSGi features file that gets
  processed as part of the mvn install. 


COMPILING:
===========
For compiling the demo simply run 
  mvn clean install 
to re-compile the demo.

The CORBA server is already provided and does not need to be recompiled. 
Also the WSDL using a CORBA binding was pre-generated out of the CORBA IDL.
And so was the JAX-WS code generated from grid.wsdl up-front. 
Running a mvn install will not re-generated any of these files but will 
recompile the JAX-WS generated Java code.


RUNNING:
==========
The demo can either be run standalone with the help of the 
camel-maven-plugin or it can be easily deployed into ServiceMix.


Running standalone:
--------------------
- Start the corba server using run_corba_server.bat. 
  It writes its IOR to stdout and to a file ior.txt

- Run mvn camel:run. This will start the Camel route and
  process any periodically generated messages.


Running in OSGI (SMX 4.3):
---------------------------
- Start the corba server using run_corba_server.bat.
  It writes its IOR to stdout and to a file ior.txt
  
- Manually change the address attribute of the <cxfEndpoint> configuration
  in src/main/resources/META-INF/spring/camel-context.xml
  and provide the full path to ior.txt
  
- run mvn install again

- start ServiceMix 4.3.

- cp target/classes/features.xml into $SMX_HOME/deploy folder.
  This will deploy the demo and any dependent OSGi features. 
  It will also start the Camel route and process any 
  periodically generated messages.



OUTPUT:
==========
The output from running the camel route standalone and in OSGi is similar to

07.01.2011 14:44:44 org.apache.camel.impl.DefaultCamelContext start
INFO: Route: route1 started and consuming from: Endpoint[timer://myTimer?fixedRate=true&period=60000]
07.01.2011 14:44:44 org.apache.camel.impl.DefaultCamelContext start
INFO: Started 1 routes
07.01.2011 14:44:44 org.apache.camel.impl.DefaultCamelContext start
INFO: Apache Camel 2.4.0-fuse-02-00 (CamelContext: camelContext) started in 1.375 seconds
07.01.2011 14:44:45 org.apache.camel.processor.Logger process
INFO: Exchange[ExchangePattern:InOnly, BodyType:com.sun.org.apache.xerces.internal.dom.DocumentImpl, Body:<cam:set xmlns:cam="http://fusesource.com/camel_cxf_corba_test" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
                <n>1</n>
                <m>1</m>
                <value>10</value>
              </cam:set>]
07.01.2011 14:44:45 com.fusesource.camel_cxf_corba_test.MyProcessor process
INFO: set invoked.
07.01.2011 14:44:45 org.apache.camel.processor.Logger process
INFO: Exchange[ExchangePattern:InOnly, BodyType:java.util.ArrayList, Body:[1, 1, 10]]
07.01.2011 14:44:45 org.apache.camel.processor.Logger process
INFO: Exchange[ExchangePattern:InOnly, BodyType:org.apache.cxf.message.MessageContentsList, Body:[]]
07.01.2011 14:44:48 org.apache.camel.spring.Main$HangupInterceptor run



The output in the CORBA server should read:
set (1, 1) = 10



-------------------------------------------------------------------------------

Finally a general note about the steps required to invoke on a CORBA server 
from a camel route.

One needs to basically follow these steps:
------------------------------------------
- Create a new maven project with a camel route
  using the servicemix-osig-camel-archetype. 
  
- Get the IDL from the external CORBA server. 

- Generate a WSDL from this CORBA IDL:
  idl2wsdl -I ./corba/idl/ -o . -w http://fusesource.com/camel_cxf_corba_test \ 
    -x http://fusesource.com/camel_cxf_corba_test -ow grid.wsdl \ 
    -verbose ./corba/idl/grid.idl
  The WSDL will not contain any SOAP bindings but only a CORBA binding.
  Note, a CXF installation is needed for running the idl2wsdl tool.
  
- Generate JAX-WS stub code out of the WSDL
  wsdl2java -d ./stub -verbose grid.wsdl  
  to generate JAX-WS stubs for this WSDL interface
  
- Add the generated JAX-WS stub code to your maven project
  source code folder so that it gets recompiled with a mvn build.

- Run the CORBA server and have it export its IOR.

- Define whatever Camel route needed and add a CXF producer endpoint 
  in camel-context.xml:
  <cxf:cxfEndpoint id="CORBAServiceEndpoint" 
     wsdlURL="classpath:wsdl/grid.wsdl"
     serviceClass="com.fusesource.camel_cxf_corba_test.Grid"
     endpointName="s:GridCORBAPort"
     serviceName="s:GridCORBAService"
     address="./ior.txt"
     xmlns:s="http://fusesource.com/camel_cxf_corba_test" >
  </cxf:cxfEndpoint>
  Make sure all these attributes are set correctly.
  The address attribute needs to either name a file containing
  the CORBA servers IOR string (as above) or needs to name the 
  IOR string directly, like 
  address="IOR:0000...."


- Provide a custom camel processor that maps the message payload to 
  a java.util.ArrayList, containing the values of the WSDL/IDL
  operation arguments. See MyProcessor.java for an example.
  The camel cxf endpoint needs to use the POJO data format, e.g.
  <to uri="cxf:bean:CORBAServiceEndpoint?dataFormat=POJO"/>

- Recompile the project.
  
- Deploy and run the generated artifact. 

     