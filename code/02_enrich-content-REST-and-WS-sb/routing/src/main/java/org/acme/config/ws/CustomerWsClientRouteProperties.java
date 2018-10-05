package org.acme.config.ws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "org.acme.customer.ws")
public class CustomerWsClientRouteProperties {
    private String input = "direct:callWsEndpoint";

    private String output = "cxf:bean:customerWebService";

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
