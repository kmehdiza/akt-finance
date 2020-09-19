package az.ingress.user.management.security;

import az.ingress.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private final SecurityUtils securityUtils;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(securityUtils.getCurrentUserLogin().orElse("system"));
    }
}
