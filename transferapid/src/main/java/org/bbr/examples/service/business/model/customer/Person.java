package org.bbr.examples.service.business.model.customer;

class Person extends CustomerAccount {

    @Override
    public double getCommission() {
        return 3;
    }

}
