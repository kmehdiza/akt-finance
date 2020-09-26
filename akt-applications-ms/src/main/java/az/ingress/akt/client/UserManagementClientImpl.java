package az.ingress.akt.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserManagementClientImpl implements UserManagementClient {

    @Override
    public void checkIfUserActive(String username) {
        log.trace(username);
    }
}
