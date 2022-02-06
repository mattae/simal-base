package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Where(clause = "archived = false")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "party_addresses")
@SQLDelete(sql = "update party_addresses set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
public class Address {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    UUID id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @NotNull
    private String line1;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String line2;

    @EqualsAndHashCode.Include
    @ToString.Include
    @NotNull
    private String city;

    @EqualsAndHashCode.Include
    @ToString.Include
    @NotNull
    private String state;

    @ToString.Include
    private String postalCode;

    @EqualsAndHashCode.Include
    private String addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Party party;

    private Boolean archived = false;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }

    @EntityView(Address.class)
    public interface View {
        @IdMapping
        UUID getId();

        String getLine1();

        String getLine2();

        String getCity();

        String getState();

        String getPostalCode();

        String getAddressType();
    }
}
