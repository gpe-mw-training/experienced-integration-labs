package org.acme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "org.acme.customer.load")
public class CustomerLoadRouteProperties {
    private String input = "file://src/data/outbox?noop=true&idempotent=true";
    private String restEndpoint = "direct:callRestEndpoint";
    private String wsEndpoint = "direct:callWsEndpoint";
    private String output = "direct:insertDb";


    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getRestEndpoint() {
        return restEndpoint;
    }

    public void setRestEndpoint(String restEndpoint) {
        this.restEndpoint = restEndpoint;
    }

    public String getWsEndpoint() {
        return wsEndpoint;
    }

    public void setWsEndpoint(String wsEndpoint) {
        this.wsEndpoint = wsEndpoint;
    }
}
