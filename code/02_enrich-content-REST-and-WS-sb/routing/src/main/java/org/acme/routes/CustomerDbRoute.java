package org.acme.routes;

import org.acme.config.CustomerDbRouteProperties;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerDbRoute extends RouteBuilder {
    @Autowired
    private CustomerDbRouteProperties customerDbRouteProperties;

    @Override
    public void configure() {
        onException(Exception.class)
                .to("log:onException")
                .handled(true)
                .transform(constant("Exception thrown. Stop route"));

        from("direct:insertDb").routeId("customer-insert-db")
            .to("log:picked-up-db")
            .to("sql:INSERT INTO USECASE.T_ACCOUNT(CLIENT_ID,SALES_CONTACT,COMPANY_NAME,COMPANY_GEO,COMPANY_ACTIVE,CONTACT_FIRST_NAME,CONTACT_LAST_NAME,CONTACT_ADDRESS," +
                    "CONTACT_CITY,CONTACT_STATE,CONTACT_ZIP,CONTACT_PHONE,CREATION_DATE,CREATION_USER) " +
                    "VALUES " +
                    "(:#CLIENT_ID,:#SALES_CONTACT,:#COMPANY_NAME,:#COMPANY_GEO,:#COMPANY_ACTIVE,:#CONTACT_FIRST_NAME,:#CONTACT_LAST_NAME,:#CONTACT_ADDRESS,:#CONTACT_CITY," +
                    ":#CONTACT_STATE,:#CONTACT_ZIP,:#CONTACT_PHONE,:#CREATION_DATE,:#CREATION_USER);");
    }
}
