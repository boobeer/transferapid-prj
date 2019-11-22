package org.bbr.examples.service.business.model.transfer;

import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.system.io.db.dto.transfer.Payment;


public class TransferMethod {

    private CustomerAccount sender;
    private CustomerAccount recipient;
    private Payment payment;

    public Transfers getType() {
        return payment.getType();
    }

    public void setSender(CustomerAccount sender) {
        this.sender = sender;
    }

    public void setRecipient(CustomerAccount recipient) {
        this.recipient = recipient;
    }

    public CustomerAccount getSender() {
        return sender;
    }

    CustomerAccount getRecipient() {
        return recipient;
    }

    public Payment getPaymentCriteria() {
        Payment payment = new Payment();
        payment.setSenderAccountId(sender.getAccount().getId());
        payment.setRecipientAccountId(recipient.getAccount().getId());
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public double getCommission() {
        return 0;
    }

    public double getCompositeCommission() {
        return getCommission() + sender.getCompositeCommission() + recipient.getCompositeCommission();
    }
}
