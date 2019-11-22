package org.bbr.examples.service.event;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.service.business.model.Currency;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.transfer.PaymentStatus;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.business.model.transfer.Transfers;
import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.system.StoreTask;
import org.bbr.examples.service.system.Task;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.service.system.io.db.dto.account.Account;
import org.bbr.examples.service.system.io.db.dto.customer.Client;
import org.bbr.examples.service.system.io.db.dto.transfer.Payment;
import org.bbr.examples.service.system.web.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.bbr.examples.service.system.io.db.dto.account.Account.ACCOUNT_BY_ID;

/**
 * Implements a sample transfer logic: subtracts amount from sender's account and adds it to recipient's
 * also subtracts some commission from both accounts.
 * Firstly amount is blocked, on Confirm operation the Payment gets confirmed.
 */
public class Send implements Event {

    private final Long senderAccountId;
    private final Long recipientAccountId;
    private CustomerAccount sender;
    private CustomerAccount recipient;
    private Transfers transferType;
    private TransferMethod transfer;
    private Operations operations;
    private double amount;

    public Send(Long senderAccountId, Long recipientAccountId, Long transferType, double amount) {
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.transferType = Transfers.of(transferType);
        this.amount = amount;
    }

    @Override
    public Task fire(Operations operations) {
        this.operations = operations;
        List<Consumer<Store>> actions = new LinkedList<>();
        actions.add(this::read);
        actions.add(this::work);
        return new StoreTask(actions);
    }

    @Override
    public void process(double commission, Store store) {
        double totalCommission = Currency.getValue(commission)
                .add(Currency.getValue(transfer.getCompositeCommission()))
                .doubleValue();
        updateAccount(sender, totalCommission, store);
        updateAccount(recipient, totalCommission, store);
        updateTransferPayment(totalCommission, store);
    }

    @Override
    public Response result(Response response) {
        response.setSender(sender);
        response.setRecipient(recipient);
        response.setTransfer(transfer);
        return response;
    }

    private void read(Store store) {
        sender = buildCustomerAccount(store, senderAccountId);
        recipient = buildCustomerAccount(store, recipientAccountId);
        buildTransfer();
    }

    private void buildTransfer() {
        transfer = transferType.factoryMethod.get();
        transfer.setSender(sender);
        transfer.setRecipient(recipient);
        transfer.setPayment(transfer.getPaymentCriteria());
        transfer.getPayment().setType(transferType);
    }

    private CustomerAccount buildCustomerAccount(Store store, Long accountId) {
        Account account = readAccount(store, accountId);
        if (account == null) {
            throw new TransferRuntimeException("Unknown account!");
        }
        Client client = account.getClient();
        CustomerAccount customerAccount = client.getType().factoryMathod.get();
        customerAccount.setAccount(account);
        customerAccount.setClient(client);
        return customerAccount;
    }

    Account getAccountCriteria(Long accountId) {
        Account account = new Account();
        account.setId(accountId);
        return account;
    }

    private Account readAccount(Store store, Long accountId) {
        Account account = getAccountCriteria(accountId);
        List<Account> accounts = store.read(ACCOUNT_BY_ID, account);
        return accounts != null && !accounts.isEmpty() ? accounts.get(0) : null;
    }

    private void work(Store store) {
        operations.chooseStrategy(sender, recipient, transfer).work(this, store);
    }

    private void updateAccount(CustomerAccount customerAccount, double commission, Store store) {
        Account account = customerAccount.getAccount();
        account.setTotalBlocked(
                Currency.getValue(account.getTotalBlocked())
                        .add(computeDelta(commission,customerAccount == sender))
                        .doubleValue());
        store.save(account);
    }

    private BigDecimal computeDelta(double commission, boolean isSending) {
        return Currency.getValue(commission)
                .divide(Currency.getValue(2.0), RoundingMode.HALF_UP)
                .add(Currency.getValue((isSending ? 1 : -1) * amount));
    }

    private void updateTransferPayment(double commission, Store store) {
        Payment payment = transfer.getPayment();
        payment.setTotal(Currency.getValue(amount).doubleValue());
        payment.setCommission(commission);
        payment.setStatus(PaymentStatus.Pending);
        store.save(payment);
    }

}
