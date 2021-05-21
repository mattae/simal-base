package com.mattae.simal.modules.base.domain.entities;

import com.mattae.simal.modules.base.domain.enumeration.LocationType;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Location {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "SequenceLocation")
    @SequenceGenerator(
        name = "SequenceLocation",
        allocationSize = 1
    )
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LocationType type = LocationType.ADDRESS;

    @Builder.Default
    private String displayName = "";

    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date toDate;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof Location))
            return false;

        Location other = (Location) o;

        return id != 0L && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
