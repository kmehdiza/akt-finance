package az.ingress.common.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomSpringSecurityUser extends User {

    private static final long serialVersionUID = 3522416053866116034L;
    private final String tin;

    public CustomSpringSecurityUser(String username, String password,
            Collection<? extends GrantedAuthority> authorities,
            String tin) {
        super(username, password, authorities);
        this.tin = tin;
    }

    public String getTin() {
        return tin;
    }
}
