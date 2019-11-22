package org.bbr.examples.service.business.startegy;

import org.bbr.examples.service.event.Event;
import org.bbr.examples.service.system.io.db.Store;

public class Basic implements Strategy {

    @Override
    public void work(Event event, Store store) {
        event.process(getCommission(), store);
    }

    @Override
    public double getCommission() {
        return 10;
    }

}
