package org.facturepro.backoffice.auth.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.facturepro.backoffice.auth.domain.enums.Role;
import org.facturepro.backoffice.shared.domain.valueObjects.Email;
import org.facturepro.backoffice.shared.infrastructure.audit.AuditEntity;

import java.util.UUID;

/**
 * Entité utilisateur — appartient à un tenant (entreprise).
 * L'entité encapsule la logique de changement de rôle et de désactivation.
 */
@Getter
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = {"email_value", "tenant_id"})
)
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email_value", nullable = false))
    private Email email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active;

    // Constructeur JPA
    protected User() {}

    private User(
        final UUID tenantId,
        final Email email,
        final String passwordHash,
        final String firstName,
        final String lastName,
        final Role role
    ) {
        this.setTenantId(tenantId);
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.active = true;
    }

    public static User create(
        final UUID tenantId,
        final Email email,
        final String passwordHash,
        final String firstName,
        final String lastName,
        final Role role
    ) {
        return new User(tenantId, email, passwordHash, firstName, lastName, role);
    }

    // ── Comportements métier ──────────────────────────────────────────────

    public void changeRole(final Role newRole) {
        this.role = newRole;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}