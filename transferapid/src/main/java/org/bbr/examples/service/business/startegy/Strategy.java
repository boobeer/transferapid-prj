package org.bbr.examples.service.business.startegy;

import org.bbr.examples.service.event.Event;
import org.bbr.examples.service.system.io.db.Store;

public interface Strategy {

    void work(Event event, Store store);

    double getCommission();
}
