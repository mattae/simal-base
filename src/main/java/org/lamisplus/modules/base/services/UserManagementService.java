package org.lamisplus.modules.base.services;


import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserPropertiesService;
import com.foreach.across.modules.user.services.UserService;
import io.github.jhipster.security.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.config.Constants;
import org.lamisplus.modules.base.services.dto.RoleDTO;
import org.lamisplus.modules.base.services.dto.UserDTO;
import org.lamisplus.modules.base.web.errors.EmailAlreadyUsedException;
import org.lamisplus.modules.base.web.errors.InvalidPasswordException;
import org.lamisplus.modules.base.web.errors.UsernameAlreadyUsedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserPropertiesService userPropertiesService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final CurrentSecurityPrincipalProxy currentSecurityPrincipalProxy;

    public static Specification<User> emailMatches(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("email")), StringUtils.lowerCase(email));
    }

    public static Specification<User> notAnonymous() {
        return (root, query, cb) -> cb.notEqual(root.get("username"), Constants.ANONYMOUS_USER);
    }

    public Optional<User> activateRegistration(String key) {

        LOG.debug("Activating user for activation key {}", key);
        return userPropertiesService.getEntityIdsForPropertyValue("activationKey", key)
            .stream()
            .findFirst()
            .flatMap(id -> {
                UserProperties userProperties = userPropertiesService.getProperties(id);
                userProperties.set("activationKey", null);
                userPropertiesService.saveProperties(userProperties);
                userPropertiesService.saveProperties(userProperties);
                return userRepository.findById(id);
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        LOG.debug("Reset user password for reset key {}", key);
        return userPropertiesService.getEntityIdsForPropertyValue("activationKey", key)
            .stream()
            .findFirst()
            .filter(id -> {
                UserProperties userProperties = userPropertiesService.getProperties(id);
                Instant resetDate = userProperties.getValue("resetDate");
                return resetDate.isAfter(Instant.now().minusSeconds(86400));
            })
            .map(id -> {
                UserProperties userProperties = userPropertiesService.getProperties(id);
                userProperties.set("resetDate", null);
                userPropertiesService.saveProperties(userProperties);
                userPropertiesService.saveProperties(userProperties);
                return userRepository.getOne(id);
            })
            .map(user -> {
                user.setPassword(newPassword);
                user = userService.save(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findByEmail(mail)
            .filter(user -> !user.hasRestrictions())
            .map(user -> {
                UserProperties userProperties = userPropertiesService.getProperties(user.getId());
                userProperties.set("resetDate", Instant.now());
                userProperties.set("resetKey", RandomUtil.generateResetKey());
                userPropertiesService.saveProperties(userProperties);
                return user;
            });
    }

    public User registerUser(UserDTO userDTO, String password) {
        userRepository.findByUsername(userDTO.getUsername().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });
        userRepository.findOne(emailMatches(userDTO.getEmail())).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(password);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setRestrictions(Arrays.asList(UserRestriction.REQUIRES_CONFIRMATION, UserRestriction.LOCKED));
        newUser = userService.save(newUser);
        UserProperties userProperties = userPropertiesService.getProperties(newUser.getId());
        userProperties.set("avatar", userDTO.getAvatar());
        userProperties.set("activationKey", RandomUtil.generateActivationKey());
        userPropertiesService.saveProperties(userProperties);
        LOG.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.hasRestrictions()) {
            return false;
        }
        userService.delete(existingUser.getId());
        return true;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setRoles(userDTO.getRoles().stream()
            .map(RoleDTO::role)
            .collect(Collectors.toSet()));
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setPassword(RandomUtil.generatePassword());
        user = userService.save(user);
        UserProperties userProperties = userPropertiesService.getProperties(user.getId());
        userProperties.set("avatar", userDTO.getAvatar());
        userProperties.set("resetKey", RandomUtil.generateActivationKey());
        userProperties.set("resetDate", Instant.now());
        userPropertiesService.saveProperties(userProperties);
        LOG.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param avatar    image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String avatar) {
        Optional.of(currentSecurityPrincipalProxy.getPrincipalName())
            .flatMap(userRepository::findByUsername)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                userService.save(user);
                UserProperties userProperties = userPropertiesService.getProperties(user.getId());
                userProperties.set("avatar", avatar);
                userPropertiesService.saveProperties(userProperties);
                LOG.debug("Changed Information for User: {}", user);
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setUsername(userDTO.getUsername().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                userService.save(user);
                UserProperties userProperties = userPropertiesService.getProperties(user.getId());
                userProperties.set("avatar", userDTO.getAvatar());
                if (!userDTO.isActivated()) {
                    user.setRestrictions(Collections.singleton(UserRestriction.LOCKED));
                }
                LOG.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository.findByUsername(login).ifPresent(user -> {
            userService.delete(user.getId());
            LOG.debug("Deleted User: {}", user);
        });
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        Optional.of(currentSecurityPrincipalProxy.getPrincipalName())
            .flatMap(userRepository::findByUsername)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                user.setPassword(newPassword);
                userService.save(user);
                LOG.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(notAnonymous(), pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserWithAuthoritiesByUsername(String username) {
        return userRepository.findByUsername(username).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return Optional.of(currentSecurityPrincipalProxy.getPrincipalName())
            .flatMap(userRepository::findByUsername);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository.findAll()
            .stream()
            .filter(user -> {
                boolean activated = user.isAccountNonLocked();
                UserProperties properties = userPropertiesService.getProperties(user.getId());
                String activationKey = properties.getValue("activationKey");
                Date createdDate = user.getCreatedDate();
                return !activated && activationKey != null && createdDate.toInstant()
                    .isBefore(Instant.now().minus(3, ChronoUnit.DAYS));
            })
            .forEach(user -> {
                LOG.debug("Deleting not activated user {}", user.getUsername());
                userService.delete(user.getId());
            });
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    public Collection<Role> getAuthorities() {
        return roleService.getRoles();
    }
}
