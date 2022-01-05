package com.mattae.simal.modules.base.domain.entities;

import com.foreach.across.modules.hibernate.business.AuditableEntity;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@SQLDelete(sql = "update individual_name set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
@Data
@EqualsAndHashCode(of = "id")
public class IndividualName {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private Individual individual;

    @Column(name = "type", nullable = false)
    private String type;

    @Embedded
    private Name name;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private Boolean archived = false;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }
}
