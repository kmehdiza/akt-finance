package az.ingress.user.management.security.jwt;

import az.ingress.common.security.CustomSpringSecurityUser;
import az.ingress.common.security.TokenUtils;
import az.ingress.user.management.config.ApplicationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider extends TokenUtils {

    private Key key;

    private long tokenValidityInMilliseconds;

    private long tokenValidityInMillisecondsForRememberMe;

    private final ApplicationProperties applicationProperties;

    public TokenProvider(ApplicationProperties applicationProperties) {
        super(applicationProperties.getSecurity().getSecret());
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes;
        String secret = applicationProperties.getSecurity().getSecret();
        keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds =
                applicationProperties.getSecurity().getTokenValidityInSeconds() * 1000;
        this.tokenValidityInMillisecondsForRememberMe =
                applicationProperties.getSecurity().getTokenValidityInSecondsForRememberMe() * 1000;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String partnerTin = null;
        if (authentication.getPrincipal() instanceof CustomSpringSecurityUser) {
            partnerTin = ((CustomSpringSecurityUser) authentication.getPrincipal()).getTin();
        }
        long now = (new Date()).getTime();
        Date valid = getExpirationDate(rememberMe, now);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(PARTNER_KEY, partnerTin)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(valid)
                .compact();
    }

    private Date getExpirationDate(boolean rememberMe, long now) {
        if (rememberMe) {
            return new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            return new Date(now + this.tokenValidityInMilliseconds);
        }
    }
}
