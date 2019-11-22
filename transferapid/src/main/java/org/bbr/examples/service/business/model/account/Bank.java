package org.bbr.examples.service.business.model.account;

class Bank extends AccountType {

    @Override
    public double getCommission() {
        return 2;
    }
}
