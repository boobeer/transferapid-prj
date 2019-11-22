package org.bbr.examples.service.business.model.account;

import java.util.function.Supplier;

/**
 * Several random account types
 */
public enum Accounts {
    Bank(Bank::new),
    Card(Card::new),
    Crypto(Crypto::new);

    public Supplier<AccountType> factoryMathod;

    Accounts(Supplier<AccountType> factoryMathod) {
        this.factoryMathod = factoryMathod;
    }

}
