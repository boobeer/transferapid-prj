package org.bbr.examples.service.system.web;

import org.bbr.examples.service.event.Events;

public class Request {

    private double amount;
    private Long senderAccountId;
    private Long recipientAccountId;
    private Long paymentId;
    private Events action;
    private Long transferType;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(Long senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public Long getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Long recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Events getAction() {
        return action;
    }

    public void setAction(Events action) {
        this.action = action;
    }

    public Long getTransferType() {
        return transferType;
    }

    public void setTransferType(Long transferType) {
        this.transferType = transferType;
    }

}
