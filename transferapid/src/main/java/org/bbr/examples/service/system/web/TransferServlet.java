package org.bbr.examples.service.system.web;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.controller.TransferController;
import org.bbr.examples.service.event.Events;
import org.bbr.examples.utils.JsonProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * A simple servlet processing POST and GET
 */
public class TransferServlet extends HttpServlet {

    static final String PARAMETER_ACTION = "action";
    static final String PARAMETER_SENDER_ACCOUNT_ID = "sender-account-id";
    static final String PARAMETER_RECIPIENT_ACCOUNT_ID = "recipient-account-id";
    static final String PARAMETER_TRANSFER_TYPE = "transfer-type";
    static final String PARAMETER_PAYMENT_ID = "payment-id";

    private TransferController getController() {
        return Factory.instance().getTransferControllerPrototype();
    }

    private JsonProcessor getJsonProcessor() {
        return Factory.instance().getJsonProcessorPrototype();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        TransferController controller = getController();
        Request serviceRequest = getPostRequest(request);
        Response serviceResponse;
        switch (chooseAction(serviceRequest)) {
            case Send:
                serviceResponse = controller.send(serviceRequest);
                break;
            case Confirm:
                serviceResponse = controller.confirm(serviceRequest);
                break;
            case Revert:
                serviceResponse = controller.revert(serviceRequest);
                break;
            default:
                serviceResponse = null;
        }
        buildResponse(response, serviceResponse);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        TransferController controller = getController();
        Request serviceRequest = getGetRequest(request);
        Response serviceResponse;
        if (chooseAction(serviceRequest) == Events.Check) {
            serviceResponse = controller.check(serviceRequest);
        } else {
            serviceResponse = null;
        }
        buildResponse(response, serviceResponse);
    }

    private Request getGetRequest(HttpServletRequest httpRequest) {
        Request request = new Request();
        request.setAction(Events.valueOf(httpRequest.getParameter(PARAMETER_ACTION)));
        request.setSenderAccountId(Long.valueOf(httpRequest.getParameter(PARAMETER_SENDER_ACCOUNT_ID)));
        request.setRecipientAccountId(Long.valueOf(httpRequest.getParameter(PARAMETER_RECIPIENT_ACCOUNT_ID)));
        request.setTransferType(Long.valueOf(httpRequest.getParameter(PARAMETER_TRANSFER_TYPE)));
        request.setPaymentId(Long.valueOf(httpRequest.getParameter(PARAMETER_PAYMENT_ID)));
        return request;
    }

    private void buildResponse(HttpServletResponse response, Response serviceResponse) throws IOException {
        if (serviceResponse != null) {
            success(response, serviceResponse);
        } else {
            failure(response);
        }
    }

    private void success(HttpServletResponse response, Response serviceResponse) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(getJsonProcessor().pojoToJsonString(serviceResponse));
    }

    private void failure(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println("");
    }

    private String getRequestJsonBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private Request getPostRequest(HttpServletRequest request) throws IOException {
        return getJsonProcessor().jsonStringToRequest(getRequestJsonBody(request));
    }

    private Events chooseAction(Request request) {
        return request.getAction();
    }
}
