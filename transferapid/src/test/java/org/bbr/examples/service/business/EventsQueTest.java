package org.bbr.examples.service.business;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventsQueTest {

    @Mock
    private Event event;
    private EventsQue eventsQue;

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        EventsQue que = new EventsQue();
        doReturn(que).when(factory).getEventsQueSingleton();
    }

    @Test
    public void testPushSuccess() {
        // given
        eventsQue = Factory.instance().getEventsQueSingleton();
        // given
        int numberOfEvents = EventsQue.DEFAULT_LIMIT;
        // when
        IntStream.range(0, numberOfEvents)
                .parallel()
                .forEach(i -> eventsQue.push(event));
    }

    @Test(expected = TransferRuntimeException.class)
    public void testPushTooMuch() {
        // given
        eventsQue = Factory.instance().getEventsQueSingleton();
        int numberOfEvents = EventsQue.DEFAULT_LIMIT;
        // when
        IntStream.range(0, numberOfEvents + 1)
                .forEach(i -> eventsQue.push(event));
    }

    @Test
    public void testPop() {
        // given
        eventsQue = Factory.instance().getEventsQueSingleton();
        eventsQue.push(event);
        // when
        Event nextEvent = eventsQue.pop();
        // then
        assertEquals(event, nextEvent);
    }

    @Test
    public void testPopOnEmptyQue() {
        // given
        eventsQue = Factory.instance().getEventsQueSingleton();
        // when
        Event nextEvent = eventsQue.pop();
        // then
        assertNull(nextEvent);
    }
}