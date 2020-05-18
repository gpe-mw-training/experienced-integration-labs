package org.fuse.usecase;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.http.RestNettyHttpBinding;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RestNetty4HttpGetTest extends BaseNetty4Test {

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("mybinding", new RestNettyHttpBinding());
        return jndi;
    }

    @Test
    public void testProducerGet() throws Exception {
        String out = template.requestBody("netty4-http:http://localhost:{{port}}/users/123/basic", null, String.class);
        assertEquals("123;Donald Duck", out);
    }

    @Test
    public void testConcurrentProducers() throws Exception {
        doSendMessages(20, 5);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            /* Issue with maxPoolSize = https://issues.apache.org/jira/browse/CAMEL-8031 */

            @Override
            public void configure() throws Exception {
                // configure to use netty4-http on localhost with the given port
                restConfiguration()
                     .component("netty4-http")
                     .host("localhost").port(getPort())
                     .componentProperty("nettyHttpBinding", "#mybinding")
                     .componentProperty("maximumPoolSize","2");

                // use the rest DSL to define the rest services
                rest("/users/")
                        .get("{id}/basic")
                        .route()
                        .log(">> Thread name : ${threadName}")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                String id = exchange.getIn().getHeader("id", String.class);
                                exchange.getOut().setBody(id + ";Donald Duck");
                            }
                        });
            }
        };
    }

    private void doSendMessages(int files, int poolSize) throws Exception {
        //getMockEndpoint("mock:result").expectedMessageCount(files);
        //getMockEndpoint("mock:result").assertNoDuplicates(body());

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        // we access the responses Map below only inside the main thread,
        // so no need for a thread-safe Map implementation
        Map<Integer, Future<String>> responses = new HashMap<Integer, Future<String>>();
        for (int i = 0; i < files; i++) {
            final int index = i;
            Future<String> out = executor.submit(new Callable<String>() {
                public String call() throws Exception {
                    return template.requestBody("netty4-http:http://localhost:{{port}}/users/" + index + "/basic", null, String.class);
                }
            });
            responses.put(index, out);
        }

        //assertMockEndpointsSatisfied();

        assertEquals(files, responses.size());

        // get all responses
        Set<String> unique = new HashSet<String>();
        for (Future<String> future : responses.values()) {
            unique.add(future.get());
        }

        // should be 'files' unique responses
        assertEquals("Should be " + files + " unique responses", files, unique.size());
        executor.shutdownNow();
    }

}