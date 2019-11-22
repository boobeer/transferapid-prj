package org.bbr.examples.service.business.model.account;

class Crypto extends AccountType {

    @Override
    public double getCommission() {
        return 1;
    }
}
