package org.bbr.examples.service.system.io.db.dto.customer;

import org.bbr.examples.service.business.model.customer.Customers;
import org.bbr.examples.service.system.io.db.dto.Entry;
import org.bbr.examples.service.system.io.db.dto.account.Account;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "CLIENT")
@SequenceGenerator(name = "CLIENT_SEQ", initialValue = 1, allocationSize = 50)
public class Client implements Entry {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENT_SEQ")
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Customers type;

    @OneToMany(mappedBy = "client")
    private Set<Account> accounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customers getType() {
        return type;
    }

    public void setType(Customers type) {
        this.type = type;
    }
}
