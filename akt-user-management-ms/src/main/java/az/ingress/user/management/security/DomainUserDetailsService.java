package az.ingress.user.management.security;

import az.ingress.common.security.CustomSpringSecurityUser;
import az.ingress.user.management.domain.User;
import az.ingress.user.management.domain.enumeration.ProfileStatus;
import az.ingress.user.management.repository.UserRepository;
import az.ingress.user.management.web.rest.errors.UserIsNotActiveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {

    private static final String IS_NOT_ACTIVE_MESSAGE = "User is not active!";

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Authenticating {}", username);

        String lowercaseUsername = username.toLowerCase(Locale.ENGLISH);
        return userRepository.findOneWithAuthoritiesAndPartnerByUsername(lowercaseUsername)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User " + lowercaseUsername + " was not found in the database"));
    }

    private CustomSpringSecurityUser createSpringSecurityUser(User user) {
        checkUserProfileStatus(user);
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new CustomSpringSecurityUser(user.getUsername(),
                user.getPassword(),
                grantedAuthorities,
                user.getPartner().getTin());
    }

    private void checkUserProfileStatus(User user) throws UserIsNotActiveException {
        if (user.getStatus() != ProfileStatus.ACTIVE) {
            throw new UserIsNotActiveException(IS_NOT_ACTIVE_MESSAGE);
        }
    }
}
