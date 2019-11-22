package org.bbr.examples.service.system.io.db;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.system.io.db.dto.account.Account;
import org.bbr.examples.utils.Utils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.bbr.examples.service.system.io.db.dto.account.Account.ACCOUNT_BY_ID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

public class StoreTest {

    private Store store;
    private Session session;

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        store = factory.getStoreSingleton();
        session = store.getCurrentSession();
    }

    @After
    public void finish() {
        store.closeCurrentSession();
    }

    @Test
    public void testSaveAndReadCustomerAccount() {
        // given
        CustomerAccount customerAccount = buildCustomerAccount();

        // when
        Transaction transaction1 = session.getTransaction();
        transaction1.begin();
        store.save(customerAccount.getClient());
        store.save(customerAccount.getAccount());
        transaction1.commit();

        session.clear();

        Transaction transaction2 = session.getTransaction();
        transaction2.begin();
        Account accountCriteria = Utils.buildAccount(customerAccount.getAccount().getId(), 0);
        List<Account> storedAccounts = store.read(ACCOUNT_BY_ID, accountCriteria);
        transaction2.commit();

        // then
        assertFalse(storedAccounts.isEmpty());
        Account storedAccount = storedAccounts.get(0);
        assertNotNull(storedAccount.getId());
        assertEquals(customerAccount.getAccount().getId(), storedAccount.getId());
        assertNotNull(storedAccount.getClient());
        assertEquals(customerAccount.getClient().getId(), storedAccount.getClient().getId());
    }

    @Test
    public void testRollbackAndReadCustomerAccount() {
        // given
        CustomerAccount customerAccount = buildCustomerAccount();

        // when
        Transaction transaction1 = session.getTransaction();
        transaction1.begin();
        store.save(customerAccount.getClient());
        store.save(customerAccount.getAccount());
        transaction1.rollback();

        session.clear();

        Transaction transaction2 = session.getTransaction();
        transaction2.begin();
        Account accountCriteria = Utils.buildAccount(customerAccount.getAccount().getId(), 0);
        List<Account> storedAccounts = store.read(ACCOUNT_BY_ID, accountCriteria);
        transaction2.commit();

        // then
        assertTrue(storedAccounts.isEmpty());
    }

    @Test
    public void testDelete() {
        // given
        CustomerAccount customerAccount = buildCustomerAccount();

        // when
        Transaction transaction1 = session.getTransaction();
        transaction1.begin();
        store.save(customerAccount.getClient());
        store.save(customerAccount.getAccount());
        List<Account> storedAccounts = store.read(ACCOUNT_BY_ID, customerAccount.getAccount());
        store.delete(storedAccounts.get(0));
        transaction1.commit();

        Transaction transaction2 = session.getTransaction();
        transaction2.begin();
        Account accountCriteria = Utils.buildAccount(customerAccount.getAccount().getId(), 0);
        List<Account> noAccounts = store.read(ACCOUNT_BY_ID, accountCriteria);
        transaction2.commit();

        // then
        assertTrue(noAccounts.isEmpty());
        assertNotNull(accountCriteria.getId());
    }

    private CustomerAccount buildCustomerAccount() {
        CustomerAccount customerAccount = Utils.buildCustomerAccount(1L, 2L, 3.7);
        customerAccount.getAccount().setId(null);
        customerAccount.getClient().setId(null);
        return customerAccount;
    }
}