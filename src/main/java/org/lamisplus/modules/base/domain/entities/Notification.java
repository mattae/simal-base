package org.lamisplus.modules.base.domain.entities;

import com.foreach.across.modules.user.business.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
public class Notification implements Serializable, Persistable<Long> {
    private final static String[] COLORS = {"primary", "accent", "warn"};
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String message;

    @NotNull
    private LocalDateTime time;

    private String route;

    private Boolean read = false;

    private String icon;

    private String color;

    @ManyToOne
    private User user;

    @Override
    public boolean isNew() {
        return false;
    }

    @PostLoad
    public void postLoad() {
        if (Arrays.stream(COLORS).noneMatch(c -> c.equals(color))) {
            color = "primary";
        }
    }
}
