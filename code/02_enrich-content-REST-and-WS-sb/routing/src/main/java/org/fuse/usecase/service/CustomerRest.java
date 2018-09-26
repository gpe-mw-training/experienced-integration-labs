package org.fuse.usecase.service;

import org.acme.Customer;
import org.globex.Account;

import javax.ws.rs.*;

@Path("/customerservice/")
public interface CustomerRest {

    @POST @Path("/enrich") @Consumes("application/json")
    Account enrich(Account customer);

}
