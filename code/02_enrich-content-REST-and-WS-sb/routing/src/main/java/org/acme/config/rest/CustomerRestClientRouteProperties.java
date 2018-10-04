package org.acme.config.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "org.acme.customer.rest")
public class CustomerRestClientRouteProperties {
    private String input = "direct:callRestEndpoint";

    private String output = "cxfrs:bean:customerRestServiceClient";

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
