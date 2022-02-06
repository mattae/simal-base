package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class ValueSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @NotNull
    private String value;

    @EqualsAndHashCode.Include
    @NotNull
    private String type;

    @NotNull
    private String display;

    @EqualsAndHashCode.Include
    @NotNull
    private String provider;

    private Boolean active = true;

    private String lang;

    @EntityView(ValueSet.class)
    @CreatableEntityView
    public interface BaseView {
        @IdMapping
        Long getId();

        String getType();

        void setType(String type);

        String getProvider();

        void setProvider(String provider);

        String getValue();

        void setValue(String value);

        String getDisplay();

        void setDisplay(String display);

        Boolean getActive();

        void setActive(Boolean active);

        String getLang();

        void setLang(String lang);

        @PostCreate
        default void init() {
            setActive(true);
        }
    }

    @UpdatableEntityView
    @EntityView(ValueSet.class)
    public interface UpdateView extends BaseView {
        void setId(Long id);
    }
}
