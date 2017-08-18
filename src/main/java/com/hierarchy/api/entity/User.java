package com.hierarchy.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users")
@NamedQueries(value = {
        @NamedQuery(name = User.FIND_ALL, query = "select u from User u"),
        @NamedQuery(name = User.FIND_BY_HASH, query = "select u from User u where u.token = :hash"),
        @NamedQuery(name = User.FIND_CHILDREN, query = "select u from User u where u.referralToken = :hash")
})
public class User {

    public static final String FIND_ALL = "Users.findAll";
    public static final String FIND_BY_HASH = "Users.findParent";
    public static final String FIND_CHILDREN = "Users.findChildren";

    private int userId;
    private String firstName;
    private String lastName;
    private String country;
    private String username;
    private String email;
    private String token;
    private String referralToken;

    public User() {
    }

    public User(int userId, String firstName, String lastName, String country, String username, String email, String token, String referralToken) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.username = username;
        this.email = email;
        this.token = token;
        this.referralToken = referralToken;
    }

    @Id
    @JsonProperty
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty
    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty
    @Column(name = "country")
    public String getCountry() {
        return country;
    }

    @NotBlank
    @JsonProperty
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    @JsonProperty
    @Email(message = "Unrecognized e-mail.")
    public String getEmail() {
        return email;
    }

    @JsonProperty
    @Column(name = "hash")
    public String getToken() {
        return token;
    }

    @NotBlank
    @JsonProperty
    @Column(name = "referral_hash")
    public String getReferralToken() {
        return referralToken;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setReferralToken(String referralToken) {
        this.referralToken = referralToken;
    }
}
