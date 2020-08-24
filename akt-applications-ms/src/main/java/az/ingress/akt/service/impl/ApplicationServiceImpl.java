package az.ingress.akt.service.impl;

import az.ingress.akt.client.UserManagementClient;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.ApplicationService;
import az.ingress.akt.web.rest.errors.UserIsNotActiveException;
import az.ingress.akt.web.rest.errors.UsernameIsNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final SecurityUtils securityUtils;
    private final UserManagementClient userManagementClient;

    @Override
    public String createApplication() {
        log.debug("Request to create new application");
        String username = securityUtils.getCurrentUserLogin().orElseThrow(UsernameIsNotFoundException::new);
        if (!userManagementClient.isUserActive(username)) {
            throw new UserIsNotActiveException();
        }
        return username;
    }

}
