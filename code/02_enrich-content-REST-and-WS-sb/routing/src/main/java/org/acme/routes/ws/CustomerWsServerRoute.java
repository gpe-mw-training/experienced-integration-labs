package org.acme.routes.ws;

import org.acme.config.ws.CustomerWsClientRouteProperties;
import org.acme.config.ws.CustomerWsServerRouteProperties;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CustomerWsServerRoute extends RouteBuilder {

    @Autowired
    private CustomerWsServerRouteProperties customerWsServerRouteProperties;

    @Override
    public void configure () throws Exception {
        onException(Exception.class)
                .to("log:onException")
                .handled(true)
                .transform(constant("Exception thrown. Stop route"));

        from(customerWsServerRouteProperties.getInput()).routeId("customer-ws-server")
            .beanRef("customerWSImplBean", "updateAccount")
            .to("log:ws-server-sent");
//            .to("direct:insertDb");
    }
}
