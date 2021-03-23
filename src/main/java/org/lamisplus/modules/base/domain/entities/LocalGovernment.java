package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "local_government")
@EqualsAndHashCode(of = "id")
public class LocalGovernment implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "name")
    private String name;

    @JoinColumn(name = "state_id")
    @ManyToOne
    private State state;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
