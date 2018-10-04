package org.acme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "org.acme.customer.transform")
public class CustomerTransformRouteProperties {
    private String input = "file://src/data/inbox?fileName=customers.csv&noop=true";
    private String output = "file://src/data/outbox?fileName=account-${property.CamelSplitIndex}.json";

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
