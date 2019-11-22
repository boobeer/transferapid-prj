package org.bbr.examples.service.business.model.account;

class Card extends AccountType {

    @Override
    public double getCommission() {
        return 3;
    }
}
