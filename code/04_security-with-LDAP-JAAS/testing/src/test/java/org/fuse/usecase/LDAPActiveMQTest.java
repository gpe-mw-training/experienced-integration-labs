package org.fuse.usecase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(FrameworkRunner.class) @CreateLdapServer(transports = {
        @CreateTransport(protocol = "LDAP", port = 1024) }) @ApplyLdifFiles("org/fuse/usecase/activemq.ldif") public class LDAPActiveMQTest
        extends AbstractLdapTestUnit {

    public BrokerService broker;
    public static LdapServer ldapServer;

    @Before public void setup() throws Exception {
        System.setProperty("ldapPort", String.valueOf(getLdapServer().getPort()));
        broker = BrokerFactory.createBroker("xbean:org/fuse/usecase/activemq-broker.xml");
        broker.start();
        broker.waitUntilStarted();
    }

    @After public void shutdown() throws Exception {
        broker.stop();
        broker.waitUntilStopped();
    }

    @Test public void testFailCreateSessionNotEnoughRight() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        try {
            Connection conn = factory.createQueueConnection("cibsen", "camel");
            Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            conn.start();
            fail("Expected Exception");
        } catch (Exception e) {
            assertEquals("User name [cibsen] or password is invalid.", e.getMessage());
            return;
        }
    }

    @Test public void testCreateQueuePublishConsume() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");

        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createQueue("usecase-input");

        MessageProducer producer = sess.createProducer(queue);
        MessageConsumer consumer = sess.createConsumer(queue);

        producer.send(sess.createTextMessage("test"));
        Message msg = consumer.receive(1000);
        assertNotNull(msg);
        conn.stop();
        sess.close();
    }

    @Test public void testFailCreateQueuePublishConsume() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        try {
            Connection conn = factory.createQueueConnection("jdoe", "sunflower");
            Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            conn.start();
            Queue queue = sess.createQueue("TEST.FOOOO");

            MessageProducer producer = sess.createProducer(queue);
            fail("Expected JMSException");
        } catch (Exception e) {
            assertEquals("User jdoe is not authorized to write to: queue://TEST.FOOOO", e.getMessage());
            return;
        }
    }

}