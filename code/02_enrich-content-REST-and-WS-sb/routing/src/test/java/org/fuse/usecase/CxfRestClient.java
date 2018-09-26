package org.fuse.usecase;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
//import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.globex.Account;
import org.globex.Company;
import org.globex.Contact;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class CxfRestClient {

    public static void main(String[] args) {

        Account account = new Account();

        Company company = new Company();
        company.setName("Rotobots");
        company.setGeo("NA");
        company.setActive(true);

        Contact contact = new Contact();
        contact.setFirstName("Bill");
        contact.setLastName("Smith");
        contact.setStreetAddr("100 N Park Ave.");
        contact.setCity("Phoenix");
        contact.setState("AZ");
        contact.setZip("85017");
        contact.setPhone("602-555-1100");

        account.setCompany(company);
        account.setContact(contact);

        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        WebTarget target = client.target("http://localhost:9191/rest/customerservice/enrich");

        Account a = target.request(MediaType.APPLICATION_JSON).post(Entity.json(account), Account.class);
        assertEquals("NORTH_AMERICA",a.getCompany().getGeo());
    }

}
