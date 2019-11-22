package org.bbr.examples.service.business.model.customer;

class Corporate extends CustomerAccount {

    @Override
    public double getCommission() {
        return 7;
    }

}
