package org.example.security.config;

import org.example.conf.EncoderConfig;
import org.example.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl userService;

    @Autowired
    @Qualifier("delegatedAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    @Autowired
    @Qualifier("customAccessDeniedHandler")
    CustomAccessDeniedHandler customAccessDeniedHandler;


    public WebSecurityConfig(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/auth").not().fullyAuthenticated()
                    .antMatchers(HttpMethod.DELETE, "/out").fullyAuthenticated()
                    .antMatchers(HttpMethod.POST, "/registration").not().fullyAuthenticated()
                    .antMatchers(HttpMethod.GET, "/external/hibernate").hasAnyRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/external/jdbc").hasAnyRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api/wheather/external").hasAnyRole("USER", "ADMIN")
                    .antMatchers(HttpMethod.GET, "/api/wheather/**").hasAnyRole("USER", "ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/wheather/**").hasAnyRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/wheather/**").hasAnyRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/wheather/**").hasAnyRole("ADMIN")
                    .antMatchers("/h2-console/**").permitAll()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(authEntryPoint)
                    .and()
                    .exceptionHandling()
                    .accessDeniedHandler(customAccessDeniedHandler);
        http.headers().frameOptions().disable();
    }


    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public ServletContextInitializer servletContextInitializer(@Value("${secure.cookie}") boolean secure) {
        return servletContext -> servletContext.getSessionCookieConfig().setSecure(secure);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(EncoderConfig.getPasswordEncoder());
    }
}
