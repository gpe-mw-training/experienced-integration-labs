package org.acme.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.ExpressionDefinition;
import org.springframework.stereotype.Component;
import org.globex.Account;
import org.globex.Company;
import org.globex.Contact;
import org.acme.Customer;

@Component
public class CustomerRoute extends RouteBuilder {
    @Override
    public void configure () throws Exception {
        onException(IllegalArgumentException.class)
            .to("log:fail")
            .to("file://src/data/error?fileName=csv-record-${date:now:yyyyMMdd}.txt")
            .handled(true)
            .stop();

        BindyCsvDataFormat format = new BindyCsvDataFormat(org.acme.Customer.class);
        format.setLocale("default");

        from("file://src/data/inbox?fileName=customers.csv&noop=true")
            .split()
            .tokenize("\n")
            .to("log:bar")
            .unmarshal(format)
            .to("log:foo")
            .process(new CustomerProcessor())
            .to("log:transformed")
            .marshal().json(JsonLibrary.Jackson)
            .to("file://src/data/outbox?fileName=account-${property.CamelSplitIndex}.json");
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

    class CustomerTransformer extends ExpressionDefinition {
        Account evaluate(Exchange exchange, Account account) {
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

            return theAccount;
        }
    }
}
