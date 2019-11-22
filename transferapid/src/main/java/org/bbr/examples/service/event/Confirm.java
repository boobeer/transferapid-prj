package org.bbr.examples.service.event;

import org.bbr.examples.service.business.model.transfer.PaymentStatus;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.system.StoreTask;
import org.bbr.examples.service.system.Task;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.service.system.io.db.dto.transfer.Payment;
import org.bbr.examples.service.system.web.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Confirm implements Event {

    private final Long paymentId;
    private TransferMethod transfer;

    public Confirm(Long paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public Task fire(Operations operations) {
        buildTransfer();
        List<Consumer<Store>> actions = new LinkedList<>();
        actions.add(this::work);
        return new StoreTask(actions);
    }

    @Override
    public void process(double commission, Store store) {
        Payment payment = transfer.getPayment();
        payment.setStatus(PaymentStatus.Approved);
        store.save(payment);
    }

    @Override
    public Response result(Response response) {
        response.setTransfer(transfer);
        return response;
    }

    private void buildTransfer() {
        transfer = new TransferMethod();
        Payment payment = new Payment();
        payment.setId(paymentId);
        transfer.setPayment(payment);
    }

    private void work(Store store) {
        process(0, store);
    }
}
