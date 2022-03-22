package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.MappingSingular;
import com.blazebit.persistence.view.UpdatableEntityView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    @EqualsAndHashCode.Include
    private String category;

    @Column(name = "_order")
    private Integer order;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Set<Data> data;

    @ManyToOne
    private Module module;

    @EntityView(Configuration.class)
    @UpdatableEntityView
    public interface View {
        @IdMapping
        @NotNull
        Long getId();

        void setId(Long id);

        @NotNull
        String getCategory();

        void setCategory(String category);

        Integer getOrder();

        void setOrder(Integer order);

        @NotEmpty
        @MappingSingular
        Set<Data> getData();

        void setData(Set<Data> configurations);
    }

    @Getter
    @Setter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString
    public static class Data {
        @NotNull
        @EqualsAndHashCode.Include
        private String key;

        @NotNull
        private String value;

        @NotNull
        private Type type;

        public enum Type {
            string, numeric, bool, date
        }
    }
}
