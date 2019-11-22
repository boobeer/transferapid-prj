package org.bbr.examples.service.system.io.db.dto.transfer;

import org.bbr.examples.service.business.model.transfer.PaymentStatus;
import org.bbr.examples.service.business.model.transfer.Transfers;
import org.bbr.examples.service.system.io.db.dto.Entry;

import javax.persistence.*;

@Entity(name = "PAYMENT")
@SequenceGenerator(name = "PAYMENT_SEQ", initialValue = 1, allocationSize = 50)
public class Payment implements Entry {

    public static final String TRANSFER_BY_ACCOUNTS =
            " FROM PAYMENT WHERE " +
            " SENDER_ACCOUNT_ID = :senderAccountId " +
            " AND " +
            " RECIPIENT_ACCOUNT_ID = :recipientAccountId ";

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT_SEQ")
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Transfers type;
    @Column(name = "SENDER_ACCOUNT_ID")
    private Long senderAccountId;
    @Column(name = "RECIPIENT_ACCOUNT_ID")
    private Long recipientAccountId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private Double total;
    private Double commission;

    public void setId(Long id) {
        this.id = id;
    }

    public Transfers getType() {
        return type;
    }

    public Long getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(Long senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public Long getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Long recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getCommission() {
        return commission;
    }

    public void setType(Transfers type) {
        this.type = type;
    }
}
