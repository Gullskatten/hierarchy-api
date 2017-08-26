package com.hierarchy.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "val_secret_attempts")
@NamedQueries(value = {
        @NamedQuery(name = SecretValidationAttempt.FIND_ALL, query = "select u from SecretValidationAttempt u"),
        @NamedQuery(name = SecretValidationAttempt.FIND_BY_ID, query = "select u from SecretValidationAttempt u where u.valId = :valId"),
        @NamedQuery(name = SecretValidationAttempt.FIND_BY_USER_ID, query = "select u from SecretValidationAttempt u where u.userId = :userId")
})
public class SecretValidationAttempt {

    public static final String FIND_ALL = "SecretValidationAttempt.findAll";
    public static final String FIND_BY_ID = "SecretValidationAttempt.findById";
    public static final String FIND_BY_USER_ID = "SecretValidationAttempt.findByUserId";

    private int valId;
    private int amount;
    private int userId;
    private Timestamp lastAttempt;

    public SecretValidationAttempt() {
    }

    public SecretValidationAttempt(int valId, int amount, int userId, Timestamp lastAttempt) {
        this.valId = valId;
        this.amount = amount;
        this.userId = userId;
        this.lastAttempt = lastAttempt;
    }


    @Id
    @JsonProperty
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "val_id")
    public int getValId() {
        return valId;
    }

    public void setValId(int valId) {
        this.valId = valId;
    }

    @JsonProperty
    @Column(name = "amount")
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonProperty
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    @Column(name = "last_attempt")
    public Timestamp getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(Timestamp lastAttempt) {
        this.lastAttempt = lastAttempt;
    }
}
