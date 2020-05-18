package org.fuse.usecase;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.NettyComponent;
import org.apache.camel.component.netty4.NettyConfiguration;
import org.apache.camel.component.netty4.http.NettyHttpComponent;
import org.apache.camel.component.netty4.http.NettyHttpConfiguration;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Netty4HttpConcurrentTest extends BaseNetty4Test {

/*    @Test
    public void testNoConcurrentProducers() throws Exception {
        doSendMessages(1, 1);
    }*/

    @Test
    public void testConcurrentProducers() throws Exception {
        doSendMessages(10, 5);
    }

    private void doSendMessages(int files, int poolSize) throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(files);
        getMockEndpoint("mock:result").assertNoDuplicates(body());

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        Map<Integer, Future<String>> responses = new HashMap<Integer, Future<String>>();
        for (int i = 0; i < files; i++) {
            final int index = i;
            Future<String> out = executor.submit(new Callable<String>() {
                public String call() throws Exception {
                    return template.requestBody("netty4-http:http://localhost:{{port}}/echo", "" + index, String.class);
                }
            });
            responses.put(index, out);
        }

        assertMockEndpointsSatisfied();

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


    protected RouteBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {

            public void configure() throws Exception {

                /* Do to this Issue https://issues.apache.org/jira/browse/CAMEL-8031, the maximumPoolSize parameter must be defined
                 * at the componentLevel */
                NettyHttpConfiguration nettyConfig = new NettyHttpConfiguration();
                nettyConfig.setMaximumPoolSize(5);
                NettyHttpComponent nettyHttp = new NettyHttpComponent();
                nettyHttp.setConfiguration(nettyConfig);

                getContext().addComponent("netty4-http", nettyHttp);
                nettyHttp.start();

                // expose a echo service
                from("netty4-http:http://localhost:{{port}}/echo")
                   .log(">> Thread name : ${threadName}")
                   .transform(body().append(body())).to("mock:result");
            }
        };
    }

}
