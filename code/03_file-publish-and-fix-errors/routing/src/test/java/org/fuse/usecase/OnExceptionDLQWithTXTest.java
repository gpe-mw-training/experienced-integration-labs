package org.fuse.usecase;

import org.acme.Customer;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.jms.ConnectionFactory;
import java.util.List;

import static org.apache.camel.component.jms.JmsComponent.jmsComponentTransacted;

public class OnExceptionDLQWithTXTest extends CamelTestSupport {

    private final static String records = "Robocops,NA,true,Bill,Smith,100 N Park Ave.,Phoenix,AZ,85017,200-555-1000\n" +
            "MountainBikers,SA,true,George,Jungle,1101 Smith St.,Raleigh,NC,27519,600-555-7000\n" +
            "MicroservicesVision,WA,true,Fred,Quicksand,202 Barney Blvd.,Rock City,MI,19728,100-400-2000\n" +
            "Error,,,EU,true,Fred,Quicksand,202 Barney Blvd.,Rock City,MI,19728,900-000-4545";

    private final static String errorRecord = "Error,,,EU,true,Fred,Quicksand,202 Barney Blvd.,Rock City,MI,19728,900-000-4545";

    private static final String componentName = "activemq";
    private static String queueInputEndpoint = "activemq:input";
    private final String mockErrorEndpoint = "mock:error";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                DataFormat bindyDF = new BindyCsvDataFormat(Customer.class);

                // we attempt to handle the exception and to send the message to our queue
                onException(Exception.class)
                   .handled(false)
                   .setHeader("error-code").constant(111)
                   .setHeader("error-message").simple("exception.message")
                   .setBody().simple("${body}")
                   .to("direct:error");

                from(queueInputEndpoint)
                   .split().tokenize("\n")
                      .log("Incoming JMS message ${body}")
                      .unmarshal(bindyDF)
                      .log("Message marshalled : ${body}")
                      .to("activemq:output");

                from("activemq:output")
                   .log("Message received ${body}");

                from("direct:error")
                   .log(">> Direct endpoint called")
                   .to(mockErrorEndpoint);
            }
        };
    }

    @Test
    public void shouldGetMessageWithinDLQ() throws Exception {
        template.sendBody(queueInputEndpoint, records);

        // Set the expectations for the Mock endpoint
        MockEndpoint mock = getMockEndpoint(mockErrorEndpoint);
        // We should get an error message
        mock.expectedMessageCount(1);

        mock.assertIsSatisfied();

        assertEquals(1,mock.getExpectedCount());

        // We will verify that the header received correspond to our expectations
        List<Exchange> exchanges = mock.getExchanges();
        Message msg = exchanges.get(0).getIn();
        assertEquals(111,msg.getHeader("error-code"));
        assertEquals("No position 11 defined for the field: 19728, line: 1 must be specified",msg.getHeader("error-message"));

        String dlqBody = (String) consumer.receiveBody("activemq:ActiveMQ.DLQ", 2000);
        assertEquals(records, dlqBody);
    }

    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        // 3 redeliveries
        ConnectionFactory connectionFactory = HelperTest.createConnectionFactory(null, 3);

        /* With Tx Manager & Transacted = True */
        JmsComponent component = jmsComponentTransacted(connectionFactory);
        camelContext.addComponent(componentName, component);
        return camelContext;
    }
}
