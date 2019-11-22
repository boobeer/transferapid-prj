package org.bbr.examples.service.event;

import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.system.Task;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.service.system.web.Response;

public interface Event {

    Task fire(Operations operations);

    void process(double commission, Store store);

    Response result(Response response);
}
