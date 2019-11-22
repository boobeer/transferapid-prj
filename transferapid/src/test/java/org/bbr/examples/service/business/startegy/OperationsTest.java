package org.bbr.examples.service.business.startegy;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.business.model.account.Accounts;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.customer.Customers;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.business.model.transfer.Transfers;
import org.bbr.examples.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

public class OperationsTest {

    private Operations operations;

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        operations = Factory.instance().getOperationsSingleton();
    }

    @Test
    public void testChooseAvailableOperation() {
        // given
        CustomerAccount sender = buildCustomerAccount(Customers.Person, Accounts.Card);
        CustomerAccount receiver = buildCustomerAccount(Customers.Person, Accounts.Card);
        TransferMethod transferMethod = buildTransferType(Transfers.Local);
        // when
        Strategy transferStrategy = operations.chooseStrategy(sender, receiver, transferMethod);
        // then
        assertEquals(transferStrategy.getClass(), Basic.class);
    }

    // then
    @Test
    public void testChooseUnsupportedOperation() {
        // given
        CustomerAccount sender = buildCustomerAccount(Customers.Person, Accounts.Card);
        CustomerAccount receiver = buildCustomerAccount(Customers.Corporate, Accounts.Crypto);
        TransferMethod transferMethod = buildTransferType(Transfers.Special);
        // when
        Strategy transferStrategy = operations.chooseStrategy(sender, receiver, transferMethod);
        // then
        assertEquals(transferStrategy.getClass(), Unsupported.class);
    }

    private CustomerAccount buildCustomerAccount(
            Customers customerType, Accounts senderAccountType) {
        CustomerAccount customerAccount = Utils.buildCustomerAccount(1L, 1L, 1.0);
        customerAccount.getClient().setType(customerType);
        customerAccount.getAccount().setType(senderAccountType);
        return customerAccount;
    }

    private TransferMethod buildTransferType(Transfers transfer) {
        TransferMethod transferMethod = Utils.buildTransferType();
        transferMethod.getPayment().setType(transfer);
        return transferMethod;
    }

}