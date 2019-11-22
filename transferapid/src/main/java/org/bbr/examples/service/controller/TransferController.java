package org.bbr.examples.service.controller;

import org.bbr.examples.error.TransferException;
import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.business.TransferService;
import org.bbr.examples.service.event.*;
import org.bbr.examples.service.system.LoggerAdaptor;
import org.bbr.examples.service.system.web.Request;
import org.bbr.examples.service.system.web.Response;

/**
 * Processes incoming requests.
 */
public class TransferController {

    private LoggerAdaptor getLogger() {
        return Factory.of(TransferController.class).getLogger();
    }

    private TransferService getProcessor() {
        return Factory.instance().getTransferProcessor();
    }

    public Response send(Request request) {
        return process(new Send(
                request.getSenderAccountId(),
                request.getRecipientAccountId(),
                request.getTransferType(),
                request.getAmount()));
    }

    public Response check(Request request) {
        return process(new Check(
                request.getSenderAccountId(),
                request.getRecipientAccountId(),
                request.getTransferType()));
    }

    public Response confirm(Request request) {
        return process(new Confirm(
                request.getPaymentId()));
    }

    public Response revert(Request request) {
        return process(new Revert(
                request.getPaymentId()));
    }

    private Response process(Event event) {
        try {
            getProcessor().process(event);
            return Response.success(event);
        } catch (TransferException | TransferRuntimeException e) {
            getLogger().error("Error while processing transfer request", e);
            return Response.fail(event);
        }
    }
}
