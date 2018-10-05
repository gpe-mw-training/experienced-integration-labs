package org.fuse.usecase;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.globex.Account;
import org.globex.CorporateAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

/**
 * Aggregator implementation which extract the id and salescontact
 * from CorporateAccount and update the Account
 */
public class AccountAggregator implements AggregationStrategy {
    final Logger logger = LoggerFactory.getLogger(AccountAggregator.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if(oldExchange == null) {
            logger.info("oldExchange is empty, just returning newExchange.");
            return newExchange;
        }

        try {
            logger.info("oldExchange body: " + oldExchange.getIn().getBody().getClass());
            logger.info("newExchange body: " + newExchange.getIn().getBody().getClass());
            Account account = (Account) oldExchange.getIn().getBody(ArrayList.class).get(0);
            logger.info("Retrieved Account from oldExchange: " + account);
            CorporateAccount corporateAccount = newExchange.getIn().getBody(CorporateAccount.class);
            logger.info("Retrieved CorporateAccount from newExchange: " + corporateAccount);
            logger.info("CorporateAccount ID: " + corporateAccount.getId());
            account.setClientId(corporateAccount.getId());
            logger.info("CorporateAccount Sales Contact: " + corporateAccount.getSalesContact());
            account.setSalesRepresentative(corporateAccount.getSalesContact());

            oldExchange.getIn().setBody(account, Account.class);

            logger.info("Successfully merged enriched accounts.");
            return oldExchange;
        }
        catch(Exception e) {
            logger.error("Exception: ", e);
        }
        return newExchange;
    }
    
}