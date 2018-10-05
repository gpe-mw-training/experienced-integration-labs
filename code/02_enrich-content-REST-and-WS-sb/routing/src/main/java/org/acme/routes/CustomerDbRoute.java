package org.acme.routes;

import org.acme.config.CustomerDbRouteProperties;
import org.apache.camel.builder.RouteBuilder;
import org.fuse.usecase.ProcessorBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerDbRoute extends RouteBuilder {
    @Autowired
    private CustomerDbRouteProperties customerDbRouteProperties;

    @Override
    public void configure() {
        from(customerDbRouteProperties.getInput()).routeId("customer-insert-db")
            .to("log:picked-up-db")
            .transform().method(ProcessorBean.class, "defineNamedParameters")
            .to(customerDbRouteProperties.getOutput())
            .to("log:inserted-db");
    }
}
