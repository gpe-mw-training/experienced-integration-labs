package org.acme.routes.ws;

import org.acme.config.ws.CustomerWsClientRouteProperties;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerWsClientRoute extends RouteBuilder {

    @Autowired
    private CustomerWsClientRouteProperties customerWsClientRouteProperties;

    @Override
    public void configure () throws Exception {
        from(customerWsClientRouteProperties.getInput()).routeId("customer-ws-client")
            .to("log:ws-picked-up")
            .to(customerWsClientRouteProperties.getOutput())
            .to("log:ws-response-received");
    }

}
