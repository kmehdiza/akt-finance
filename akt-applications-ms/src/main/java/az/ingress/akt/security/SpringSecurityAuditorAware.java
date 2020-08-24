package az.ingress.akt.security;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private final SecurityUtils securityUtils;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(securityUtils.getCurrentUserLogin().orElse("system"));
    }
}
