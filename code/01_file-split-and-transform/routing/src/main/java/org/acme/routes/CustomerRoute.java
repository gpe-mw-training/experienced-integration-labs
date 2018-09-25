package org.acme.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
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
        onException(IllegalArgumentException.class).to("log:fail").handled(true).stop();
//        from("timer:foo").to("log:bar");
        BindyCsvDataFormat format = new BindyCsvDataFormat(org.acme.Customer.class);
        format.setLocale("default");
        from("file://src/data/inbox?fileName=customers.csv&noop=true")
            .split().tokenize("\n")
            .to("log:bar")
            .unmarshal(format).to("log:foo")
            .transform().expression(new CustomerTransformer()).to("log:tranformed");
//            .unmarshal(format);
//                .transform().
////                .json()
//                .to("file://src/data/outbox?fileName=account-${property.CamelSplitIndex}.json").
//                onException(IllegalArgumentException.class).to("file://src/data/error?fileName=csv-record-${date:now:yyyyMMdd}.txt");
    }

    class CustomerTransformer extends ExpressionDefinition {
        Account evaluate(Exchange exchange, Account account) {
            Customer customer = exchange.getIn(Customer.class);
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
