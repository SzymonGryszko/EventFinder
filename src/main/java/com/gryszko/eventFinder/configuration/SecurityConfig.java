package com.gryszko.eventFinder.configuration;

import com.gryszko.eventFinder.security.JwtAuthenticationFilter;

import com.gryszko.eventFinder.security.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.gryszko.eventFinder.security.UserRole.*;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("api/auth/**").permitAll()
                .antMatchers("/api/admin/**").hasRole(ADMIN.toString())
                .antMatchers("/api/admin/").hasRole(ADMIN.toString())
                .antMatchers(HttpMethod.POST, "/api/events/**").hasAnyRole(ORGANIZER.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.PUT, "/api/events/**").hasAnyRole(ORGANIZER.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.DELETE, "/api/events/**").hasAnyRole(ORGANIZER.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/comments/**").permitAll()
                .antMatchers(HttpMethod.POST,"/api/comments/**").authenticated()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/auth/**");
    }
}
