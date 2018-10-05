package org.fuse.usecase;

import org.acme.Application;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(CamelSpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ValidateTransformationTest {

    @EndpointInject(uri = "mock:csv2json-test-output")
    private MockEndpoint resultEndpoint;

    @Test
    public void testMessageOutputCount() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.assertIsSatisfied();
    }
}
