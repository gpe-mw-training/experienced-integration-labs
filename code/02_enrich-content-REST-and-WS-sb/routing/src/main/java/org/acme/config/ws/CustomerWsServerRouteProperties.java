package org.acme.config.ws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "org.acme.customer.ws")
public class CustomerWsServerRouteProperties {
    private String input = "cxf:bean:customerWebService";

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    private String output = "direct:insertDb";

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
