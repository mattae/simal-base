package com.mattae.simal.modules.base.services.dto;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.services.UserPropertiesService;
import lombok.Data;
import com.mattae.simal.modules.base.config.Constants;
import com.mattae.simal.modules.base.config.ContextProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
public class UserDTO {

    private Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String username;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    private String displayName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String avatar;

    private boolean activated = false;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private Set<String> authorities;

    private Collection<RoleDTO> roles;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        BeanUtils.copyProperties(user.toDto(), this, "roles");
        UserPropertiesService userPropertiesService = ContextProvider.getBean(UserPropertiesService.class);
        UserProperties properties = userPropertiesService.getProperties(user.getId());
        this.avatar = properties.getValue("avatar");
        activated = user.isEnabled();
        authorities = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }

    public User user() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
