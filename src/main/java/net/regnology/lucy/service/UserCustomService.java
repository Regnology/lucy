package net.regnology.lucy.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.regnology.lucy.config.ApplicationProperties;
import net.regnology.lucy.domain.Authority;
import net.regnology.lucy.domain.User;
import net.regnology.lucy.repository.AuthorityRepository;
import net.regnology.lucy.repository.UserCustomRepository;
import net.regnology.lucy.repository.UserRepository;
import net.regnology.lucy.security.AuthoritiesConstants;
import net.regnology.lucy.service.dto.AdminUserDTO;
import net.regnology.lucy.service.exceptions.EmailAlreadyUsedException;
import net.regnology.lucy.service.exceptions.InvalidEmailException;
import net.regnology.lucy.service.exceptions.UsernameAlreadyUsedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

/**
 * Custom service class for managing users.
 */
@Service
@Transactional
public class UserCustomService extends UserService {

    private final Logger log = LoggerFactory.getLogger(UserCustomService.class);

    private final UserCustomRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    private final ApplicationProperties applicationProperties;

    public UserCustomService(
        UserCustomRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        ApplicationProperties applicationProperties
    ) {
        super(userRepository, passwordEncoder, authorityRepository, cacheManager);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.applicationProperties = applicationProperties;
    }

    public User registerUser(AdminUserDTO userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            String email = userDTO.getEmail().toLowerCase();

            if (this.isEmailDomainInvalid(email)) throw new InvalidEmailException(
                "Only a \"" + applicationProperties.getMail().getAllowedDomain() + "\" email address is allowed!"
            );

            newUser.setEmail(email);
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.READONLY).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }

    /**
     * Validate an email address if the property "application.mail.restrict-to-domain" is true.
     * If the property is false, every email is valid.
     *
     * @param email Email address
     * @return true if the email doesn't match the restricted domain or is empty.
     * False if the property is disabled or the restricted domain does match with the email.
     * <br>
     * <i>Example:<br></i>
     * restrict-to-domain: <b>true</b><br>
     * allowed-domain: <b>@test.com</b><br>
     * Valid email: <b>max.mustermann@test.com</b><br>
     * Invalid email: <b>max.mustermann@fail.com</b>
     */
    public boolean isEmailDomainInvalid(String email) {
        if (applicationProperties.getMail().isRestrictToDomain()) {
            return StringUtils.isBlank(email) || !email.endsWith(applicationProperties.getMail().getAllowedDomain().toLowerCase());
        }

        return false;
    }
}
