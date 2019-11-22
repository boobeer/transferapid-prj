package org.bbr.examples.service.business;

import org.bbr.examples.error.TransferException;
import org.bbr.examples.service.event.Event;

public interface TransferService {

    void process(Event send) throws TransferException;

}
