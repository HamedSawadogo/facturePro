package org.facturepro.backoffice.client.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.facturepro.backoffice.client.domain.enums.ClientType;
import org.facturepro.backoffice.shared.domain.valueObjects.Email;
import org.facturepro.backoffice.shared.domain.valueObjects.PhoneNumber;
import org.facturepro.backoffice.shared.infrastructure.audit.AuditEntity;

import java.util.UUID;

/**
 * Entité Client — CRM simple FacturePro.
 * Encapsule les règles de mise à jour (seuls les champs autorisés sont modifiables).
 */
@Getter
@Entity
@Table(
    name = "clients",
    indexes = {
        @Index(name = "idx_clients_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_clients_email", columnList = "email_value, tenant_id")
    }
)
public class Client extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email_value"))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_value"))
    private PhoneNumber phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected Client() {}

    private Client(
        final UUID tenantId,
        final String name,
        final Email email,
        final PhoneNumber phone,
        final ClientType clientType,
        final String address,
        final String city,
        final String country,
        final String taxNumber
    ) {
        this.setTenantId(tenantId);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.clientType = clientType;
        this.address = address;
        this.city = city;
        this.country = country;
        this.taxNumber = taxNumber;
        this.active = true;
    }

    public static Client create(
        final UUID tenantId,
        final String name,
        final Email email,
        final PhoneNumber phone,
        final ClientType clientType,
        final String address,
        final String city,
        final String country,
        final String taxNumber
    ) {
        return new Client(tenantId, name, email, phone, clientType, address, city, country, taxNumber);
    }

    // ── Comportements métier ──────────────────────────────────────────────

    public void update(
        final String name,
        final Email email,
        final PhoneNumber phone,
        final String address,
        final String city,
        final String country,
        final String taxNumber
    ) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.taxNumber = taxNumber;
    }

    public void archive() {
        this.active = false;
    }
}