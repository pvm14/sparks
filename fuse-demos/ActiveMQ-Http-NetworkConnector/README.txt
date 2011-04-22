
DESCRIPTION:
================

Testcase tests the http network connector going through a http proxy (simulated via a socket proxy). The
socket proxy simulates an unstable network by stalling data going over the network connector
and by periodically randomly dropping the http networkconnection between the two brokers.

This testcase builds on existing tests within ActiveMQ by extending JmsMultipleBrokersTestSupport.java

TODO: The testcase could be modified to test flaky network connections using the tcp:// transport


PREREQUISITES:
================
Fuse MB 5.4.x

COMPILING:
================

Edit the pom.xml to configure the version of the broker you want to run the test against.

>mvn install



RUNNING:
================
 
Two consoles:

Console 1: Broker B - waits for incoming NetworkConnection, checks all msgs arrive, sends an END msg to tell A to shutdown.

> mvn -Dtest=BrokerBQueueHttpNetworkWithDisconnectTest test

Console 2: Broker A - opens a duplex network connector (via socket proxy) to broker B:
This broker also simulates stalled network ..

>mvn -Dtest=BrokerAQueueHttpNetworkWithDisconnectTest test

Note: You must start BrokerB first

Note: To set this up in a remote network, just need to change the targetBindLocation hostname
       in BrokerAQueueHttpNetworkWithDisconnectTest.java

This is the URI that the socket proxy will forward data on to...by default is set to http://localhost:61617

protected String getTargetBindLocation() {
    return "http://localhost:61617";
}

Sample Output - Broker A
========================

$ mvn -Dtest=BrokerAQueueHttpNetworkWithDisconnectTest test
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building Broker HTTP Proxy Test 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.4.3:resources (default-resources) @ http-reconnect-test ---
[WARNING] Using platform encoding (MacRoman actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /Users/scranton/workspaces/testcase_01_24_11/src/main/resources
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ http-reconnect-test ---
[INFO] No sources to compile
[INFO]
[INFO] --- maven-resources-plugin:2.4.3:testResources (default-testResources) @ http-reconnect-test ---
[WARNING] Using platform encoding (MacRoman actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 1 resource
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ http-reconnect-test ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-surefire-plugin:2.7.1:test (default-test) @ http-reconnect-test ---
[INFO] Surefire report directory: /Users/scranton/workspaces/testcase_01_24_11/target/surefire-reports

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.apache.activemq.bugs.BrokerAQueueHttpNetworkWithDisconnectTest
INFO  | Starting auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
INFO  | Starting auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
TEST TRANSPORT DISCONNECT
=========================
remoteUri: http://localhost:61617
SOCKETPROXY URI: http://localhost:55861
STARTING brokers ...
INFO  | PListStore:target/activemq-data/BrokerA/tmp_storage started | org.apache.activemq.store.kahadb.plist.PListStore | main
INFO  | Using Persistence Adapter: KahaDBPersistenceAdapter[target/activemq-data/BrokerA/KahaDB] | org.apache.activemq.broker.BrokerService | main
INFO  | Persistence store purged. | org.apache.activemq.store.kahadb.MessageDatabase | main
INFO  | ActiveMQ 5.4.2-fuse-01-00 JMS Message Broker (BrokerA) is starting | org.apache.activemq.broker.BrokerService | main
INFO  | For help or more information please see: http://activemq.apache.org/ | org.apache.activemq.broker.BrokerService | main
2011-02-11 14:17:50.727:INFO::jetty-7.1.6.v20100715
2011-02-11 14:17:50.833:INFO::Started SocketConnector@0.0.0.0:61616
INFO  | Connector http://0.0.0.0:61616?initialDelayTime=14000&keepAliveResponseRequired=true&readCheckTime=20000 Started | org.apache.activemq.broker.TransportConnector | main
INFO  | Establishing network connection from vm://BrokerA?async=false&network=true to http://localhost:55861 | org.apache.activemq.network.DiscoveryNetworkConnector | main
DEBUG | binding to broker: BrokerA | org.apache.activemq.transport.vm.VMTransportFactory | main
INFO  | Connector vm://BrokerA Started | org.apache.activemq.broker.TransportConnector | main
DEBUG | HTTP GET consumer thread starting: HTTP Reader http://localhost:55861 | org.apache.activemq.transport.http.HttpClientTransport | main
INFO  | accepted Socket[addr=/127.0.0.1,port=55862,localport=55861], receiveBufferSize:81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | accepted Socket[addr=/127.0.0.1,port=55862,localport=55861], receiveBufferSize:81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | proxy connection Socket[addr=localhost/127.0.0.1,port=61617,localport=55863], receiveBufferSize=81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
DEBUG | HTTP GET consumer thread starting: HTTP Reader http://localhost:55861 | org.apache.activemq.transport.http.HttpClientTransport | ActiveMQ Transport: HTTP Reader http://localhost:55861
INFO  | Network Connector localhost Started | org.apache.activemq.network.NetworkConnector | main
INFO  | ActiveMQ JMS Message Broker (BrokerA, ID:scranton-55860-1297451870356-1:1) started | org.apache.activemq.broker.BrokerService | main
INFO  | accepted Socket[addr=/127.0.0.1,port=55864,localport=55861], receiveBufferSize:81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | accepted Socket[addr=/127.0.0.1,port=55864,localport=55861], receiveBufferSize:81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | proxy connection Socket[addr=localhost/127.0.0.1,port=61617,localport=55865], receiveBufferSize=81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | Network connection between vm://BrokerA#0 and HTTP Reader http://localhost:55861(BrokerB) has been established. | org.apache.activemq.network.DemandForwardingBridgeSupport | StartLocalBridge: localBroker=vm://BrokerA#0
WAITING for REMOTE end ...
REMOTE BROKER IS UP, BRIDGE ESTABLISHED
=========================
Sending messages to Remote broker
DEBUG | A receive is in progress | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
BEGIN SIMULATE STALLED NETWORK
=========================
INFO  | pause, numConnectons=2 | org.apache.activemq.util.SocketProxy | main
DEBUG | 30000 ms elapsed since last read check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | A send is in progress | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | A receive is in progress | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | A send is in progress | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | A send is in progress | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
WARN  | Caught an exception processing local command | org.apache.activemq.network.DemandForwardingBridgeSupport | BrokerService[BrokerA] Task
java.io.IOException: Could not post command: ActiveMQTextMessage {commandId = 1007, responseRequired = true, messageId = ID:scranton-55860-1297451870356-5:1:1:1:1002, originalDestination = null, originalTransactionId = null, producerId = ID:scranton-55860-1297451870356-4:1:1:1, destination = queue://TEST.FOO, transactionId = null, expiration = 0, timestamp = 1297451924671, arrival = 0, brokerInTime = 1297451924671, brokerOutTime = 1297451924671, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@669d2f26, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 1116, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, text = BrokerA:-55860-1297451870356-0:1: Message-1001} due to: java.net.SocketTimeoutException: Read timed out
	at org.apache.activemq.util.IOExceptionSupport.create(IOExceptionSupport.java:33)
	at org.apache.activemq.transport.http.HttpClientTransport.oneway(HttpClientTransport.java:101)
	at org.apache.activemq.transport.InactivityMonitor.oneway(InactivityMonitor.java:254)
	at org.apache.activemq.transport.MutexTransport.oneway(MutexTransport.java:40)
	at org.apache.activemq.transport.ResponseCorrelator.asyncRequest(ResponseCorrelator.java:81)
	at org.apache.activemq.network.DemandForwardingBridgeSupport.serviceLocalCommand(DemandForwardingBridgeSupport.java:768)
	at org.apache.activemq.network.DemandForwardingBridgeSupport$1.onCommand(DemandForwardingBridgeSupport.java:160)
	at org.apache.activemq.transport.ResponseCorrelator.onCommand(ResponseCorrelator.java:116)
	at org.apache.activemq.transport.TransportFilter.onCommand(TransportFilter.java:69)
	at org.apache.activemq.transport.vm.VMTransport.dispatch(VMTransport.java:121)
	at org.apache.activemq.transport.vm.VMTransport.oneway(VMTransport.java:112)
	at org.apache.activemq.transport.MutexTransport.oneway(MutexTransport.java:40)
	at org.apache.activemq.transport.ResponseCorrelator.oneway(ResponseCorrelator.java:60)
	at org.apache.activemq.broker.TransportConnection.dispatch(TransportConnection.java:1250)
	at org.apache.activemq.broker.TransportConnection.processDispatch(TransportConnection.java:809)
	at org.apache.activemq.broker.TransportConnection.iterate(TransportConnection.java:845)
	at org.apache.activemq.thread.PooledTaskRunner.runTask(PooledTaskRunner.java:122)
	at org.apache.activemq.thread.PooledTaskRunner$1.run(PooledTaskRunner.java:43)
	at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908)
	at java.lang.Thread.run(Thread.java:680)
Caused by: java.net.SocketTimeoutException: Read timed out
	at java.net.SocketInputStream.socketRead0(Native Method)
	at java.net.SocketInputStream.read(SocketInputStream.java:129)
	at java.io.BufferedInputStream.fill(BufferedInputStream.java:218)
	at java.io.BufferedInputStream.read(BufferedInputStream.java:237)
	at org.apache.commons.httpclient.HttpParser.readRawLine(HttpParser.java:78)
	at org.apache.commons.httpclient.HttpParser.readLine(HttpParser.java:106)
	at org.apache.commons.httpclient.HttpConnection.readLine(HttpConnection.java:1116)
	at org.apache.commons.httpclient.HttpMethodBase.readStatusLine(HttpMethodBase.java:1973)
	at org.apache.commons.httpclient.HttpMethodBase.readResponse(HttpMethodBase.java:1735)
	at org.apache.commons.httpclient.HttpMethodBase.execute(HttpMethodBase.java:1098)
	at org.apache.commons.httpclient.HttpMethodDirector.executeWithRetry(HttpMethodDirector.java:398)
	at org.apache.commons.httpclient.HttpMethodDirector.executeMethod(HttpMethodDirector.java:171)
	at org.apache.commons.httpclient.HttpClient.executeMethod(HttpClient.java:397)
	at org.apache.commons.httpclient.HttpClient.executeMethod(HttpClient.java:323)
	at org.apache.activemq.transport.http.HttpClientTransport.oneway(HttpClientTransport.java:89)
	... 19 more
INFO  | Network connection between vm://BrokerA#0 and HTTP Reader http://localhost:55861 shutdown due to a local error: java.io.IOException: Could not post command: ActiveMQTextMessage {commandId = 1007, responseRequired = true, messageId = ID:scranton-55860-1297451870356-5:1:1:1:1002, originalDestination = null, originalTransactionId = null, producerId = ID:scranton-55860-1297451870356-4:1:1:1, destination = queue://TEST.FOO, transactionId = null, expiration = 0, timestamp = 1297451924671, arrival = 0, brokerInTime = 1297451924671, brokerOutTime = 1297451924671, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@669d2f26, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 1116, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, text = BrokerA:-55860-1297451870356-0:1: Message-1001} due to: java.net.SocketTimeoutException: Read timed out | org.apache.activemq.network.DemandForwardingBridgeSupport | BrokerService[BrokerA] Task
INFO  | Establishing network connection from vm://BrokerA?async=false&network=true to http://localhost:55861 | org.apache.activemq.network.DiscoveryNetworkConnector | ActiveMQ Task
DEBUG | HTTP GET consumer thread starting: HTTP Reader http://localhost:55861 | org.apache.activemq.transport.http.HttpClientTransport | ActiveMQ Task
INFO  | BrokerA bridge to BrokerB stopped | org.apache.activemq.network.DemandForwardingBridgeSupport | ActiveMQ Task
RESTART STALLED NETWORK
=========================
INFO  | goOn, numConnectons=2 | org.apache.activemq.util.SocketProxy | main
INFO  | accepted Socket[addr=/127.0.0.1,port=55876,localport=55861], receiveBufferSize:538956 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | accepted Socket[addr=/127.0.0.1,port=55876,localport=55861], receiveBufferSize:538956 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | proxy connection Socket[addr=localhost/127.0.0.1,port=61617,localport=55878], receiveBufferSize=538956 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | accepted Socket[addr=/127.0.0.1,port=55875,localport=55861], receiveBufferSize:538956 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | accepted Socket[addr=/127.0.0.1,port=55875,localport=55861], receiveBufferSize:538956 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | proxy connection Socket[addr=localhost/127.0.0.1,port=61617,localport=55879], receiveBufferSize=538956 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
DEBUG | HTTP GET consumer thread starting: HTTP Reader http://localhost:55861 | org.apache.activemq.transport.http.HttpClientTransport | ActiveMQ Transport: HTTP Reader http://localhost:55861
INFO  | accepted Socket[addr=/127.0.0.1,port=55880,localport=55861], receiveBufferSize:81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | accepted Socket[addr=/127.0.0.1,port=55880,localport=55861], receiveBufferSize:81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | proxy connection Socket[addr=localhost/127.0.0.1,port=61617,localport=55881], receiveBufferSize=81660 | org.apache.activemq.util.SocketProxy | SocketProxy-Acceptor-55861
INFO  | Network connection between vm://BrokerA#4 and HTTP Reader http://localhost:55861(BrokerB) has been established. | org.apache.activemq.network.DemandForwardingBridgeSupport | StartLocalBridge: localBroker=vm://BrokerA#4
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | A receive is in progress | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
Send completed .. waiting for END from remote broker
INFO  | Waiting for 1 message(s) to arrive | org.apache.activemq.util.MessageIdList | main
INFO  | End of wait for 26 millis and received: 1 messages | org.apache.activemq.util.MessageIdList | main
BROKER A (initiator is shutting down)
=========================
INFO  | ActiveMQ Message Broker (BrokerA, ID:scranton-55860-1297451870356-1:1) is shutting down | org.apache.activemq.broker.BrokerService | main
Exception in thread "ActiveMQ Transport: HTTP Reader http://localhost:55861" java.lang.IllegalStateException: Method has been aborted
	at org.apache.commons.httpclient.HttpMethodBase.checkExecuteConditions(HttpMethodBase.java:1055)
	at org.apache.commons.httpclient.HttpMethodBase.execute(HttpMethodBase.java:1085)
	at org.apache.commons.httpclient.HttpMethodDirector.executeWithRetry(HttpMethodDirector.java:398)
	at org.apache.commons.httpclient.HttpMethodDirector.executeMethod(HttpMethodDirector.java:171)
	at org.apache.commons.httpclient.HttpClient.executeMethod(HttpClient.java:397)
	at org.apache.commons.httpclient.HttpClient.executeMethod(HttpClient.java:323)
	at org.apache.activemq.transport.http.HttpClientTransport.run(HttpClientTransport.java:124)
	at java.lang.Thread.run(Thread.java:680)
DEBUG | Shutting down VM connectors for broker: BrokerA | org.apache.activemq.transport.vm.VMTransportFactory | main
INFO  | Connector vm://BrokerA Stopped | org.apache.activemq.broker.TransportConnector | main
INFO  | BrokerA bridge to BrokerB stopped | org.apache.activemq.network.DemandForwardingBridgeSupport | main
INFO  | Network Connector localhost Stopped | org.apache.activemq.network.NetworkConnector | main
WARN  | Caught an exception processing local command | org.apache.activemq.network.DemandForwardingBridgeSupport | BrokerService[BrokerA] Task
org.apache.activemq.transport.TransportDisposedIOException: Transport disposed.
	at org.apache.activemq.transport.vm.VMTransport.oneway(VMTransport.java:76)
	at org.apache.activemq.transport.MutexTransport.oneway(MutexTransport.java:40)
	at org.apache.activemq.transport.ResponseCorrelator.oneway(ResponseCorrelator.java:60)
	at org.apache.activemq.network.DemandForwardingBridgeSupport.serviceLocalCommand(DemandForwardingBridgeSupport.java:737)
	at org.apache.activemq.network.DemandForwardingBridgeSupport$1.onCommand(DemandForwardingBridgeSupport.java:160)
	at org.apache.activemq.transport.ResponseCorrelator.onCommand(ResponseCorrelator.java:116)
	at org.apache.activemq.transport.TransportFilter.onCommand(TransportFilter.java:69)
	at org.apache.activemq.transport.vm.VMTransport.dispatch(VMTransport.java:121)
	at org.apache.activemq.transport.vm.VMTransport.oneway(VMTransport.java:112)
	at org.apache.activemq.transport.MutexTransport.oneway(MutexTransport.java:40)
	at org.apache.activemq.transport.ResponseCorrelator.oneway(ResponseCorrelator.java:60)
	at org.apache.activemq.broker.TransportConnection.dispatch(TransportConnection.java:1250)
	at org.apache.activemq.broker.TransportConnection.processDispatch(TransportConnection.java:809)
	at org.apache.activemq.broker.TransportConnection.iterate(TransportConnection.java:845)
	at org.apache.activemq.thread.PooledTaskRunner.runTask(PooledTaskRunner.java:122)
	at org.apache.activemq.thread.PooledTaskRunner$1.run(PooledTaskRunner.java:43)
	at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908)
	at java.lang.Thread.run(Thread.java:680)
INFO  | Connector http://0.0.0.0:61616?initialDelayTime=14000&keepAliveResponseRequired=true&readCheckTime=20000 Stopped | org.apache.activemq.broker.TransportConnector | main
INFO  | PListStore:target/activemq-data/BrokerA/tmp_storage stopped | org.apache.activemq.store.kahadb.plist.PListStore | main
INFO  | Stopping async queue tasks | org.apache.activemq.store.kahadb.KahaDBStore | main
INFO  | Stopping async topic tasks | org.apache.activemq.store.kahadb.KahaDBStore | main
INFO  | Stopped KahaDB | org.apache.activemq.store.kahadb.KahaDBStore | main
INFO  | ActiveMQ JMS Message Broker (BrokerA, ID:scranton-55860-1297451870356-1:1) stopped | org.apache.activemq.broker.BrokerService | main
INFO  | Stopping auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
INFO  | Stopping auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 141.13 sec

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2:24.091s
[INFO] Finished at: Fri Feb 11 14:20:11 EST 2011
[INFO] Final Memory: 8M/81M
[INFO] ------------------------------------------------------------------------


Sample Output Broker B
======================

scranton:testcase_01_24_11$ mvn -Dtest=BrokerBQueueHttpNetworkWithDisconnectTest test
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building Broker HTTP Proxy Test 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.4.3:resources (default-resources) @ http-reconnect-test ---
[WARNING] Using platform encoding (MacRoman actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /Users/scranton/workspaces/testcase_01_24_11/src/main/resources
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ http-reconnect-test ---
[INFO] No sources to compile
[INFO]
[INFO] --- maven-resources-plugin:2.4.3:testResources (default-testResources) @ http-reconnect-test ---
[WARNING] Using platform encoding (MacRoman actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 1 resource
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ http-reconnect-test ---
[WARNING] File encoding has not been set, using platform encoding MacRoman, i.e. build is platform dependent!
[INFO] Compiling 3 source files to /Users/scranton/workspaces/testcase_01_24_11/target/test-classes
[INFO]
[INFO] --- maven-surefire-plugin:2.7.1:test (default-test) @ http-reconnect-test ---
[INFO] Surefire report directory: /Users/scranton/workspaces/testcase_01_24_11/target/surefire-reports

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.apache.activemq.bugs.BrokerBQueueHttpNetworkWithDisconnectTest
INFO  | Could not set field 'useDuplexNetworkBridge' to value 'true', make sure the field exists and is public. | org.apache.activemq.CombinationTestSupport | main
INFO  | Could not set field 'simulateStalledNetwork' to value 'true', make sure the field exists and is public. | org.apache.activemq.CombinationTestSupport | main
INFO  | Starting auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
INFO  | Starting auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
TEST TRANSPORT DISCONNECT (BROKERB)
=========================
INFO  | PListStore:target/activemq-data/BrokerB/tmp_storage started | org.apache.activemq.store.kahadb.plist.PListStore | main
INFO  | Using Persistence Adapter: KahaDBPersistenceAdapter[target/activemq-data/BrokerB/KahaDB] | org.apache.activemq.broker.BrokerService | main
INFO  | Persistence store purged. | org.apache.activemq.store.kahadb.MessageDatabase | main
INFO  | ActiveMQ 5.4.2-fuse-01-00 JMS Message Broker (BrokerB) is starting | org.apache.activemq.broker.BrokerService | main
INFO  | For help or more information please see: http://activemq.apache.org/ | org.apache.activemq.broker.BrokerService | main
2011-02-11 14:17:44.498:INFO::jetty-7.1.6.v20100715
2011-02-11 14:17:44.604:INFO::Started SocketConnector@0.0.0.0:61617
INFO  | Connector http://0.0.0.0:61617?initialDelayTime=4000&keepAliveResponseRequired=true&readCheckTime=20000 Started | org.apache.activemq.broker.TransportConnector | main
INFO  | ActiveMQ JMS Message Broker (BrokerB, ID:scranton-55856-1297451864135-1:1) started | org.apache.activemq.broker.BrokerService | main
DEBUG | binding to broker: BrokerB | org.apache.activemq.transport.vm.VMTransportFactory | main
INFO  | Connector vm://BrokerB Started | org.apache.activemq.broker.TransportConnector | main
INFO  | Network connection between vm://BrokerB#2 and org.apache.activemq.transport.http.BlockingQueueTransport@6ec5122f(BrokerA) has been established. | org.apache.activemq.network.DemandForwardingBridgeSupport | StartLocalBridge: localBroker=vm://BrokerB#2
INFO  | Created Duplex Bridge back to BrokerA | org.apache.activemq.broker.TransportConnection | qtp1328245897-19
REMOTE BROKER IS UP, BRIDGE ESTABLISHED
=========================
INFO  | Waiting for 2000 message(s) to arrive | org.apache.activemq.util.MessageIdList | main
DEBUG | Message received since last read check, resetting flag:  | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 30000 ms elapsed since last read check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | Message received since last read check, resetting flag:  | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | No message sent since last write check, sending a KeepAliveInfo | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
INFO  | Stopping network bridge on shutdown of remote broker | org.apache.activemq.network.DemandForwardingBridgeSupport | qtp1328245897-21
WARN  | Network connection between vm://BrokerB#2 and org.apache.activemq.transport.http.BlockingQueueTransport@6ec5122f shutdown due to a remote error: java.io.IOException: ShutdownInfo {commandId = 1008, responseRequired = false} | org.apache.activemq.network.DemandForwardingBridgeSupport | qtp1328245897-21
INFO  | BrokerB bridge to BrokerA stopped | org.apache.activemq.network.DemandForwardingBridgeSupport | ActiveMQ Task
INFO  | Created Duplex Bridge back to BrokerA | org.apache.activemq.broker.TransportConnection | qtp1328245897-22
INFO  | Network connection between vm://BrokerB#4 and org.apache.activemq.transport.http.BlockingQueueTransport@c303a60(BrokerA) has been established. | org.apache.activemq.network.DemandForwardingBridgeSupport | StartLocalBridge: localBroker=vm://BrokerB#4
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message received since last read check, resetting flag:  | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor ReadCheck
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
INFO  | End of wait for 114588 millis and received: 2000 messages | org.apache.activemq.util.MessageIdList | main
Sending END to Remote broker
DEBUG | 10000 ms elapsed since last write check. | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
DEBUG | Message sent since last write check, resetting flag | org.apache.activemq.transport.InactivityMonitor | InactivityMonitor WriteCheck
TEST TRANSPORT DISCONNECT COMPLETE
=========================
INFO  | ActiveMQ Message Broker (BrokerB, ID:scranton-55856-1297451864135-1:1) is shutting down | org.apache.activemq.broker.BrokerService | main
DEBUG | Shutting down VM connectors for broker: BrokerB | org.apache.activemq.transport.vm.VMTransportFactory | ActiveMQ Task
INFO  | Connector vm://BrokerB Stopped | org.apache.activemq.broker.TransportConnector | ActiveMQ Task
INFO  | BrokerB bridge to BrokerA stopped | org.apache.activemq.network.DemandForwardingBridgeSupport | ActiveMQ Task
INFO  | Connector http://0.0.0.0:61617?initialDelayTime=4000&keepAliveResponseRequired=true&readCheckTime=20000 Stopped | org.apache.activemq.broker.TransportConnector | main
INFO  | PListStore:target/activemq-data/BrokerB/tmp_storage stopped | org.apache.activemq.store.kahadb.plist.PListStore | main
INFO  | Stopping async queue tasks | org.apache.activemq.store.kahadb.KahaDBStore | main
INFO  | Stopping async topic tasks | org.apache.activemq.store.kahadb.KahaDBStore | main
INFO  | Stopped KahaDB | org.apache.activemq.store.kahadb.KahaDBStore | main
INFO  | ActiveMQ JMS Message Broker (BrokerB, ID:scranton-55856-1297451864135-1:1) stopped | org.apache.activemq.broker.BrokerService | main
INFO  | Stopping auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
INFO  | Stopping auto fail thread... | org.apache.activemq.AutoFailTestSupport | main
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 150.208 sec

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2:34.227s
[INFO] Finished at: Fri Feb 11 14:20:14 EST 2011
[INFO] Final Memory: 17M/81M
[INFO] ------------------------------------------------------------------------



