package org.fuse.usecase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.util.FileUtil;

import javax.jms.ConnectionFactory;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class HelperTest {

        private static AtomicInteger counter = new AtomicInteger(0);

        private HelperTest() {
        }

        public static PooledConnectionFactory createPooledConnectionFactory() {
            ConnectionFactory cf = createConnectionFactory(null, null);
            PooledConnectionFactory pooled = new PooledConnectionFactory();
            pooled.setConnectionFactory(cf);
            pooled.setMaxConnections(8);
            return pooled;
        }

        public static ConnectionFactory createConnectionFactory() {
            return createConnectionFactory(null, null);
        }

        public static ConnectionFactory createConnectionFactory(String options, Integer maximumRedeliveries) {
            // using a unique broker name improves testing when running the entire test suite in the same JVM
            int id = counter.incrementAndGet();
            String url = "vm://test-broker-" + id + "?broker.persistent=false&broker.useJmx=false";
            if (options != null) {
                url = url + "&" + options;
            }
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            // optimize AMQ to be as fast as possible so unit testing is quicker
            connectionFactory.setCopyMessageOnSend(false);
            connectionFactory.setOptimizeAcknowledge(true);
            connectionFactory.setOptimizedMessageDispatch(true);
            // When using asyncSend, producers will not be guaranteed to send in the order we
            // have in the tests (which may be confusing for queues) so we need this set to false.
            // Another way of guaranteeing order is to use persistent messages or transactions.
            connectionFactory.setUseAsyncSend(false);
            connectionFactory.setAlwaysSessionAsync(false);
            if (maximumRedeliveries != null) {
                connectionFactory.getRedeliveryPolicy().setMaximumRedeliveries(maximumRedeliveries);
            }
            return connectionFactory;
        }

        public static ConnectionFactory createPersistentConnectionFactory() {
            return createPersistentConnectionFactory(null);
        }

        public static ConnectionFactory createPersistentConnectionFactory(String options) {
            // using a unique broker name improves testing when running the entire test suite in the same JVM
            int id = counter.incrementAndGet();

            // use an unique data directory in target
            String dir = "target/activemq-data-" + id;

            // remove dir so its empty on startup
            FileUtil.removeDir(new File(dir));

            String url = "vm://test-broker-" + id + "?broker.persistent=true&broker.useJmx=false&broker.dataDirectory=" + dir;
            if (options != null) {
                url = url + "&" + options;
            }
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            // optimize AMQ to be as fast as possible so unit testing is quicker
            connectionFactory.setCopyMessageOnSend(false);
            connectionFactory.setOptimizeAcknowledge(true);
            connectionFactory.setOptimizedMessageDispatch(true);
            connectionFactory.setAlwaysSessionAsync(false);
            return connectionFactory;
        }
}
