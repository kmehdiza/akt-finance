package az.ingress.akt.client;

import org.springframework.stereotype.Component;

@Component
public class UserManagementClientImpl implements UserManagementClient {
    @Override
    public boolean isUserActive(String username) {
        return true;
    }
}
