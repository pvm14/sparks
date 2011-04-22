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
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.util.MessageIdList;
import org.apache.activemq.util.SocketProxy;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import java.net.URI;
import java.util.List;


public class BrokerQueueHttpNetworkWithDisconnectTest extends JmsMultipleBrokersTestSupport {
    private static final String HUB = "HubBroker";
    private static final String SPOKE = "SpokeBroker";

    private static final int NETWORK_DOWN_TIME = 35000;
    private static final int MESSAGE_COUNT = 200;

    private SocketProxy socketProxy;
    private long networkDownTimeStart;

    public boolean useDuplexNetworkBridge;
    public boolean simulateStalledNetwork;
    public int socketTimeout;

    private static final String BROKER_OPTIONS = "?persistent=true&useJmx=false&deleteAllMessagesOnStartup=true";

    public void initCombosForTestSendOnAReceiveOnBWithTransportDisconnect() {
        addCombinationValues("useDuplexNetworkBridge", new Object[]{Boolean.TRUE});
        addCombinationValues("simulateStalledNetwork", new Object[]{Boolean.TRUE, Boolean.FALSE});
        addCombinationValues("socketTimeout", new Object[]{1000, 30000});
    }

    public void testSendOnAReceiveOnBWithTransportDisconnect() throws Exception {
        System.out.println("TEST TRANSPORT DISCONNECT");
        System.out.println("=========================");

        bridgeBrokers(SPOKE, HUB);

        startAllBrokers();

        // Setup destination
        Destination dest = createDestination("TEST.FOO", false);

        // Setup consumers
        MessageConsumer client = createConsumer(HUB, dest);

        // allow subscription information to flow back to Spoke
        sleep(600);

        // Send messages
        sendMessages(SPOKE, dest, MESSAGE_COUNT);

        MessageIdList msgs = getConsumerMessages(HUB, client);

        // We have a 2x35 second stall
        msgs.setMaximumDuration(120000L);
        msgs.waitForMessagesToArrive(MESSAGE_COUNT);

        assertTrue("At least message " + MESSAGE_COUNT +
                " must be received, duplicates are expected, count=" + msgs.getMessageCount(),
                MESSAGE_COUNT <= msgs.getMessageCount());

        System.out.println("TEST TRANSPORT DISCONNECT COMPLETE");
        System.out.println("=========================");
    }

    @Override
    protected void startAllBrokers() throws Exception {
        // Ensure HUB is started first so bridge will be active from the get go
        BrokerItem brokerItem = brokers.get(HUB);
        brokerItem.broker.start();

        brokerItem = brokers.get(SPOKE);
        brokerItem.broker.start();

        sleep(600);
    }

    protected String getInitiatorBindLocation() {
        return "http://localhost:61616";
    }

    protected String getTargetBindLocation() {
        return "http://localhost:61617";
    }

    @Override
    public void setUp() throws Exception {
        networkDownTimeStart = 0;

        super.setAutoFail(true);
        super.setUp();

        final String transportOptions = "?transport.initialDelayTime=4000&transport.keepAliveResponseRequired=true&transport.readCheckTime=20000&transport.soTimeout=" + socketTimeout;

        BrokerService hubBroker = createBroker(new URI("broker:()/" + HUB + BROKER_OPTIONS));
        hubBroker.setDataDirectory("target/activemq-data");
        hubBroker.addConnector(getInitiatorBindLocation() + transportOptions);

        BrokerService spokeBroker = createBroker(new URI("broker:()/" + SPOKE + BROKER_OPTIONS));
        spokeBroker.setDataDirectory("target/activemq-data");
        //spokeBroker.addConnector(getTargetBindLocation() + transportOptions);
    }

    public static Test suite() {
        return suite(BrokerQueueHttpNetworkWithDisconnectTest.class);
    }

    @Override
    protected void onSend(int i, TextMessage msg) {
        sleep(50);

        if (i == 10 || i == 60) {
            if (simulateStalledNetwork) {
                System.out.println("BEGIN SIMULATE STALLED NETWORK");
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
            if (networkDownTimeStart + NETWORK_DOWN_TIME < System.currentTimeMillis()) {
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
        List<TransportConnector> transportConnectors = remoteBroker.getTransportConnectors();
        URI remoteURI;
        if (!transportConnectors.isEmpty()) {
            remoteURI = ((TransportConnector) transportConnectors.get(0)).getConnectUri();
            System.out.println("remoteUri: " + remoteURI);
            socketProxy = new SocketProxy(remoteURI);

            final String networkConnectorOptions = "?initialDelayTime=4000&keepAliveResponseRequired=true&readCheckTime=20000&soTimeout=" + socketTimeout;

            final URI remoteUrl = socketProxy.getUrl();
        
            DiscoveryNetworkConnector connector = new DiscoveryNetworkConnector(new URI("static:(http://" + remoteUrl.getHost() + ":" + remoteUrl.getPort() + networkConnectorOptions + ")?useExponentialBackOff=false"));
            connector.setDynamicOnly(dynamicOnly);
            connector.setNetworkTTL(networkTTL);
            localBroker.addNetworkConnector(connector);

            maxSetupTime = 2000;

            if (useDuplexNetworkBridge) {
                connector.setDuplex(true);
            }

            return connector;
        } else {
            throw new Exception("Remote broker has no registered connectors.");
        }
    }

    private void sleep(int milliSecondTime) {
        try {
            Thread.sleep(milliSecondTime);
        } catch (InterruptedException ignored) {
        }
    }

}
