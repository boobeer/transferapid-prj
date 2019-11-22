package org.bbr.examples.service.system;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.TimeUnit;

import static org.bbr.examples.service.system.Scheduler.OBSERVE_DELAY;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SchedulerTest {

    @Mock
    private Task randomTask;
    private Scheduler scheduler;

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        scheduler = spy(new Scheduler());
        doReturn(OBSERVE_DELAY).when(scheduler).getObserveDelay();
        doReturn(scheduler).when(factory).getScheduler();
    }


    @Test
    public void testStartStop() throws InterruptedException {
        // given
        scheduler.setTask(() -> randomTask);
        // when
        TimeUnit.MILLISECONDS.sleep(OBSERVE_DELAY * 4);
        scheduler.stop();
        // then
        verify(randomTask, atLeast(3)).run();
    }

    @Test
    public void testStartExceptionInTheThreadStop() throws InterruptedException {
        // given
        scheduler.setTask(() -> randomTask);
        doThrow(new NullPointerException()).when(randomTask).run();
        // when
        TimeUnit.MILLISECONDS.sleep(OBSERVE_DELAY * 4);
        scheduler.stop();
        // then
        verify(randomTask, atLeast(3)).run();
        verify(scheduler, atLeastOnce()).logTaskError();
    }

    @Test
    public void testStartAndStopWhenNoActionInTheQue() throws InterruptedException {
        // given
        scheduler.setTask(() -> null);
        // when
        TimeUnit.MILLISECONDS.sleep(OBSERVE_DELAY * 4);
        scheduler.stop();
        // then
        verify(scheduler, atLeast(3)).tryProcessTask();
    }

    // then
    @Test(expected = TransferRuntimeException.class)
    public void testSetNullActionSupplier() {
        // when
        scheduler.setTask(null);
    }

    // then
    @Test(expected = TransferRuntimeException.class)
    public void testExceptionOnRestart() {
        // given
        doThrow(new RuntimeException("Any exception")).when(scheduler).stop();
        // when
        scheduler.setTask(() -> randomTask);
    }

    @Test
    public void testStopNoAction() {
        // when
        boolean stopped = scheduler.stop();
        // then
        assertFalse(stopped);
    }
}