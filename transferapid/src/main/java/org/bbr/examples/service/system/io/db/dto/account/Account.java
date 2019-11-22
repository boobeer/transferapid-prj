package org.bbr.examples.service.system.io.db.dto.account;

import org.bbr.examples.service.business.model.Currency;
import org.bbr.examples.service.business.model.account.Accounts;
import org.bbr.examples.service.system.io.db.dto.Entry;
import org.bbr.examples.service.system.io.db.dto.customer.Client;

import javax.persistence.*;

@Entity(name = "ACCOUNT")
@SequenceGenerator(name = "ACCOUNT_SEQ", initialValue = 1, allocationSize = 50)
public class Account implements Entry {

    public static final String ACCOUNT_BY_ID = "FROM ACCOUNT WHERE ID = :id ";

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_SEQ")
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Accounts type;
    @ManyToOne
    @JoinColumn(name = "CLIENT_ID", nullable = false, updatable = false)
    private Client client;
    private Double total;
    private Double totalBlocked;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public Accounts getType() {
        return type;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTotalBlocked() {
        return totalBlocked;
    }

    public void setTotalBlocked(Double totalBlocked) {
        this.totalBlocked = totalBlocked;
    }

    public void setType(Accounts type) {
        this.type = type;
    }

    @Transient
    public double getAvailableAmount() {
        return Currency.getValue(total).subtract(Currency.getValue(totalBlocked)).doubleValue();
    }

}
