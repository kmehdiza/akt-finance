package az.ingress.user.management.security;

import az.ingress.user.management.domain.Authority;
import az.ingress.user.management.domain.Partner;
import az.ingress.user.management.domain.User;
import az.ingress.user.management.domain.enumeration.ProfileStatus;
import az.ingress.user.management.repository.UserRepository;
import az.ingress.user.management.web.rest.errors.UserIsNotActiveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomainUserDetailsServiceTest {

    private static final String USERNAME = "username";

    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DomainUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(USERNAME);
        user.setPartner(
                Partner.builder()
                        .tin("tin")
                        .build());
        user.setPassword("password");
        user.setAuthorities(
                Set.of(new Authority("authority")));
    }

    @Test
    void givenNotExistedUsername_WhenAuthenticate_ThenThrowUserNotFoundException() {
        //Arrange
        when(userRepository.findOneWithAuthoritiesAndPartnerByUsername(USERNAME))
                .thenReturn(Optional.empty());

        //Assert and Act
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userDetailsService.loadUserByUsername(USERNAME));

        verify(userRepository).findOneWithAuthoritiesAndPartnerByUsername(USERNAME);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenNotActiveUsername_WhenAuthenticate_ThenThrowUserIsNotActiveException() {
        //Arrange
        user.setStatus(ProfileStatus.BLOCKED);
        when(userRepository.findOneWithAuthoritiesAndPartnerByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        //Assert and Act
        assertThatExceptionOfType(UserIsNotActiveException.class)
                .isThrownBy(() -> userDetailsService.loadUserByUsername(USERNAME));

        verify(userRepository).findOneWithAuthoritiesAndPartnerByUsername(USERNAME);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenActiveUsername_WhenAuthenticate_ThenReturnsUserDetails() {
        //Arrange
        user.setStatus(ProfileStatus.ACTIVE);
        when(userRepository.findOneWithAuthoritiesAndPartnerByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        //Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(USERNAME);

        //Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(userDetails.getAuthorities())
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(user.getAuthorities());

        verify(userRepository).findOneWithAuthoritiesAndPartnerByUsername(USERNAME);
        verifyNoMoreInteractions(userRepository);
    }
}
