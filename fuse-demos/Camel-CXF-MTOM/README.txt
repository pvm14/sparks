DESCRIPTION:
==============

This demo shows how to work with MTOM attachments in a Camel route. 
A camel route receives a SOAP message with MTOM attachment and passes it in 
POJO mode to a bean component that reads the MTOM attachment and constructs 
a reply with another MTOM attachment. 
It is recommended to use POJO mode when using camel-cxf component with MTOM
attachments. 

A SOAP Client is provided as JUnit test case in mtom-client/
Camel route definition and bean implementation is in mtom-service/

Camel route can be run either standalone or deployed inside ServiceMix 
See below for further instructions.
However before deploying inside ServiceMix, you need to manually change the line
 
  DataSource source = new FileDataSource(new File("src/main/resources/fusesource_logo.png"));

in mtom-service/src/main/java/com/fusesource/test/mtom/MtomImpl.java
and point the File URL to some fully qualified filename.


COMPILING:
===========
From top level directory call 
  mvn -Dmaven.test.skip=true install


RUNNING:
==========
The demo can be run in two modes:
- standalone
- deployed into ServiceMix 4.3

Standalone:
------------
- cd mtom-service
- mvn camel:run
- cd mtom-client
- mvn test

Inside ServiceMix
------------------
- start ServiceMix
- cp mtom-service/feature.xml $KARAF_HOME/deploy
  make sure it deploys alright
- cd mtom-client
- mvn test



OUTPUT:
==========

Output from Camel route:
7031 [23757859@qtp-24622291-1] INFO  CamelMessageLogger  - Exchange[ExchangePattern:InOut, BodyType:org.apache.cxf.message.MessageContentsList, Body:[com.fusesource.test.mtom.v1.DoMtomRequest@286fe6]]
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Entered doMtom().
7047 [23757859@qtp-24622291-1] INFO  com.fusesource.test.mtom.MtomImpl  - input: This is my input string.
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Got datahandler and input stream.
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Read bytes of length 1024
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Read bytes of length 1024
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Read bytes of length 1024
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Read bytes of length 409
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Constructing response.
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Response DataSource created.
7047 [23757859@qtp-24622291-1] DEBUG com.fusesource.test.mtom.MtomImpl  - Returning response.
