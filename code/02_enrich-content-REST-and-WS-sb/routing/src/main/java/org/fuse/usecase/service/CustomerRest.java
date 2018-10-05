package org.fuse.usecase.service;

import org.acme.Customer;
import org.globex.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/customerservice/")
public interface CustomerRest {

    @POST @Path("/enrich")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Account enrich(Account customer);

}
