package com.hierarchy.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@NamedQueries(value = {
        @NamedQuery(name = User.FIND_ALL, query = "select u from User u"),
        @NamedQuery(name = User.FIND_BY_ID, query = "select u from User u where u.userId = :userId"),
        @NamedQuery(name = User.FIND_BY_HASH, query = "select u from User u where u.token = :token"),
        @NamedQuery(name = User.FIND_CHILDREN_OF_HASH, query = "select u from User u where u.referralToken = :token"),
        @NamedQuery(name = User.VALIDATE_TOKEN_AND_SECRET, query = "select u.username from User u where u.token = :token and u.secret = :secret")
})
public class User {

    public static final String FIND_ALL = "Users.findAll";
    public static final String FIND_BY_ID = "Users.findById";
    public static final String FIND_CHILDREN_OF_HASH = "Users.findChildrenOfHash";
    public static final String FIND_BY_HASH = "Users.findByHash";
    public static final String VALIDATE_TOKEN_AND_SECRET = "Users.findUserNameByHashAndSecret";

    private int userId;
    private String firstName;
    private String lastName;
    private String country;
    private String username;
    private String email;
    private String token;
    private Timestamp lockedUntil;
    private boolean isLocked;

    @Transient
    private transient String referralToken;
    @Transient
    private transient String secret;

    public User() {
    }

    public User(int userId, String firstName, String lastName, String country, String username, String email, String token, String referralToken,
                String secret) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.username = username;
        this.email = email;
        this.token = token;
        this.referralToken = referralToken;
        this.secret = secret;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @JsonProperty
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    @JsonProperty
    @Email(message = "Unrecognized e-mail.")
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    @JsonProperty
    @Column(name = "hash")
    public String getToken() {
        return token;
    }

    @JsonProperty
    @Column(name = "referral_hash")
    public String getReferralToken() {
        return referralToken;
    }

    @JsonProperty
    @Column(name = "is_locked")
    public boolean isLocked() {
        return isLocked;
    }

    @JsonProperty
    @Column(name = "locked_until")
    public Timestamp getLockedUntil() {
        return lockedUntil;
    }

    @JsonProperty
    @Column(name = "secret")
    public String getSecret() {
        return secret;
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

    public void setSecret(String secret) { this.secret = secret; }

    public void setLockedUntil(Timestamp lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
