package org.acme.routes.rest;

import org.acme.config.rest.CustomerRestServerRouteProperties;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerRestServerRoute extends RouteBuilder {

    @Autowired
    private CustomerRestServerRouteProperties customerRestServerRouteProperties;

    @Override
    public void configure () throws Exception {
        from(customerRestServerRouteProperties.getInput()).to("log:found-rest-message");
    }
}
