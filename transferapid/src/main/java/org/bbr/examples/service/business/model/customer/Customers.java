package org.bbr.examples.service.business.model.customer;

import java.util.function.Supplier;

/**
 * Several random customer types
 */
public enum Customers {
    Person(Person::new),
    Corporate(Corporate::new);

    public Supplier<CustomerAccount> factoryMathod;

    Customers(Supplier<CustomerAccount> factoryMathod) {
        this.factoryMathod = factoryMathod;
    }
}
