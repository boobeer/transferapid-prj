package org.bbr.examples.service.business.model.transfer;

import java.util.function.Supplier;

/*
* Several random kind of transfers
 */
public enum Transfers {
    Local(Local::new),
    Foreign(Foreign::new),
    Special(Special::new);

    public Supplier<TransferMethod> factoryMethod;

    Transfers(Supplier<TransferMethod> factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public static Transfers of(Long transferType) {
        return Transfers.values()[transferType.intValue()];
    }
}
