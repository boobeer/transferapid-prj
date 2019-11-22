package org.bbr.examples.service.system;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.utils.Utils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class StoreTaskTest {

    @Mock
    private Store store;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;

    private CustomerAccount customerAccount = Utils.buildCustomerAccount(1L,2L,3.8D);
    private TransferMethod transferMethod = Utils.buildTransferType();
    private List<Consumer<Store>> actions = new LinkedList<>();
    private StoreTask storeTask;

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        doReturn(store).when(factory).getStoreSingleton();
        when(store.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        actions.add(this::saveCustomerAccount);
        actions.add(this::saveTransfer);
        storeTask = spy(new StoreTask(actions));
    }

    private void saveCustomerAccount(Store store) {
        store.save(customerAccount.getClient());
        store.save(customerAccount.getAccount());
    }

    private void saveTransfer(Store store) {
        store.save(transferMethod.getPayment());
    }

    @Test
    public void testActionsSucceed() {
        // when
        storeTask.run();
        // then
        verify(store,times(1)).save(eq(customerAccount.getClient()));
        verify(store,times(1)).save(eq(customerAccount.getAccount()));
        verify(store,times(1)).save(eq(transferMethod.getPayment()));
        verify(session,times(1)).beginTransaction();
        verify(transaction,times(1)).commit();
        verify(transaction,times(0)).rollback();
        verify(store,times(1)).closeCurrentSession();
    }

    @Test
    public void testActionsFailed() {
        // given
        doThrow(new TransferRuntimeException("Error!")).when(store).save(eq(customerAccount.getAccount()));
        // when
        storeTask.run();
        // then
        verify(store,times(1)).save(eq(customerAccount.getClient()));
        verify(store,times(1)).save(eq(customerAccount.getAccount()));
        verify(store,times(0)).save(eq(transferMethod.getPayment()));
        verify(session,times(1)).beginTransaction();
        verify(transaction,times(0)).commit();
        verify(transaction,times(1)).rollback();
        verify(session,times(1)).clear();
        verify(store,times(1)).closeCurrentSession();
    }
}