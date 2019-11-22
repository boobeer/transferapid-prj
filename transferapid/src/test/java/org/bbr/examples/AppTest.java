package org.bbr.examples;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.business.model.account.Accounts;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.customer.Customers;
import org.bbr.examples.service.business.model.transfer.Transfers;
import org.bbr.examples.service.event.Events;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.service.system.web.HttpUtils;
import org.bbr.examples.service.system.web.Request;
import org.bbr.examples.service.system.web.Response;
import org.bbr.examples.utils.JsonProcessor;
import org.bbr.examples.utils.Utils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


public class AppTest {

    private Request requestSend;
    private Request requestCheck;
    private Response actualResponseSend;
    private Response actualResponseCheck;
    private double sendAmount = 30.0;
    private Store store;
    private Session session;
    private CustomerAccount[] accounts = new CustomerAccount[2];

    @Test
    public void testSendTransfer() throws InterruptedException, IOException, URISyntaxException {
        // given
        try {
            App.start();
            initStore();
            buildSendRequest();
            buildCheckRequest();

            // when
            actualResponseSend = HttpUtils.doPost(requestSend);
            actualResponseCheck = HttpUtils.doGet(requestCheck);

            // then
            TimeUnit.SECONDS.sleep(3);
        } finally {
            App.stop();
            logResults();
        }
    }

    private void logResults() {
        JsonProcessor jsonProcessor = new JsonProcessor();
        System.out.println("********* POST:  SEND REQUEST *********");
        System.out.println("Transferring: " + sendAmount);
        String requestJson = jsonProcessor.pojoToJsonString(requestSend);
        System.out.println("--->" + requestJson);
        System.out.println("********* RESPONSE *********");
        String actualResponseJson = jsonProcessor.pojoToJsonString(actualResponseSend);
        System.out.println("<---" + actualResponseJson);
        System.out.println("********* GET:  CHECK REQUEST *********");
        String requestCheckJson = jsonProcessor.pojoToJsonString(requestCheck);
        System.out.println("--->" + requestCheckJson);
        System.out.println("********* CHECK *********");
        String actualResponseCheckJson = jsonProcessor.pojoToJsonString(actualResponseCheck);
        System.out.println("<---" + actualResponseCheckJson);
    }

    private void initStore() {
        Factory factory = Factory.instance();
        store = factory.getStoreSingleton();
        session = store.getCurrentSession();
        IntStream.range(0, accounts.length).forEach(this::makeAccount);
    }

    private void makeAccount(int accountNumber) {
        CustomerAccount customerAccount = buildCustomerAccount();
        Transaction transaction = session.getTransaction();
        transaction.begin();
        store.save(customerAccount.getClient());
        store.save(customerAccount.getAccount());
        transaction.commit();
        accounts[accountNumber] = customerAccount;
    }

    private CustomerAccount buildCustomerAccount() {
        CustomerAccount customerAccount = Utils
                .buildCustomerAccount(Customers.Person, 0L, 0L, 100);
        customerAccount.getAccount().setType(Accounts.Card);
        customerAccount.getAccount().setId(null);
        customerAccount.getClient().setId(null);
        return customerAccount;
    }

    private void buildSendRequest() {
        requestSend = Utils.buildRequest();
        requestSend.setSenderAccountId(accounts[0].getAccount().getId());
        requestSend.setRecipientAccountId(accounts[1].getAccount().getId());
        requestSend.setPaymentId(null);
        requestSend.setAction(Events.Send);
        requestSend.setTransferType((long) Transfers.Local.ordinal());
        requestSend.setAmount(sendAmount);
    }

    private void buildCheckRequest() {
        requestCheck = Utils.buildRequest();
        requestCheck.setSenderAccountId(accounts[0].getAccount().getId());
        requestCheck.setRecipientAccountId(accounts[1].getAccount().getId());
        requestCheck.setPaymentId(0L);
        requestCheck.setAction(Events.Check);
        requestCheck.setTransferType((long) Transfers.Local.ordinal());
    }

}
