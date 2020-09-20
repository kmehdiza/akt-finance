package az.ingress.asan.config;

import az.ingress.asan.jwt.JwtConfigurer;
import az.ingress.common.security.SecurityUtils;
import az.ingress.common.security.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ApplicationProperties applicationProperties;

    @Bean
    public TokenUtils tokenUtils() {
        return new TokenUtils(applicationProperties
                .getSecurity()
                .getSecret());
    }

    @Bean
    public SecurityUtils securityUtils() {
        return new SecurityUtils();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .headers()
                .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' " +
                        "'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' " +
                        "'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
            .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
                .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; " +
                        "camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; " +
                        "fullscreen 'self'; payment 'none'")
            .and()
                .frameOptions()
                .deny()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/management/**").hasAnyAuthority("ROLE_ADMIN")
            .and()
                .apply(securityConfigurerAdapter());
    }

    private JwtConfigurer securityConfigurerAdapter() {
        return new JwtConfigurer(tokenUtils());
    }

}
