package org.fuse.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.globex.Account;
import org.globex.Company;
import org.globex.Contact;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class JacksonCompanyTest {

    @Test
    public void AccountToJson() throws IOException {
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

        String result = new ObjectMapper().writeValueAsString(account);
        assertThat(result, containsString("company"));
        assertThat(result, containsString("contact"));
    }

    @Test
    public void JSonToAccount() throws IOException {
        String json = "{\"company\":{\"name\":\"Rotobots\",\"geo\":\"NA\",\"active\":true},\"contact\":{\"firstName\":\"Bill\",\"lastName\":\"Smith\",\"streetAddr\":\"100 N Park Ave.\",\"city\":\"Phoenix\",\"state\":\"AZ\",\"zip\":\"85017\",\"phone\":\"602-555-1100\"}}";
        Account account = new ObjectMapper().readValue(json,Account.class);
        assertNotNull(account);
        assertEquals("Rotobots",account.getCompany().getName());
    }

}
