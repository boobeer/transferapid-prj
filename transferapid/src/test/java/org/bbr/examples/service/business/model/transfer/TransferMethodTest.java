package org.bbr.examples.service.business.model.transfer;

import org.bbr.examples.service.business.model.Currency;
import org.bbr.examples.service.business.model.account.Accounts;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.customer.Customers;
import org.bbr.examples.service.system.io.db.dto.transfer.Payment;
import org.bbr.examples.utils.Utils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class TransferMethodTest {

    @Test
    public void testGetPaymentCriteria() {
        // given
        Local local = new Local();
        local.setSender(buildCustomerAccount(Customers.Person, Accounts.Crypto));
        local.setRecipient(buildCustomerAccount(Customers.Corporate, Accounts.Bank));
        // when
        Payment paymentCriteria = local.getPaymentCriteria();
        // then
        assertNotNull(paymentCriteria);
        assertEquals(paymentCriteria.getRecipientAccountId(), local.getRecipient().getAccount().getId());
        assertEquals(paymentCriteria.getRecipientAccountId(), local.getSender().getAccount().getId());
    }

    @Test
    public void testGetCompositeCommission() {
        // given
        Foreign foreign = new Foreign();
        foreign.setSender(buildCustomerAccount(Customers.Person, Accounts.Crypto));
        foreign.setRecipient(buildCustomerAccount(Customers.Corporate, Accounts.Bank));
        // when
        BigDecimal totalCommission = Currency.getValue(foreign.getCompositeCommission());
        // then
        BigDecimal[] commissions = {
                Currency.getValue(Customers.Person.factoryMathod.get().getCommission()),
                Currency.getValue(Accounts.Crypto.factoryMathod.get().getCommission()),
                Currency.getValue(Customers.Corporate.factoryMathod.get().getCommission()),
                Currency.getValue(Accounts.Bank.factoryMathod.get().getCommission()),
                Currency.getValue(foreign.getCommission())
        };
        BigDecimal sum = Stream.of(commissions).reduce(Currency.getValue(0.0), BigDecimal::add);
        assertTrue(sum.doubleValue() > 0);
        assertEquals(0, sum.compareTo(totalCommission));
    }

    private CustomerAccount buildCustomerAccount(
            Customers customerType, Accounts senderAccountType) {
        CustomerAccount customerAccount = Utils.buildCustomerAccount(1L, 1L, 1.0);
        customerAccount.getClient().setType(customerType);
        customerAccount.getAccount().setType(senderAccountType);
        return customerAccount;
    }

}