package org.divorobioff.nevis.assignment.entity;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.*;

import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "country_of_residence")
    private String countryOfResidence;

    @Column(name = "email_domain", nullable = false)
    private String emailDomain;

    @PrePersist
    @PreUpdate
    private void computeEmailDomain() {
        if (email == null) {
            emailDomain = null;
            return;
        }
        int atIndex = email.indexOf('@');
        if (atIndex < 0 || atIndex == email.length() - 1) {
            emailDomain = null;
        } else {
            emailDomain = email.substring(atIndex + 1).toLowerCase(Locale.ROOT);
        }
    }

    public ClientEntity() {}

    public ClientEntity(String firstName, String lastName, String email, String countryOfResidence) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.countryOfResidence = countryOfResidence;
    }

    public UUID getId() {
        return id;
    }

    @VisibleForTesting
    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public String getEmailDomain() {
        return emailDomain;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientEntity that)) return false;
        return id != null && id.equals(that.id);
    }
}
