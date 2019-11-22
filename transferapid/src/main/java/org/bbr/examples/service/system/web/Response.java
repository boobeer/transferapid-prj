package org.bbr.examples.service.system.web;

import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.event.Event;

public class Response {

    public static final String SUCCESS_MESSAGE = "TRANSFER PROCESSED";
    public static final String FAIL_MESSAGE = "TRANSFER FAILED";

    private String message;
    private CustomerAccount sender;
    private CustomerAccount recipient;
    private TransferMethod transfer;

    public Response(String message) {
        this.message = message;
    }

    public static Response success(Event event) {
        return event.result(new Response(SUCCESS_MESSAGE));
    }

    public static Response fail(Event event) {
        return event.result(new Response(FAIL_MESSAGE));
    }

    public void setSender(CustomerAccount sender) {
        this.sender = sender;
    }

    public CustomerAccount getSender() {
        return sender;
    }

    public void setRecipient(CustomerAccount recipient) {
        this.recipient = recipient;
    }

    public CustomerAccount getRecipient() {
        return recipient;
    }

    public void setTransfer(TransferMethod transfer) {
        this.transfer = transfer;
    }

    public TransferMethod getTransfer() {
        return transfer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
