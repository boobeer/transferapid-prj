package org.bbr.examples.service.business.startegy;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.service.event.Event;
import org.bbr.examples.service.system.io.db.Store;

public class Unsupported implements Strategy {

    @Override
    public void work(Event event, Store store) {
        throw new TransferRuntimeException("Unsupported function!");
    }

    @Override
    public double getCommission() {
        throw new TransferRuntimeException("Unsupported function!");
    }
}
