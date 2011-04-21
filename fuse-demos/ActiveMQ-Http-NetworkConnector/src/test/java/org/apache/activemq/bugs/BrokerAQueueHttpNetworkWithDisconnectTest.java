/*
 * Copyright 2011 FuseSource
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.activemq.bugs;

import junit.framework.Test;
import org.apache.activemq.JmsMultipleBrokersTestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.util.MessageIdList;
import org.apache.activemq.util.SocketProxy;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import java.net.URI;
import java.util.Random;


public class BrokerAQueueHttpNetworkWithDisconnectTest extends JmsMultipleBrokersTestSupport {
    private static final String LOCAL_BROKER_NAME = "BrokerA";

    private static final int NETWORK_DOWN_TIME = 35000;
    private static final int MESSAGE_COUNT = 100;

    private int stallAtMsgNum = 0;
    private SocketProxy socketProxy;
    private long networkDownTimeStart;

    public boolean useDuplexNetworkBridge;
    public boolean simulateStalledNetwork;

    public void initCombosForTestSendOnAReceiveOnBWithTransportDisconnect() {
        addCombinationValues("useDuplexNetworkBridge", new Object[]{Boolean.TRUE});
        addCombinationValues("simulateStalledNetwork", new Object[]{Boolean.TRUE});
    }

    public void testSendOnAReceiveOnBWithTransportDisconnect() throws Exception {
        System.out.println("TEST TRANSPORT DISCONNECT");
        System.out.println("=========================");

        // Bridge our embedded broker to a target broker via the socket proxy
        // Note: We are passing null for the remote broker. We will use getTargetBindLocation()
        // to figure out who we should be connecting to.
        BrokerService localBroker = brokers.get(LOCAL_BROKER_NAME).broker;
        bridgeBrokers(localBroker, null, false, 1, true, false);

        System.out.println("STARTING brokers ...");

        startAllBrokers();

        // Wait for indication that we got all msgs.
        // Once BrokerB gets all msgs, it will send an END msg to the END queue
        Destination end = createDestination("END", false);
        MessageConsumer client = createConsumer(LOCAL_BROKER_NAME, end);
        MessageIdList msgs = getConsumerMessages(LOCAL_BROKER_NAME, client);

        System.out.println("WAITING for REMOTE end ...");

        // allow subscription information to flow back to BrokerA
        sleep(600);

        int testNum = 0;
        final Random random = new Random(System.currentTimeMillis());

        while (true) {
            final long start = System.currentTimeMillis();

            // We use this to sync with remote broker
            waitForBridgeFormation();

            ++testNum;

            System.out.println("REMOTE BROKER IS UP, BRIDGE ESTABLISHED TEST #" + testNum);
            System.out.println("=========================");

            stallAtMsgNum = random.nextInt(MESSAGE_COUNT);
            System.out.println("Stall at MSG #" + stallAtMsgNum);

            // Setup destination, send msgs to remote broker via socket proxy
            System.out.println("Sending messages to Remote broker");
            Destination dest = createDestination("TEST.FOO", false);
            sendMessages(LOCAL_BROKER_NAME, dest, MESSAGE_COUNT);

            System.out.println("Send completed .. waiting for END from remote broker");

            if (networkDownTimeStart > 0) {
                System.out.println("Done sending but in downtime");
                System.out.println("RESTART STALLED NETWORK");
                System.out.println("=======================");

                socketProxy.goOn();
                networkDownTimeStart = 0;
            }

            // We have a 2x35 second stall + network latency in the test
            msgs.setMaximumDuration(120000L);
            msgs.waitForMessagesToArrive(1);

            assertTrue("End message from remote broker must be received, count=" + msgs.getMessageCount(),
                    1 <= msgs.getMessageCount());

            System.out.println("TEST CLEANUP TEST #" + testNum + " Duration: " + (System.currentTimeMillis() - start) + "ms");
            System.out.println("===================");

            System.out.println("[sleeping for 30 secs]");
            sleep(30000);

            msgs.flushMessages();

            System.out.println("NEXT SEND LOOP");
        }
    }

    @Override
    protected void startAllBrokers() throws Exception {
        // Ensure BrokerB is started first so bridge will be active from the get go
        BrokerItem brokerItem = brokers.get(LOCAL_BROKER_NAME);
        brokerItem.broker.start();
    }

    protected String getTargetBindLocation() {
        return "http://localhost:61617";
    }

    @Override
    public void setUp() throws Exception {
        networkDownTimeStart = 0;

        super.setAutoFail(false);
        super.setUp();

        final String brokerOptions = "?persistent=true&useJmx=false&deleteAllMessagesOnStartup=true";
        BrokerService broker = createBroker(new URI("broker:()/" + LOCAL_BROKER_NAME + brokerOptions));
        broker.setDataDirectory("target/activemq-data");
    }

    public static Test suite() {
        return suite(BrokerAQueueHttpNetworkWithDisconnectTest.class);
    }

    @Override
    protected void onSend(int i, TextMessage msg) {
        sleep(50);

        if (i == stallAtMsgNum) {
            if (simulateStalledNetwork) {
                System.out.println("BEGIN SIMULATE STALLED NETWORK at MSG #" + stallAtMsgNum);
                System.out.println("=========================");
                socketProxy.pause();
            } else {
                System.out.println("CLOSE SOCKET PROXY");
                System.out.println("====================");
                socketProxy.close();
            }

            networkDownTimeStart = System.currentTimeMillis();
        } else if (networkDownTimeStart > 0) {
            // restart after NETWORK_DOWN_TIME seconds
            if ((networkDownTimeStart + NETWORK_DOWN_TIME) < System.currentTimeMillis()) {
                if (simulateStalledNetwork) {
                    System.out.println("RESTART STALLED NETWORK");
                    System.out.println("=========================");
                    socketProxy.goOn();
                } else {
                    System.out.println("REOPEN SOCKET PROXY");
                    System.out.println("====================");
                    socketProxy.reopen();
                }

                networkDownTimeStart = 0;
            } else {
                // slow message production to allow bridge to recover and limit message duplication
                sleep(1000);
            }
        }

        super.onSend(i, msg);
    }

    @Override
    protected NetworkConnector bridgeBrokers(BrokerService localBroker, BrokerService remoteBroker, boolean dynamicOnly, int networkTTL, boolean conduit, boolean failover) throws Exception {
        URI remoteURI = URI.create(getTargetBindLocation());
        socketProxy = new SocketProxy(remoteURI);
        final URI url = socketProxy.getUrl();

        System.out.println("remoteUri: " + remoteURI);
        System.out.println("SOCKETPROXY URI: " + url);

        final String networkConnectorOptions = "?initialDelayTime=4000&keepAliveResponseRequired=true&readCheckTime=8000&soTimeout=10000";

		// This looks weird being hardcoded to localhost but its needed if running brokers on separate machines.. we are
		// connecting to the localsocket proxy which forwards to the remote broker..
		DiscoveryNetworkConnector connector = new DiscoveryNetworkConnector(new URI("static:(http://localhost:"  + url.getPort() + networkConnectorOptions + ")?useExponentialBackOff=false"));
        connector.setDynamicOnly(dynamicOnly);
        connector.setNetworkTTL(networkTTL);
        localBroker.addNetworkConnector(connector);

        maxSetupTime = 2000;

        if (useDuplexNetworkBridge) {
            connector.setDuplex(true);
        }

        return connector;
    }

    private void sleep(int milliSecondTime) {
        try {
            Thread.sleep(milliSecondTime);
        } catch (InterruptedException ignored) {
        }
    }
}
