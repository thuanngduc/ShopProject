package com.shop.security;

import com.shop.security.oauth.CustomerOAuth2UserService;
import com.shop.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Autowired private CustomerOAuth2UserService oAuth2UserService;
    @Autowired private OAuth2LoginSuccessHandler oAuth2LoginHandler;
    @Autowired private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
    @Bean
    public UserDetailsService userDetailsService()
    {
        return new CustomerUserDetailsService();
    }
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/account_details", "/update_account_details", "/orders/**","/cart", "/address_book/**", "/checkout", "/place_order", "/process_paypal_order").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .successHandler(databaseLoginSuccessHandler)
                    .permitAll()
                .and()
                .oauth2Login()
                    .loginPage("/login")
                    .userInfoEndpoint()
                    .userService(oAuth2UserService)
                    .and()
                    .successHandler(oAuth2LoginHandler)
                .and()
                .logout().permitAll()
                .and()
                    .rememberMe()
                        .key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }

}
