package org.bbr.examples.service.system.io.db.dto.account;

import org.bbr.examples.service.business.model.Currency;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;

public class AccountTest {

    @Test
    public void testGetAvailableAmountAfterBlockingOnTransfer() {
        // given
        Account account = new Account();
        account.setTotal(10.0);
        account.setTotalBlocked(7.0);
        // when
        BigDecimal available = Currency.getValue(account.getAvailableAmount());
        // then
        assertEquals(0, available.compareTo(Currency.getValue(3.0)));
    }
}