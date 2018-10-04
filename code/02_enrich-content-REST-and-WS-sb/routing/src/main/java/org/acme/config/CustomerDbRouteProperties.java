package org.acme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "org.acme.customer.db")
public class CustomerDbRouteProperties {

}
