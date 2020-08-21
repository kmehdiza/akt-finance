package az.ingress.user.management.service.impl;

import az.ingress.user.management.domain.Authority;
import az.ingress.user.management.domain.User;
import az.ingress.user.management.repository.UserRepository;
import az.ingress.user.management.security.AuthoritiesConstants;
import az.ingress.user.management.service.UserService;
import az.ingress.user.management.web.rest.errors.UsernameAlreadyUsedException;
import az.ingress.user.management.web.rest.vm.ManagedUserVm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createUser(ManagedUserVm userVm) {
        userRepository.findUserByUsernameIgnoreCase(userVm.getUsername())
                .ifPresent(user -> {
                    throw new UsernameAlreadyUsedException();
                });
        User user = createUserEntityObject(userVm, createAuthorities(AuthoritiesConstants.USER));
        userRepository.save(user);
    }

    private Set<Authority> createAuthorities(String... authoritiesString) {
        Set<Authority> authorities = new HashSet<>();
        for (String authorityString : authoritiesString) {
            authorities.add(new Authority(authorityString));
        }
        return authorities;
    }

    private User createUserEntityObject(ManagedUserVm userVm, Set<Authority> authorities) {
        User user = new User();
        user.setUsername(userVm.getUsername());
        user.setPassword(passwordEncoder.encode(userVm.getPassword()));
        user.setAuthorities(authorities);
        return user;
    }

}
