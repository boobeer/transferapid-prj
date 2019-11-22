package org.bbr.examples.service.event;

import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.business.model.transfer.Transfers;
import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.system.StoreTask;
import org.bbr.examples.service.system.Task;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.service.system.io.db.dto.account.Account;
import org.bbr.examples.service.system.io.db.dto.transfer.Payment;
import org.bbr.examples.service.system.web.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.bbr.examples.service.system.io.db.dto.transfer.Payment.TRANSFER_BY_ACCOUNTS;
import static org.bbr.examples.service.system.web.Response.FAIL_MESSAGE;

/**
 * Checks the state of a transfer identified by sender and recipient.
 */
public class Check implements Event {

    private final Long senderAccountId;
    private final Long recipientAccountId;
    private CustomerAccount sender;
    private CustomerAccount recipient;
    private Transfers transferType;
    private TransferMethod transfer;
    private Payment storedPayment;

    public Check(Long senderAccountId, Long recipientAccountId, Long transferType) {
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.transferType = Transfers.of(transferType);
    }

    @Override
    public Task fire(Operations operations) {
        return null;
    }

    @Override
    public void process(double commission, Store store) {
        List<Payment> payments = store.read(TRANSFER_BY_ACCOUNTS, transfer.getPaymentCriteria());
        storedPayment = payments == null || payments.isEmpty() ? null : payments.get(0);
    }

    @Override
    public Response result(Response response) {
        List<Consumer<Store>> actions = new LinkedList<>();
        actions.add(this::read);
        new StoreTask(actions).run();
        if (transfer != null) {
            transfer.setPayment(storedPayment);
        } else {
            response.setMessage(FAIL_MESSAGE);
        }
        response.setSender(sender);
        response.setRecipient(recipient);
        response.setTransfer(transfer);
        return response;
    }

    private void read(Store store) {
        sender = buildCustomerAccount(senderAccountId);
        recipient = buildCustomerAccount(recipientAccountId);
        buildTransfer();
        process(0.0, store);
    }

    private void buildTransfer() {
        transfer = transferType.factoryMethod.get();
        transfer.setSender(sender);
        transfer.setRecipient(recipient);
    }

    private CustomerAccount buildCustomerAccount(Long accountId) {
        Account account = new Account();
        account.setId(accountId);
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setAccount(account);
        return customerAccount;
    }

}
