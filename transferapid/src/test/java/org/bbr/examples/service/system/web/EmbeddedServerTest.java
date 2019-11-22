package org.bbr.examples.service.system.web;

import org.bbr.examples.App;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.controller.TransferController;
import org.bbr.examples.service.event.Events;
import org.bbr.examples.utils.JsonProcessor;
import org.bbr.examples.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class EmbeddedServerTest {

    private Request request = Utils.buildRequest();
    private Response responseSend = Utils.buildResponse();
    private Response responseConfirm = Utils.buildResponse();
    private Response responseRevert = Utils.buildResponse();
    private Response responseCheck = Utils.buildResponse();

    @Mock
    private TransferController controller;

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(controller).when(factory).getTransferControllerPrototype();
        mockController();
        App.start();
    }

    @After
    public void finish() {
        App.stop();
    }

    @Test
    public void testPostSend() throws IOException {
        // given
        request.setAction(Events.Send);
        System.out.println("*************");
        System.out.println(request.getAction().name().toUpperCase());
        System.out.println("*************");
        // when
        Response actualResponseSend = HttpUtils.doPost(request);
        // then
        assertEqualResponse(responseSend, actualResponseSend);
    }

    @Test
    public void testPostConfirm() throws IOException {
        // given
        request.setAction(Events.Confirm);
        System.out.println("*************");
        System.out.println(request.getAction().name().toUpperCase());
        System.out.println("*************");
        // when
        Response actualResponseConfirm = HttpUtils.doPost(request);
        // then
        assertEqualResponse(responseSend, actualResponseConfirm);
    }

    @Test
    public void testPostRevert() throws IOException {
        // given
        request.setAction(Events.Revert);
        System.out.println("*************");
        System.out.println(request.getAction().name().toUpperCase());
        System.out.println("*************");
        // when
        Response actualResponseRevert = HttpUtils.doPost(request);
        // then
        assertEqualResponse(responseSend, actualResponseRevert);
    }

    @Test
    public void testGetCheck() throws IOException, URISyntaxException {
        // given
        request.setAction(Events.Check);
        System.out.println("*************");
        System.out.println(request.getAction().name().toUpperCase());
        System.out.println("*************");
        // when
        Response actualResponseCheck = HttpUtils.doGet(request);
        // then
        assertEqualResponse(responseSend, actualResponseCheck);
    }

    private void assertEqualResponse(Response response, Response actualResponse) {
        JsonProcessor jsonProcessor = new JsonProcessor();
        String actualJson = jsonProcessor.pojoToJsonString(actualResponse);
        assertEquals(jsonProcessor.pojoToJsonString(response), actualJson);
    }

    private void mockController() {
        when(controller.send(any())).thenReturn(responseSend);
        when(controller.confirm(any())).thenReturn(responseConfirm);
        when(controller.revert(any())).thenReturn(responseRevert);
        when(controller.check(any())).thenReturn(responseCheck);
    }

}