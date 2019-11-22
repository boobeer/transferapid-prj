package org.bbr.examples.service.business.model.customer;

import org.bbr.examples.service.business.model.account.AccountType;
import org.bbr.examples.service.business.model.account.Accounts;
import org.bbr.examples.service.system.io.db.dto.account.Account;
import org.bbr.examples.service.system.io.db.dto.customer.Client;


public class CustomerAccount {

    private Client client;
    private Account account;

    public Customers getCustomerType() {
        return client.getType();
    }

    public Accounts getAccountType() {
        return account.getType();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    private CustomerAccount getCustomerTypeModel() {
        return getCustomerType().factoryMathod.get();
    }

    private AccountType getAccountTypeModel() {
        return getAccountType().factoryMathod.get();
    }

    public double getCommission() {
        return 0;
    }

    public double getCompositeCommission() {
        return getCommission() + getAccountTypeModel().getCommission() + getCustomerTypeModel().getCommission();
    }
}
