package org.acme.routes;

import org.acme.config.CustomerLoadRouteProperties;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.fuse.usecase.AccountAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerLoadRoute extends RouteBuilder {

    @Autowired
    private CustomerLoadRouteProperties customerLoadRouteProperties;

    @Override
    public void configure () throws Exception {
        JacksonDataFormat format = new JacksonDataFormat(org.globex.Account.class);

        from(customerLoadRouteProperties.getInput()).routeId("customer-load")
            .to("log:loaded")
            .unmarshal(format)
            .to("log:unmarshalled")
            .multicast(new AccountAggregator())
            .to(customerLoadRouteProperties.getRestEndpoint(), customerLoadRouteProperties.getWsEndpoint()).end()
            .to("log:enriched")
            .to(customerLoadRouteProperties.getOutput());
    }
}
