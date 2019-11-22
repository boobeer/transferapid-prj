package org.bbr.examples.service.controller;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.business.TransferProcessor;
import org.bbr.examples.service.event.*;
import org.bbr.examples.service.system.web.Request;
import org.bbr.examples.service.system.web.Response;
import org.bbr.examples.utils.Utils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.bbr.examples.service.system.web.Response.FAIL_MESSAGE;
import static org.bbr.examples.service.system.web.Response.SUCCESS_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TransferControllerTest {

    @Captor
    private ArgumentCaptor<Event> eventArgumentCaptor;
    @Mock
    private TransferProcessor transferProcessor;
    @Spy
    private TransferController controller = new TransferController();

    private Request request = Utils.buildRequest();


    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        doReturn(controller).when(factory).getTransferControllerPrototype();
        doReturn(transferProcessor).when(factory).getTransferProcessor();
    }

    @Test
    public void testEventFailed() {
        // given
        doThrow(new TransferRuntimeException("Processing error")).when(transferProcessor).process(any());
        // when
        Response response = controller.send(request);
        // then
        verify(transferProcessor, times(1)).process(eventArgumentCaptor.capture());
        assertEquals(Send.class,eventArgumentCaptor.getValue().getClass());
        assertEquals(FAIL_MESSAGE,response.getMessage());
    }

    @Test
    public void testSend() {
        // given
        request.setAction(Events.Send);
        // when
        Response response = controller.send(request);
        // then
        verify(transferProcessor, times(1)).process(eventArgumentCaptor.capture());
        assertEquals(Send.class,eventArgumentCaptor.getValue().getClass());
        assertEquals(SUCCESS_MESSAGE,response.getMessage());
    }

    @Test
    public void testCheck() {
        // given
        request.setAction(Events.Check);
        // when
        Response response = controller.check(request);
        // then
        verify(transferProcessor, times(1)).process(eventArgumentCaptor.capture());
        assertEquals(Check.class,eventArgumentCaptor.getValue().getClass());
        assertEquals(SUCCESS_MESSAGE,response.getMessage());
    }

    @Test
    public void testConfirm() {
        // given
        request.setAction(Events.Confirm);
        // when
        Response response = controller.confirm(request);
        // then
        verify(transferProcessor, times(1)).process(eventArgumentCaptor.capture());
        assertEquals(Confirm.class,eventArgumentCaptor.getValue().getClass());
        assertEquals(SUCCESS_MESSAGE,response.getMessage());
    }

    @Test
    public void testRevert() {
        // given
        request.setAction(Events.Revert);
        // when
        Response response = controller.revert(request);
        // then
        verify(transferProcessor, times(1)).process(eventArgumentCaptor.capture());
        assertEquals(Revert.class,eventArgumentCaptor.getValue().getClass());
        assertEquals(SUCCESS_MESSAGE,response.getMessage());
    }
}