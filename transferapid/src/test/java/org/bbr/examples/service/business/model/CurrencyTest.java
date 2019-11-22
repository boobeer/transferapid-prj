package org.bbr.examples.service.business.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CurrencyTest {

    @Test
    public void testGetValue() {
        // given
        double value = 1.7;
        // when
        BigDecimal result = Currency.getValue(value);
        // then
        assertEquals(value, result.doubleValue(), 0.0);
    }

    @Test
    public void testGetZero() {
        // given
        Double value = null;
        // when
        BigDecimal result = Currency.getValue(value);
        // then
        assertEquals(0.0, result.doubleValue(), 0.0);
    }
}