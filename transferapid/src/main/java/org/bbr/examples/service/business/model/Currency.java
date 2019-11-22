package org.bbr.examples.service.business.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class Currency {
    public static BigDecimal getValue(Double value) {
        return new BigDecimal(Optional.ofNullable(value).orElse(0.0))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
