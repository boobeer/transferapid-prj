package org.bbr.examples.utils;

import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.customer.Customers;
import org.bbr.examples.service.business.model.transfer.PaymentStatus;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.business.model.transfer.Transfers;
import org.bbr.examples.service.event.Events;
import org.bbr.examples.service.system.io.db.dto.account.Account;
import org.bbr.examples.service.system.io.db.dto.customer.Client;
import org.bbr.examples.service.system.io.db.dto.transfer.Payment;
import org.bbr.examples.service.system.web.Request;
import org.bbr.examples.service.system.web.Response;

import java.util.Optional;


public class Utils {

    public static Request buildRequest() {
        Request request = new Request();
        request.setPaymentId(8534L);
        request.setAction(Events.Send);
        request.setAmount(23412.83D);
        request.setSenderAccountId(8788787L);
        request.setRecipientAccountId(111L);
        request.setTransferType((long) Transfers.Special.ordinal());
        return request;
    }

    public static Response buildResponse() {
        Response response = new Response("Message");
        response.setTransfer(Utils.buildTransferType());
        return response;
    }

    public static TransferMethod buildTransferType() {
        CustomerAccount sender = buildCustomerAccount(7L, 77777L, 777.1D);
        CustomerAccount recipient = buildCustomerAccount(8L, 888L, 2222.8D);
        Payment payment = buildPayment(sender, recipient);

        TransferMethod transferMethod = new TransferMethod();
        transferMethod.setSender(sender);
        transferMethod.setRecipient(recipient);
        transferMethod.setPayment(payment);
        return transferMethod;
    }

    public static CustomerAccount buildCustomerAccount(long clientId, long accountId, double amount) {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setClient(buildClient(clientId));
        customerAccount.setAccount(buildAccount(accountId, amount));
        customerAccount.getAccount().setClient(customerAccount.getClient());
        return customerAccount;
    }

    public static CustomerAccount buildCustomerAccount(Customers type,long clientId, long accountId, double amount) {
        CustomerAccount customerAccount = Optional.ofNullable(type)
                .map(t -> t.factoryMathod.get()).orElseGet(CustomerAccount::new);
        customerAccount.setClient(buildClient(clientId));
        customerAccount.setAccount(buildAccount(accountId, amount));
        customerAccount.getAccount().setClient(customerAccount.getClient());
        return customerAccount;
    }

    private static Client buildClient(long clientId) {
        Client client = new Client();
        client.setId(clientId);
        client.setType(Customers.Person);
        return client;
    }

    public static Account buildAccount(long accountId, double amount) {
        Account account = new Account();
        account.setId(accountId);
        account.setTotal(amount);
        return account;
    }


    private static Payment buildPayment(CustomerAccount sender, CustomerAccount recipient) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.Approved);
        payment.setSenderAccountId(sender.getAccount().getId());
        payment.setRecipientAccountId(recipient.getAccount().getId());
        payment.setId(111L);
        payment.setTotal(123.7);
        payment.setCommission(7432.87);
        return payment;
    }
}
