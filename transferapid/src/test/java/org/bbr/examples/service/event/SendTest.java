package org.bbr.examples.service.event;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.business.model.Currency;
import org.bbr.examples.service.business.model.account.Accounts;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.customer.Customers;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.business.model.transfer.Transfers;
import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.business.startegy.Strategy;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.service.system.io.db.dto.Entry;
import org.bbr.examples.service.system.io.db.dto.account.Account;
import org.bbr.examples.service.system.io.db.dto.transfer.Payment;
import org.bbr.examples.utils.Utils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.bbr.examples.service.system.io.db.dto.account.Account.ACCOUNT_BY_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendTest {

    private long senderAccountId = 10L;
    private long recipientAccountId = 20L;
    private long senderId = 1L;
    private long recipientId = 2L;
    private Operations operations;
    private CustomerAccount senderAccount;
    private CustomerAccount recipientAccount;
    private Account sender;
    private Account recipient;
    private TransferMethod transferMethod = Transfers.Local.factoryMethod.get();
    private double transferAmount = 30.0;

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Store store;
    @Spy
    private Send send = new Send(senderId, recipientId, (long)Transfers.Local.ordinal(), transferAmount);

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        doReturn(store).when(factory).getStoreSingleton();
        operations = factory.getOperationsSingleton();
        mockValues();
        mockStore();
    }

    @Test
    public void testSend() {
        // given
        System.out.println("************ INITIAL ************");
        System.out.println("Sender account: " + sender.getAvailableAmount());
        System.out.println("Recipient account: " + recipient.getAvailableAmount());
        Strategy operationStrategy = operations
                .chooseStrategy(senderAccount,recipientAccount,transferMethod);
        BigDecimal commission =
                Currency.getValue(transferMethod.getCompositeCommission())
                .add(Currency.getValue(operationStrategy.getCommission()));
        BigDecimal clientCommission = commission.divide(Currency.getValue(2.0),RoundingMode.HALF_UP);
        BigDecimal transfer = Currency.getValue(transferAmount);

        // when
        System.out.println("************ TRANSFER ************");
        System.out.println("To send: " + transferAmount);
        send.fire(operations).run();

        // then
        System.out.println("************ RESULT ************");
        assertEquals(0,
                Currency.getValue(sender.getAvailableAmount())
                .compareTo(Currency.getValue(100.0).subtract(clientCommission).subtract(transfer)));
        assertEquals(0,
                Currency.getValue(recipient.getAvailableAmount())
                        .compareTo(Currency.getValue(100.0).subtract(clientCommission).add(transfer)));

        verify(store,times(3)).save(paymentArgumentCaptor.capture());
        Payment payment = paymentArgumentCaptor.getValue();
        assertEquals(0,
                Currency.getValue(payment.getTotal())
                        .compareTo(Currency.getValue(transferAmount)));
        assertEquals(0,
                Currency.getValue(payment.getCommission())
                        .compareTo(commission));
        assertEquals(senderAccountId,payment.getSenderAccountId().longValue());
        assertEquals(recipientAccountId,payment.getRecipientAccountId().longValue());
        System.out.println("Sent: " + transfer.doubleValue());
        System.out.println("Sender commission: " + clientCommission.doubleValue());
        System.out.println("Recipient commission: " + clientCommission.doubleValue());
        System.out.println("Sender account: " + sender.getAvailableAmount());
        System.out.println("Recipient account: " + recipient.getAvailableAmount());
        System.out.println("Payment: " + payment.getTotal());
        System.out.println("Profit: " + payment.getCommission());
    }

    private void mockValues() {
        senderAccount = Utils.buildCustomerAccount(Customers.Person, senderId, senderAccountId, 100.0);
        recipientAccount = Utils.buildCustomerAccount(Customers.Person, recipientId, recipientAccountId, 100.0);
        sender = senderAccount.getAccount();
        sender.setType(Accounts.Card);
        sender.getClient().setType(Customers.Person);
        recipient = recipientAccount.getAccount();
        recipient.setType(Accounts.Card);
        recipient.getClient().setType(Customers.Person);
        Payment payment = new Payment();
        payment.setType(Transfers.Local);
        transferMethod.setPayment(payment);
        transferMethod.setSender(senderAccount);
        transferMethod.setRecipient(recipientAccount);
    }

    private void mockStore() {
        List<Entry> senders = new LinkedList<>(Collections.singletonList(sender));
        List<Entry> recipients = new LinkedList<>(Collections.singletonList(recipient));
        when(store.read(eq(ACCOUNT_BY_ID),eq(sender))).thenReturn(senders);
        when(store.read(eq(ACCOUNT_BY_ID),eq(recipient))).thenReturn(recipients);
        doReturn(sender).when(send).getAccountCriteria(eq(senderId));
        doReturn(recipient).when(send).getAccountCriteria(eq(recipientId));
        when(store.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
    }

}