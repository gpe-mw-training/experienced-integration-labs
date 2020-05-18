package org.fuse.usecase;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import java.util.HashMap;
import java.util.Map;

public class ProcessorBean {

    public void debug(Exchange exchange) {
        Object body = (Object) exchange.getIn().getBody();
        Map<String, Object> headers = (Map<String, Object>) exchange.getIn().getHeaders();
        System.out.println(">> TO DEBUG >>");
    }
}
