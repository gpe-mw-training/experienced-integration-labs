package org.fuse.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.Application;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.acme.routes.CustomerRoute;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

@RunWith(CamelSpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {ValidateTransformationTest.ContextConfig.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@SpringBootTest(classes = Application.class)
public class ValidateTransformationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private CamelContext camelContext;

    @EndpointInject(uri = "mock:csv2json-test-output") private MockEndpoint resultEndpoint;

    @Produce(uri = "direct:csv2json-test-input") private ProducerTemplate startEndpoint;

//    private RouteBuilder createRouteBuilder() throws Exception {
//        return new CustomerRoute();
//    }

    @Test
    public void testMessageOutputCount() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.assertIsSatisfied();
    }

//    @Test
//    public void test() {
//        resultEndpoint.
//    }

//    private String readFile(String filePath) throws Exception {
//        String content;
//        FileInputStream fis = new FileInputStream(filePath);
//        try {
//                content = new CamelContext().getTypeConverter().convertTo(String.class, fis);
//            } finally {
//                fis.close();
//            }
//        return content;
//    }

    private String jsonUnprettyPrint(String jsonString) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        JsonNode node = mapper.readTree(jsonString);
        return node.toString();
    }
}
