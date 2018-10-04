package org.acme.routes.rest;

import org.acme.config.rest.CustomerRestClientRouteProperties;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerRestClientRoute extends RouteBuilder {

    @Autowired
    private CustomerRestClientRouteProperties customerRestClientRouteProperties;

    @Override
    public void configure () throws Exception {
        onException(Exception.class)
                .to("log:onException")
                .handled(true)
                .transform(constant("Exception thrown. Stop route"));

        from(customerRestClientRouteProperties.getInput()).routeId("customer-rest")
            .to("log:rest-picked-up")
            .process(new AccountHeaderProcessor())
            .inOut(customerRestClientRouteProperties.getOutput())
            .to("log:sent-to-rest");
//            .to("direct:insertDb");
    }

    class AccountHeaderProcessor implements Processor {
        public void process(Exchange exchange) {
            Message message = exchange.getIn();
            message.setHeader("Content-type", "application/json");
            message.setHeader("Accept", "application/json");
            message.setHeader("CamelHTTPMethod", "POST");
            message.setHeader("CamelHttpPath", "/customerservice/enrich");
            message.setHeader("CamelCxfRsUsingHttpAPI", "True");
        }
    }
}
