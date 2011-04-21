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
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.util.MessageIdList;
import org.apache.activemq.util.Wait;

import javax.jms.MessageConsumer;
import java.net.URI;


public class BrokerBQueueHttpNetworkWithDisconnectTest extends JmsMultipleBrokersTestSupport {
    private static final String LOCAL_BROKER_NAME = "BrokerB";

    protected static final int MESSAGE_COUNT = 100;
    protected static final int ONE_MESSAGE = 1;

    public void testSendOnAReceiveOnBWithTransportDisconnect() throws Exception {
        System.out.println("TEST TRANSPORT DISCONNECT (" + LOCAL_BROKER_NAME + ")");
        System.out.println("=========================");

        startAllBrokers();

        // Setup destination, this come in over the Network Connector
        ActiveMQDestination destination = createDestination("TEST.FOO", false);
        MessageConsumer client = createConsumer(LOCAL_BROKER_NAME, destination);

        // allow subscription information to flow back to remote broker
        sleep(600);

        int testNum = 0;
        while (true) {
            final long start = System.currentTimeMillis();

            // We use this to sync with BrokerA
            waitForBridgeFormation();

            ++testNum;

            System.out.println("REMOTE BROKER IS UP, BRIDGE ESTABLISHED TEST #" + testNum);
            System.out.println("=========================");

            MessageIdList msgs = getConsumerMessages(LOCAL_BROKER_NAME, client);

            // We have a 2x35 second stall
            msgs.setMaximumDuration(120000L);
            msgs.waitForMessagesToArrive(MESSAGE_COUNT);

            assertTrue("At least message " + MESSAGE_COUNT +
                    " must be received, duplicates are expected, count=" + msgs.getMessageCount(),
                    MESSAGE_COUNT <= msgs.getMessageCount());

            System.out.println("Sending END to Remote broker");
            ActiveMQDestination end = createDestination("END", false);
            sendMessages(LOCAL_BROKER_NAME, end, ONE_MESSAGE);

            System.out.println("TEST TRANSPORT DISCONNECT COMPLETE TEST");

            System.out.println("TEST CLEANUP TEST #" + testNum + " Duration: " + (System.currentTimeMillis() - start) + "ms");
            System.out.println("=========================");

            System.out.println("[sleeping for 30 secs]");
            sleep(30000);

            msgs.flushMessages();
        }
    }

    @Override
    protected void startAllBrokers() throws Exception {
        BrokerItem brokerItem = brokers.get(LOCAL_BROKER_NAME);
        brokerItem.broker.start();
    }

    protected String getTargetBindLocation() {
        return "http://0.0.0.0:61617";
    }

    @Override
    public void setUp() throws Exception {
        super.setAutoFail(false);
        super.setUp();

        final String brokerOptions = "?persistent=true&useJmx=false&deleteAllMessagesOnStartup=true";
        BrokerService broker = createBroker(new URI("broker:()/" + LOCAL_BROKER_NAME + brokerOptions));
        broker.setDataDirectory("target/activemq-data");

        final String transportOptions = "?transport.initialDelayTime=4000&transport.keepAliveResponseRequired=true&transport.readCheckTime=20000&transport.soTimeout=30000";

        broker.addConnector(getTargetBindLocation() + transportOptions);
    }

    public static Test suite() {
        return suite(BrokerBQueueHttpNetworkWithDisconnectTest.class);
    }

    @Override
    protected NetworkConnector bridgeBrokers(BrokerService localBroker, BrokerService remoteBroker, boolean dynamicOnly, int networkTTL, boolean conduit, boolean failover) throws Exception {
        // NOOP on this end
        return null;
    }

    @Override
    protected void waitForBridgeFormation() throws Exception {
        for (BrokerItem brokerItem : brokers.values()) {
            final BrokerService broker = brokerItem.broker;
            if (broker.getNetworkConnectors().isEmpty()) {
                Wait.waitFor(new Wait.Condition() {
                    public boolean isSatisified() throws Exception {
                        return (broker.getNetworkConnectors().size() >= 1);
                    }
                });
            }
        }
    }

    private void sleep(int milliSecondTime) {
        try {
            Thread.sleep(milliSecondTime);
        } catch (InterruptedException ignored) {
        }
    }
}
