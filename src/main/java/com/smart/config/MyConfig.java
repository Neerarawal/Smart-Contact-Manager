package com.smart.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {

    // ✅ Security Filter Chain (Handles Authentication & Authorization)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN") // Protect admin endpoints
                .requestMatchers("/user/**").hasRole("USER")   // Protect user endpoints
                .requestMatchers("/change-password").permitAll() 
                .requestMatchers("/**").permitAll()           // Allow public access to other endpoints
                .anyRequest().permitAll()               // Require authentication for any other request
            )
            .formLogin(form -> form
            	    .loginPage("/signin")
            	    .defaultSuccessUrl("/dologin")
            	    .failureUrl("/signin?error=true") // Wrong credentials message
            	    .defaultSuccessUrl("/user/index", true)
            	    .permitAll()
            	)
            	.logout(logout -> logout
            	    .logoutUrl("/logout")  // Default logout endpoint
            	    .logoutSuccessUrl("/signin?logout=true") // Redirect with a message
            	    .invalidateHttpSession(true)
            	    .deleteCookies("JSESSIONID") // Optional: Clear session cookies
            	    .permitAll()
            	)

            .csrf(csrf -> csrf.disable()); // Disable CSRF (Optional - Enable for better security)

        return http.build();
    }

    // ✅ UserDetailsService Bean
    @Bean
    public UserDetailsService getUserDetailService() {
        return new UserDetailsServiceImpl(); // Ensure UserDetailsServiceImpl is implemented correctly
    }

    // ✅ Password Encoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Define Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // ✅ Define AuthenticationManager (Required in Spring Security 5.7+)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig, DaoAuthenticationProvider authProvider) throws Exception {
        return new ProviderManager(List.of(authProvider));
    }
}