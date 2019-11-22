package org.bbr.examples.service.business;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.event.Confirm;
import org.bbr.examples.service.event.Event;
import org.bbr.examples.service.event.Send;
import org.bbr.examples.service.system.Scheduler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.bbr.examples.service.system.Scheduler.OBSERVE_DELAY;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TransferProcessorTest {

    @Mock
    private Send send;
    @Mock
    private Event check;
    @Mock
    private Confirm confirm;

    private static TransferProcessor processor;

    @BeforeClass
    public static void initSingletons() {
        Factory factory = FactorySpy.getTestingFactory();
        doNothing().when(factory).with(any(TransferProcessor.class));
        processor = spy(new TransferProcessor());
        doReturn(processor).when(factory).getTransferProcessor();
        Scheduler.visit(factory);
        Scheduler schedulerSpy = spy(factory.getScheduler());
        doReturn(schedulerSpy).when(factory).getScheduler();
        doReturn(OBSERVE_DELAY).when(schedulerSpy).getObserveDelay();
        TransferProcessor.visit(factory);
    }

    @Before
    public void init() {
        initMocks(this);
    }

    @Test
    public void testProcessAllEventsInTheQue() throws InterruptedException {
        // given
        Event[] events = {send, check, confirm};
        // when
        Stream.of(events).parallel().forEach(processor::process);
        TimeUnit.MILLISECONDS.sleep(OBSERVE_DELAY * (events.length * 2));
        // then
        verify(send, times(1)).fire(any(Operations.class));
        verify(check, times(1)).fire(any(Operations.class));
        verify(confirm, times(1)).fire(any(Operations.class));
    }

}