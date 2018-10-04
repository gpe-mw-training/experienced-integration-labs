package org.acme.routes;

import org.acme.Customer;
import org.acme.config.CustomerTransformRouteProperties;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.globex.Account;
import org.globex.Company;
import org.globex.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerTransformRoute extends RouteBuilder {

    @Autowired
    private CustomerTransformRouteProperties customerTransformRouteProperties;

    @Override
    public void configure () throws Exception {
        onException(IllegalArgumentException.class)
            .to("log:fail")
            .to("file://src/data/error?fileName=csv-record-${date:now:yyyyMMdd}.txt")
            .handled(true)
            .stop();

        BindyCsvDataFormat format = new BindyCsvDataFormat(org.acme.Customer.class);
        format.setLocale("default");

        from(customerTransformRouteProperties.getInput()).routeId("customer2account-transform")
            .split()
            .tokenize("\n")
            .to("log:tokenized")
            .unmarshal(format)
            .to("log:unmarshalled")
            .to("dozer:customerToAccount?mappingFile=transformation.xml&sourceModel=org.acme.Customer&targetModel=org.globex.Account")
            /*
            If you want to use a processor, uncomment the line below and comment the dozer line above
            .process(new CustomerProcessor())
             */
            .to("log:transformed")
            .marshal().json(JsonLibrary.Jackson)
            .to(customerTransformRouteProperties.getOutput());
    }

    class CustomerProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            Customer customer = exchange.getIn().getBody(Customer.class);
            Account theAccount = new Account();
            Company theCompany = new Company();
            Contact theContact = new Contact();

            theContact.setCity(customer.getCity());
            theContact.setFirstName(customer.getFirstName());
            theContact.setLastName(customer.getLastName());
            theContact.setPhone(customer.getPhone());
            theContact.setState(customer.getState());
            theContact.setStreetAddr(customer.getStreetAddr());
            theContact.setZip(customer.getZip());

            theCompany.setActive(customer.isActive());
            theCompany.setGeo(customer.getRegion());
            theCompany.setName(customer.getCompanyName());

            theAccount.setCompany(theCompany);
            theAccount.setContact(theContact);

            exchange.getIn().setBody(theAccount, Account.class);
        }
    }
}
